package com.liella.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.liella.liellaclientsdk.client.liellaClient;
import com.liella.liellacommon.model.entity.InterfaceInfo;
import com.liella.liellacommon.model.entity.User;
import com.liella.liellacommon.model.entity.UserInterfaceInfo;
import com.liella.project.annotation.AuthCheck;
import com.liella.project.common.*;
import com.liella.project.constant.CommonConstant;
import com.liella.project.exception.BusinessException;
import com.liella.project.model.dto.InterfaceInfo.*;
import com.liella.project.model.enums.InterfaceInfoEnum;
import com.liella.project.service.InterfaceInfoService;
import com.liella.project.service.UserInterfaceInfoService;
import com.liella.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 接口管理
 *
 * @author liella
 */
@RestController
@RequestMapping("/InterfaceInfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private liellaClient liellaClient;

    @Resource
    private InterfaceInfoService InterfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    /**
     * 创建
     *
     * @param InterfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest InterfaceInfoAddRequest, HttpServletRequest request) {
        if (InterfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo InterfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(InterfaceInfoAddRequest, InterfaceInfo);
        // 校验
        InterfaceInfoService.validInterfaceInfo(InterfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        InterfaceInfo.setUserId(loginUser.getId());
        boolean result = InterfaceInfoService.save(InterfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newInterfaceInfoId = InterfaceInfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = InterfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = InterfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param InterfaceInfoUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest InterfaceInfoUpdateRequest,
                                            HttpServletRequest request) {
        if (InterfaceInfoUpdateRequest == null || InterfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo InterfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(InterfaceInfoUpdateRequest, InterfaceInfo);
        // 参数校验
        InterfaceInfoService.validInterfaceInfo(InterfaceInfo, false);
        User user = userService.getLoginUser(request);
        long id = InterfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = InterfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = InterfaceInfoService.updateById(InterfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<InterfaceInfo> getInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo InterfaceInfo = InterfaceInfoService.getById(id);
        return ResultUtils.success(InterfaceInfo);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param InterfaceInfoQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(InterfaceInfoQueryRequest InterfaceInfoQueryRequest) {
        InterfaceInfo InterfaceInfoQuery = new InterfaceInfo();
        if (InterfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(InterfaceInfoQueryRequest, InterfaceInfoQuery);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(InterfaceInfoQuery);
        List<InterfaceInfo> InterfaceInfoList = InterfaceInfoService.list(queryWrapper);
        return ResultUtils.success(InterfaceInfoList);
    }

    /**
     * 分页获取列表
     *
     * @param InterfaceInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(InterfaceInfoQueryRequest InterfaceInfoQueryRequest, HttpServletRequest request) {
        if (InterfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo InterfaceInfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(InterfaceInfoQueryRequest, InterfaceInfoQuery);
        long current = InterfaceInfoQueryRequest.getCurrent();
        long size = InterfaceInfoQueryRequest.getPageSize();
        String sortField = InterfaceInfoQueryRequest.getSortField();
        String sortOrder = InterfaceInfoQueryRequest.getSortOrder();
        String description = InterfaceInfoQuery.getDescription();
        //description 需支持模糊搜索
        InterfaceInfoQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(InterfaceInfoQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<InterfaceInfo> InterfaceInfoPage = InterfaceInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(InterfaceInfoPage);
    }

    /**
     * 上线
     *
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/online")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest,
                                                     HttpServletRequest request) {
        if (idRequest == null || idRequest.getId()<= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //1. 校验接口对象是否存在
        //获取IdRequest中的id
        long id = idRequest.getId();
        //根据id查询接口信息是否存在
        InterfaceInfo oldInterfaceInfo = InterfaceInfoService.getById(id);

        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //判断接口是否可以调用
       com.liella.liellaclientsdk.model.User user =new com.liella.liellaclientsdk.model.User();
        user.setUsername("test");
        String userNameByPost = liellaClient.getUserNameByPost(user);
        if (StringUtils.isBlank(userNameByPost)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"接口不可调用");
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoEnum.ONLINE.getValue());
        boolean result = InterfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 下线
     *
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/offline")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> offInterfaceInfo(@RequestBody  IdRequest idRequest,
                                                     HttpServletRequest request) {
        if (idRequest == null || idRequest.getId()<= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //1. 校验接口对象是否存在
        //获取IdRequest中的id
        long id = idRequest.getId();
        //根据id查询接口信息是否存在
        InterfaceInfo oldInterfaceInfo = InterfaceInfoService.getById(id);

        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //判断接口是否可以调用
        com.liella.liellaclientsdk.model.User user =new com.liella.liellaclientsdk.model.User();
        user.setUsername("test");
        String userNameByPost = liellaClient.getUserNameByPost(user);
        if (StringUtils.isBlank(userNameByPost)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"接口不可调用");
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoEnum.OFFLINE.getValue());
        boolean result = InterfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }




    /**
     * 测试
     *
     * @param interfaceInfoInvokeRequest
     * @param request
     * @return
     */
    @PostMapping("/invoke")
    public BaseResponse<Object> invokeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest,
                                                    HttpServletRequest request) {
        log.info("invokeInterfaceInfo request:{}", interfaceInfoInvokeRequest);
        if ( interfaceInfoInvokeRequest == null ||  interfaceInfoInvokeRequest.getId()<= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //1. 校验接口对象是否存在
        //获取IdRequest中的id
        long id =  interfaceInfoInvokeRequest.getId();
        //获取用户请求参数
        System.out.println(interfaceInfoInvokeRequest.getRequestParams());
        List<InterfaceInfoInvokeRequest.Field> fieldList = interfaceInfoInvokeRequest.getRequestParams();
        String requestParams = "{}";
        Gson gson = new Gson();
        if (fieldList != null && fieldList.size() > 0) {
            JsonObject jsonObject = new JsonObject();
            for (InterfaceInfoInvokeRequest.Field field : fieldList) {
                jsonObject.addProperty(field.getFieldName(), field.getValue());
            }

            requestParams = gson.toJson(jsonObject);
        }
        String userRequestParams = interfaceInfoInvokeRequest.getRequestParams().toString();

        //根据id查询接口信息是否存在
        InterfaceInfo oldInterfaceInfo = InterfaceInfoService.getById(id);

        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (oldInterfaceInfo.getStatus().equals(InterfaceInfoEnum.OFFLINE.getValue())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"接口不可调用");
        }
        User loginUser = userService.getLoginUser(request);
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        com.liella.liellaclientsdk.client.liellaClient client = new liellaClient(accessKey, secretKey);

        com.liella.liellaclientsdk.model.User user =gson.fromJson(requestParams, com.liella.liellaclientsdk.model.User.class);
        if (user.getUsername() == null) {
            return ResultUtils.error(0,"请输入正确的参数");
        }
        String userNameByPost = client.getUserNameByPost(user);
        return ResultUtils.success(userNameByPost);
    }

    /**
     * 提交接口调用申请请求
     */

    @PostMapping("/apply")
    public BaseResponse<Boolean> applyInterfaceInfo(@RequestBody ApplyRequest applyRequest,
                                                    HttpServletRequest request) {
        if (applyRequest == null || applyRequest.getUserId()<= 0|| applyRequest.getInterfaceId()<= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //1. 校验接口对象是否存在
        //获取IdRequest中的id
        long id = applyRequest.getInterfaceId();
        //根据id查询接口信息是否存在
        InterfaceInfo oldInterfaceInfo = InterfaceInfoService.getById(id);

        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        //todo 获取用户信息
        User user = userService.getById(applyRequest.getUserId());
        //todo 判断用户是否存在
        if (user == null||user.getId()<=0) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //todo 判断用户是否已经申请过该接口 如果已经申请过则不允许再次申请
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService
                .getOne(new QueryWrapper<UserInterfaceInfo>().eq("userId",
                user.getId()).eq("interfaceInfoId", oldInterfaceInfo.getId()));
        if (userInterfaceInfo != null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"您已经申请过该接口");
        }
        UserInterfaceInfo info = new UserInterfaceInfo();
        //todo 为用户分配接口调用权限及次数
        info.setUserId(user.getId());
        info.setInterfaceInfoId(oldInterfaceInfo.getId());
        info.setLeftNum(10);
        info.setTotalNum(0);
        info.setStatus(InterfaceInfoEnum.ONLINE.getValue());
        try {
            userInterfaceInfoService.save(info);
        }catch (Exception e){
            log.error("保存用户接口调用信息失败",e);
            //throw new BusinessException(ErrorCode.SYSTEM_ERROR,"保存用户接口调用信息失败");
            return ResultUtils.error(0,"保存用户接口调用信息失败");
        }
        //  todo  后续更改为 添加申请记录 申请状态为待审核 只有经过管理员同意才能调用接口
        //  todo  申请方式为免费试用 则直接给与十次调用机会
        //todo 更新接口调用申请状态
        //判断接口是否可以调用

        return ResultUtils.success(true);
    }
}
