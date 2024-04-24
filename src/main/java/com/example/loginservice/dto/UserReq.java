package com.example.loginservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class UserReq {
    @Getter@AllArgsConstructor@NoArgsConstructor
    static public class UserJoinReq{
        /*
        *
        * {
            "user_id": "qwer123",
            "id_check": true,
            "password_encode": "wjgbaKdwheb234Adjgn==",
            "name": "김수정",
            "nickname": "테스트맨",
            "double_nick": false,
            "gender": 1,
            "birth": 2001-01-01,
            "phone_number": "010-1111-1111",
            "email": "mytest@test.com",
        * }
        *
        * */

        @JsonProperty(value = "user_id")
        String userId;
        @JsonProperty(value = "password",access = JsonProperty.Access.WRITE_ONLY)
        String password;
        String name;
        String nickname;
        boolean gender;
        @DateTimeFormat
        LocalDate birth;
        @JsonProperty(value = "phone_number")
        String phoneNumber;
        String email;

    }
    @Getter@AllArgsConstructor@NoArgsConstructor
    static public class UserLoginReq{
        String username;
        String password;
    }
    @Getter@AllArgsConstructor@NoArgsConstructor
    static public class UserFindIdReq{
        String name;
        String email;//사실 이메일말고 폰번호였으면함... 본인인증 서비스에 맞기기.
    }
    @Getter@AllArgsConstructor@NoArgsConstructor
    static public class UserFindPasswordReq{
        String userId;
        String name;
        String email;
    }

    @Getter@AllArgsConstructor@NoArgsConstructor
    static public class UserNewPasswordReq{
        String userId;
        String tmpPassword;
        String newPassword;
    }

    @Getter@AllArgsConstructor@NoArgsConstructor
    static public class UserGrantReq{
        @JsonProperty("granted")
        String userId;
        @JsonProperty("score")
        float userReliability;
    }

    @Getter@AllArgsConstructor@NoArgsConstructor
    public class UserDropReq {
        String password;
    }
}
