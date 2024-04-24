package com.example.loginservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemListRes {
    String nickname;
    @JsonProperty("user_id")
    String userId;
    boolean grant;

}
