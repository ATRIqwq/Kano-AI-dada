package com.kano.springbootinit.config;

import com.zhipu.oapi.ClientV4;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "ai")
@Configuration
@Data
public class AiConfig {

   private String apiKey;

   @Bean
   public ClientV4 getClientV4(){
       return new ClientV4.Builder(apiKey).build();
   }

}
