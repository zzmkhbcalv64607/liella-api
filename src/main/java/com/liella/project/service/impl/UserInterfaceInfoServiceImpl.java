package com.liella.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.liella.liellacommon.model.entity.UserInterfaceInfo;

import com.liella.project.common.ErrorCode;
import com.liella.project.exception.BusinessException;
import com.liella.project.mapper.UserInterfaceInfoMapper;
import com.liella.project.service.UserInterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author cys
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
* @createDate 2023-11-17 14:30:14
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService {
    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {


        if ( userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank() ) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (userInterfaceInfo.getLeftNum()>0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "剩余次数不能小于0");
        }

    }


    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        //判断用户是否存在
        if (userId<=0||interfaceInfoId<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //使用updateWrapper封装查询条件
        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("interfaceInfoId", interfaceInfoId);
        updateWrapper.eq("userId", userId);
        updateWrapper.setSql("leftNum = leftNum - 1, totalNum = totalNum + 1");
        //用户可能会瞬间调用大量接口次数，为了避免统计出错，需要涉及到事务和锁的知识
        return this.update(updateWrapper);
    }


}




