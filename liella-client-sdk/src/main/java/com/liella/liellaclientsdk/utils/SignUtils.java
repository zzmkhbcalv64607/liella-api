package com.liella.liellaclientsdk.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

public class SignUtils {

    public static String genSign(String hashMap, String secretKey){
        Digester md5 = new Digester(DigestAlgorithm.SHA256);
        String sign = hashMap+"."+secretKey;
        return md5.digestHex(sign);
    }
}
