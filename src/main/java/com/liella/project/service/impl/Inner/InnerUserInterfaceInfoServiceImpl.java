package com.liella.project.service.impl.Inner;

import com.liella.liellacommon.model.entity.UserInterfaceInfo;
import com.liella.liellacommon.service.InnerUserInterfaceInfoService;
import com.liella.project.service.impl.UserInterfaceInfoServiceImpl;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoServiceImpl userInterfaceInfoService;
    @Override
    public boolean invokeCount(Long userId, Long interfaceInfoId) {
       return userInterfaceInfoService.invokeCount(userId,interfaceInfoId);
    }
}
