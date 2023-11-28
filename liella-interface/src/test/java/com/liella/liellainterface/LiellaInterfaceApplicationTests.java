package com.liella.liellainterface;

import com.liella.liellaclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.liella.liellaclientsdk.client.liellaClient;
import javax.annotation.Resource;

@SpringBootTest
class LiellaInterfaceApplicationTests {

    @Resource
    private liellaClient liellaClient;
    @Test
    void contextLoads() {
        String nameByGet = liellaClient.getNameByGet("ll");
        User user = new User();
        user.setUsername("ll");
        String userNameByPost = liellaClient.getUserNameByPost(user);
        System.out.println(nameByGet);
        System.out.println(userNameByPost);
    }

}
