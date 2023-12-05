package com.liella.liellacommon.service;

import com.liella.liellacommon.model.entity.InterfaceInfo;

/**
* @author cys
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-11-08 14:53:54
*/
public interface InnerInterfaceInfoService  {

    Boolean addInvokeCoint(long interfaceInfoId);
    /**
     * 从数据库中查询接口次数
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    int getInvokeCount(long interfaceInfoId,Long userId);

    /**
     * 从数据库中查询接口是否存在(请求路劲，请求方法)
     * @return
     */
    InterfaceInfo getInterfaceInfo(String path, String method);
 //   void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
