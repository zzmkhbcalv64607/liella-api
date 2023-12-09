package com.liella.project.service.impl;

import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liella.liellacommon.model.dto.MailDTO;
import com.liella.liellacommon.model.entity.User;
import com.liella.project.common.BaseResponse;
import com.liella.project.constant.UserConstant;
import com.liella.project.common.ErrorCode;
import com.liella.project.exception.BusinessException;
import com.liella.project.mapper.UserMapper;
import com.liella.project.service.EmailService;
import com.liella.project.service.RedisService;
import com.liella.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 用户服务实现类
 *
 * @author liella
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    private static String CAPTCHA_CACHE_KEY = "captcha:";
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "liella";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword,String userName) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 用户需要知道自己的 ak sk
            // 如何知晓呢
            // 3. 分配ak sk
            String accessKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(8));
            // 3. 插入数据
            User user = new User();
            user.setUserName(userName);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        return user;
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && UserConstant.ADMIN_ROLE.equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    /**
     * 用户邮箱注册
     * @param userAccount
     * @param code
     * @param userName
     * @return
     */
    @Override
    public long userEmailRegister(String userAccount, String code, String userName) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, code, userName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        String RCode = redisTemplate.opsForValue().get(CODE_KEY + userAccount);
        log.info("redis中的验证码为：{}",RCode,code);
        if (RCode==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码过期");
        }
        log.info("验证码为：{}",code);
        if (!code.equals(RCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        }

        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密(默认密码)
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userAccount).getBytes());
            // 用户需要知道自己的 ak sk
            // 如何知晓呢
            // 3. 分配ak sk
            String accessKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(8));
            // 3. 插入数据
            User user = new User();
            user.setUserName(userName);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    /**
     * 更改签名
     * @param id
     * @return
     */
    @Override
    public boolean updateAk(Long id) {
        User user = this.getById(id);
        if (user!=null){
            String accessKey = DigestUtil.md5Hex(SALT + user.getUserAccount() + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(SALT + user.getUserAccount() + RandomUtil.randomNumbers(8));
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            return this.updateById(user);
        }
        return false;
    }

    public static final String CAPTCHA = "验证码";
    public static final Integer CODE_EXPIRE_TIME = 2;
    public static final String CODE_KEY = "code:";
    @Autowired
    private RedisService redisService;

    @Autowired
    private EmailService emailService;
    @Override
    public void sendCode(String username) {
        Assert.isTrue(checkEmail(username), "请输入正确的邮箱！");
        RandomGenerator randomGenerator = new RandomGenerator("0123456789", 6);
        String code = randomGenerator.generate();
        MailDTO mailDTO = MailDTO.builder()
                .toEmail(username)
                .subject(CAPTCHA)
                .content("您的验证码为 " + code + " 有效期为" + CODE_EXPIRE_TIME + "分钟")
                .build();
        // 验证码存入消息队列
      //  rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, EMAIL_SIMPLE_KEY, mailDTO);

        // 验证码存入redis
        redisTemplate.opsForValue().set(CODE_KEY + username, code, CODE_EXPIRE_TIME, TimeUnit.MINUTES);
        //redisService.setObject(CODE_KEY + username, code, CODE_EXPIRE_TIME, TimeUnit.MINUTES);
        emailService.sendSimpleMail(mailDTO);
    }



    /**
     * 验证邮箱
     * @param username
     * @return
     */
    public static boolean checkEmail(String username) {
        String rule = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
        //正则表达式的模式 编译正则表达式
        Pattern p = Pattern.compile(rule);
        //正则表达式的匹配器
        Matcher m = p.matcher(username);
        //进行正则匹配
        return m.matches();
    }


    private static final String USER_SIGN_KEY = "user:sign:";


    /**
     * 签到
     * @param userID
     * @return
     */
    @Override
    public String sign(Long userID) {
        if (userID==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        //获取当前时间
        LocalDateTime now = LocalDateTime.now();
        //3. 拼接key
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = USER_SIGN_KEY + userID + keySuffix;
        //4. 判断是否已签到
        Boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey==true){
            return "今日已签到";
        }

        //获取本月是第几天
        int dayOfMonth = now.getDayOfMonth();

        //5. 写入redis setbit key offset 1
        Boolean sign = false;
        try{
           sign = redisTemplate.opsForValue().setBit(key, dayOfMonth - 1, true);
        }catch (Exception e){
            log.error("签到失败",e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"签到失败");
        }


        return sign?"签到成功":"签到失败";
    }

    @Override
    public Integer signCount(Long id) {
        LocalDateTime now = LocalDateTime.now();
        //3. 拼接key
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = USER_SIGN_KEY + id + keySuffix;
        //4. 获取今天是本月的第几天
        int dayOfMonth = now.getDayOfMonth();
        //5. 获取本月截至今天为止的所有的签到记录，返回的是一个十进制的数字 BITFIELD sign:5:202301 GET u3 0
        List<Long> result = redisTemplate.opsForValue().bitField(
                key,
                BitFieldSubCommands.create()
                        .get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth)).valueAt(0));
        //没有任务签到结果
        if (result == null || result.isEmpty()) {
            return  0;
        }
        Long num = result.get(0);
        if (num == null || num == 0) {
            return 0;
        }
        //6. 循环遍历
        int count = 0;
        while (true) {
            //6.1 让这个数字与1 做与运算，得到数字的最后一个bit位 判断这个数字是否为0
            if ((num & 1) == 0) {
                //如果为0，签到结束
                break;
            } else {
                count ++;
            }
            num >>>= 1;
        }
        return count;
    }

}




