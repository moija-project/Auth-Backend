package com.example.loginservice.client;

import org.springframework.cloud.openfeign.support.JsonFormWriter;
import org.springframework.context.annotation.Bean;

public class MyClientConfiguration {

    @Bean
    JsonFormWriter jsonFormWriter() {
        return new JsonFormWriter();
    }
}
