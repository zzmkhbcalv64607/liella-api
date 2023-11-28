package com.liella.project.service.impl.Inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liella.liellacommon.model.entity.InterfaceInfo;
import com.liella.liellacommon.service.InnerInterfaceInfoService;
import com.liella.project.mapper.InterfaceInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * 针对表【interface_info(接口信息)】的数据库操作Service
 * @createDate 2023-11-08 14:53:54
 */
@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

   @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    /**
     * 从数据库中查询接口是否存在(请求路劲，请求方法)
     * @param path
     * @param method
     * @return
     */
    @Override
    public InterfaceInfo getInterfaceInfo(String path, String method) {
        //判断accessKey和secretKey是否为空
        if (StringUtils.isAnyBlank(path,method)){
            throw new RuntimeException("path or method is null");
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url",path);
        queryWrapper.eq("method",method);
        return  interfaceInfoMapper.selectOne(queryWrapper);
    }


}