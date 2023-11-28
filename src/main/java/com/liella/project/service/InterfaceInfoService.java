package com.liella.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liella.liellacommon.model.entity.InterfaceInfo;

/**
* @author cys
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-11-08 14:53:54
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
