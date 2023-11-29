package com.liella.liellainterface.controller;


import com.liella.liellaclientsdk.model.User;
import com.liella.liellaclientsdk.utils.SignUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * 此处为模拟的Api接口
 *
 * @auther  cys
 */
@RestController
@RequestMapping("/name")
public class NameController {

    @GetMapping("/get")
    public String getNameByGet(String name) {

        return "GET 你的名字是" + name;
    }

    @PostMapping("/post")
    public String getNameByPost(@RequestParam String name) {
        return "POST 你的名字是" + name;
    }

    @PostMapping("/user")
    public String getUserNameByPost(@RequestBody User user, HttpServletRequest request) {
        String accessKey = request.getHeader("accessKey");
        String nonce = request.getHeader("nonce");
        String timestamp = request.getHeader("timestamp");
        String sign = request.getHeader("sign");
        String body = request.getHeader("body");
        if (!accessKey.equals("ll")){
            throw new RuntimeException("accessKey错误");
        }

        if (Long.parseLong(nonce)>10000){
            throw new RuntimeException("nonce错误");
        }
        String gennedSign = SignUtils.genSign(body, "abcdefg");
        if (!sign.equals(gennedSign)){
            throw new RuntimeException("sign错误");
        }
        return "POST 用户名字是" + user.getUsername();
    }
}
