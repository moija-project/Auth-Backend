package com.example.loginservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

public class PostRes {
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ListPostRes{
        private Long post_id;
        private boolean state_recruit;
        private String title;
        private String contents;
        private String leader_nickname;
        private Timestamp latest_write;
        private long likes;
        private long views;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReadPostRes{
        @JsonProperty("state_recruit")
        private boolean stateRecruit;
        private String title;
        private String contents;
        @JsonProperty("leader_nickname")
        private String leaderNickname;
        @JsonProperty("latest_write")
        private Timestamp latestWrite;
        @JsonProperty("last_write")
        private Timestamp lastWrite;
        @JsonProperty("first_write")
        private Timestamp firstWrite;

        private String category;
        @JsonProperty("is_changed")
        private boolean isChanged;
        private int penalty;
        @JsonProperty("reliability_recruit")
        private float reliabilityRecruit;
        private long likes;
        private long views;
        @JsonProperty("pictures")
        List<String> pictures;

        private boolean myliked;
        private boolean mycliped;
        @JsonProperty("num_condition")
        int numCondition;

        //유저 관련 정보 전달
        private String userNickname;
        @JsonProperty("user_id")
        private String userId;
        @JsonProperty("reliability_user")
        private Float reliabilityUser;
        @JsonProperty("born_in")
        private String bornIn;

        @JsonProperty("profile_photo")
        private String profilePhoto;

    }
    public static class AnswerPostRes{}
}
