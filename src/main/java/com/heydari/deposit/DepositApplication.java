package com.heydari.deposit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class DepositApplication {
    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }
    @Bean
    public WebClient.Builder getWebClientBuilder(){
        return  WebClient.builder();
    }
    public static void main(String[] args) {
        SpringApplication.run(DepositApplication.class, args);
    }

}
