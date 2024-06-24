package com.example.loginservice.controller;


import com.example.loginservice.client.ChatClient;
import com.example.loginservice.client.MyClient;
import com.example.loginservice.dto.ChatListDTO;
import com.example.loginservice.etity.Account;
import com.example.loginservice.global.BaseException;
import com.example.loginservice.global.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class ChatController {
    @Autowired
    ChatClient chatClient;
    @PostMapping("/box")
    public Object loadChatRoomList(
            @AuthenticationPrincipal Account account) throws BaseException {
        if(account == null)
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        return chatClient.loadChatRoomList(account.getUsername());
    }

    @PostMapping(value = "/list")
    public Object loadMessageList(
            @AuthenticationPrincipal Account account, @RequestBody ChatListDTO chatListDTO) throws BaseException {
        if(account == null)
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);

        return chatClient.loadMessageList(chatListDTO,account.getUsername());
    }


}
