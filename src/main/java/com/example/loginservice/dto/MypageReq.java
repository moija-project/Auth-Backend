package com.example.loginservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class MypageReq {
    @Getter@Setter@Builder@AllArgsConstructor
    public static class MyKickReq{
        @JsonProperty(value = "user_nickname")
        String userNickname;
    }
    @Getter@Setter@Builder@AllArgsConstructor
    public static class MyNickReq {
        @JsonProperty(value = "new_nickname")
        String newNickname;
    }

}
