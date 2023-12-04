package com.liella.project.common;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.UUID;
 
/**
 * 阿里云 OSS 工具类
 */
@Slf4j
public class AliOSSUtils {
 

 
    /**
     * 实现上传图片到OSS
     */
    public String upload(MultipartFile file) throws IOException {

        // 获取上传的文件的输入流
        InputStream inputStream = file.getInputStream();
 
        // 避免文件覆盖
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));
 
        //上传文件到 OSS
       // OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
       OSS ossClient = new OSSClientBuilder().build("oss-cnxxxxxxxxxuncs.com",
               "LTAI5tBLQxxxxxxxxxxmqXiBB",
               "NwpxxxxxxxxxxxxqBeJR0qRF");
        ossClient.putObject("liellaliyuu", fileName, inputStream);
        String url ="";
        try {
            // 关闭OSSClient。
             url = "/" +fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        //log.info("上传文件到OSS",endpoint,accessKeyId,accessKeySecret,bucketName);
        //文件访问路径
        try {
            // 关闭OSSClient。
            ossClient.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 关闭ossClient
        //ossClient.shutdown();
        return url;// 把上传到oss的路径返回
    }
 
}