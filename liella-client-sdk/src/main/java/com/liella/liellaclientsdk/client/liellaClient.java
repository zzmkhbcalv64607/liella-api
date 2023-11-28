package com.liella.liellaclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.liella.liellaclientsdk.model.User;

import java.util.HashMap;

import static com.liella.liellaclientsdk.utils.SignUtils.genSign;


/**
 * 第三方接口的客户端
 *
 * @auther cys
 */
public class liellaClient {


    private static final String GATEWAY_WAY="http://localhost:8090";
    private String accessKey;
    private String secretKey;

    public liellaClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }
    public String getNameByGet(String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);

        String result= HttpUtil.get(GATEWAY_WAY+"/api/name/", paramMap);
        System.out.println(result);
        return result;
    }


    public String getNameByPost( String name) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);

        String result= HttpUtil.post(GATEWAY_WAY+"/api/name/", paramMap);
        System.out.println(result);
        return result;
    }
    private HashMap<String, String> getHeaderMap(String body) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("accessKey", accessKey);
       // headerMap.put("secretKey", secretKey);
        headerMap.put("nonce", RandomUtil.randomNumbers(4));
        headerMap.put("body",body);
        headerMap.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
        headerMap.put("sign",genSign(body,secretKey));
        return headerMap;
    }

    public String getUserNameByPost( User user) {
        String json = JSONUtil.toJsonStr(user);
        HttpResponse result2 = HttpRequest.post(GATEWAY_WAY+"/api/name/user")
                .addHeaders(getHeaderMap(json))
                .body(json)
                .execute();
        //打印状态码和响应内容
        System.out.println(result2.getStatus());
        System.out.println(result2.body());
        return result2.body();
    }
}
