package com.liella.liellacommon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liella.liellacommon.model.entity.InterfaceInfo;
import com.liella.liellacommon.model.entity.User;
import com.liella.liellacommon.model.entity.UserInterfaceInfo;


/**
* @author cys
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2023-11-17 14:30:14
*/
public interface InnerUserInterfaceInfoService  {
  //  void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);



    /**
     * 调用接口统计
     * @param userId 用户id
     * @param interfaceInfoId 接口id
     * @return true-调用成功，false-调用失败
     */
    boolean invokeCount(Long userId, Long interfaceInfoId);
}
