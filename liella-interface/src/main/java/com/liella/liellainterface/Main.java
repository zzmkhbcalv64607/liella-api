package com.liella.liellainterface;


import com.liella.liellaclientsdk.client.liellaClient;
import com.liella.liellaclientsdk.model.User;

public class Main {
    public static void main(String[] args) {
        String accessKey="ll";
        String secretKey="abcdefg";
        liellaClient liellaClient = new liellaClient(accessKey, secretKey);
        System.out.println(liellaClient.getNameByGet("liella"));
        System.out.println(liellaClient.getNameByPost("liella"));
        User user = new User();
        user.setUsername("liella");
        System.out.println(liellaClient.getUserNameByPost(user));
    }
}
