package com.example.loginservice.client;

import com.example.loginservice.config.MultiPartConfig;
import com.example.loginservice.dto.MemListRes;
import com.example.loginservice.dto.MypageReq;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

//localhost:8093
@FeignClient(name = "chat", url = "${spring.cloud.openfeign.client.chat-url}",configuration = MultiPartConfig.class)
@Qualifier("chat")
public interface ChatClient {

    @PostMapping("/message/box")
    Object loadTeamList(
            @RequestBody String userId
    );
}
