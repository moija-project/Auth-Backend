package com.example.loginservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

public class UserRes {
    @Setter
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProfileRes {
        @JsonProperty("user_id")
        String userId;
        String nickname;
        @JsonProperty("birth_year")
        String bornIn;
        String gender;
        @JsonProperty("reliability_user")
        Float reliabilityUser;
        @JsonProperty("photo_profile")
        String profilePhotoUrl;
        @JsonProperty("my_grant")
        Float myGrant;
    }
}
