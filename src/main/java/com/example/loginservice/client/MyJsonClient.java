package com.example.loginservice.client;

import com.example.loginservice.config.JsonConfig;
import com.example.loginservice.config.MultiPartConfig;
import com.example.loginservice.dto.IdPageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;

@FeignClient(name = "myjson", url = "${spring.cloud.openfeign.client.my-url}",configuration = JsonConfig.class)
@Qualifier("myjson")
public interface MyJsonClient {
    @PostMapping(value = "/my/team/list")
    Object loadTeamList(
            @RequestBody IdPageDTO idPageDTO
    );
    @PostMapping("/my/send/list")
    Object loadSendList(
            @RequestBody IdPageDTO idPageDTO
    );
    @PostMapping("/my/joined-team")

    Object viewMyJoinTeam(
            @RequestBody IdPageDTO idPageDTO
    );
}
