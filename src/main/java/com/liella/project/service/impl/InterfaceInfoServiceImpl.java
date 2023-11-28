package com.liella.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liella.liellacommon.model.entity.InterfaceInfo;
import com.liella.project.common.ErrorCode;
import com.liella.project.exception.BusinessException;

import com.liella.project.service.InterfaceInfoService;
import com.liella.project.mapper.InterfaceInfoMapper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author cys
* @description 针对表【interface_info(接口信息)】的数据库操作Service实现
* @createDate 2023-11-08 14:53:54
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService{
    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {


        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank() ) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (StringUtils.isNotBlank(name) && name.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }

    }
}




