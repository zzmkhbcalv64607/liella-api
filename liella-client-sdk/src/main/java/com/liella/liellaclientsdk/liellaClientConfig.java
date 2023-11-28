package com.liella.liellaclientsdk;

import com.liella.liellaclientsdk.client.liellaClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
//通过 @Configuration 注解,将该类标记为一个配置类,告诉 Spring 这是一个用于配置的类
@ConfigurationProperties("liella.client")
//能够读取application.yml的配置,读取到配置之后,把这个读到的配置设置到我们这里的属性中,
@Data
@ComponentScan
//@ComponentScan 注解用于自动扫描组件，使得 Spring 能够自动注册相应的 Bean
public class liellaClientConfig {
    private String accessKey;
    private String secretKey;
    //通过 @Bean 注解,将该方法的返回值注册为一个 Bean,并且命名为 liellaClient
    @Bean
    public liellaClient liellaClient(){
        return new liellaClient(accessKey, secretKey);
    }
}
