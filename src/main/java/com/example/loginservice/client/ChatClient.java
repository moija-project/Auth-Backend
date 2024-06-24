package com.example.loginservice.client;

import com.example.loginservice.config.MultiPartConfig;
import com.example.loginservice.dto.ChatListDTO;
import com.example.loginservice.dto.MemListRes;
import com.example.loginservice.dto.MypageReq;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

//localhost:8093
@FeignClient(name = "chat", url = "${spring.cloud.openfeign.client.chat-url}",configuration = MultiPartConfig.class)
@Qualifier("chat")
public interface ChatClient {

    @PostMapping("/message/box")
    public Object loadChatRoomList(
            @RequestBody String userId
    );

    @PostMapping(value="/message/list",consumes = MULTIPART_FORM_DATA_VALUE)
    Object loadMessageList(
            @RequestPart(name = "chat") ChatListDTO chatListDTO, @RequestPart(name = "userId")String username);
}
