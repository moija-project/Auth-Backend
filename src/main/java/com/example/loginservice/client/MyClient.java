package com.example.loginservice.client;

import com.example.loginservice.config.MultiPartConfig;
import com.example.loginservice.dto.IdPageDTO;
import com.example.loginservice.dto.MemListRes;
import com.example.loginservice.dto.MypageReq;
import com.example.loginservice.global.BaseResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.List;

//localhost:8090
@FeignClient(name = "my", url = "${spring.cloud.openfeign.client.my-url}",configuration = MultiPartConfig.class)
@Qualifier("my")
public interface MyClient {


    @PostMapping("/my/member/{postId}")
    List<MemListRes> loadMemberList(
            @PathVariable(value = "postId") Long postId,
            @RequestBody String userId
    );
    @PostMapping("/my/member/kick/{postId}")

    Object kickMember(
            @PathVariable(value = "postId") Long postId,
            @RequestPart(name = "req") MypageReq.MyKickReq myKickReq,
            @RequestPart(name = "userId") String userId
    );
    @PostMapping("/my/waiting/list")
    Object loadWaitingList(
            @RequestBody String userId
    );

    @PostMapping("/my/waiting/{waitingId}")

    Object viewWaiting(
            @PathVariable(value = "waitingId") Long waitingId,
            @RequestBody String userId
    );

    @PostMapping("/my/accept/{waitingId}")

    Object acceptWaiting(
            @PathVariable(value = "waitingId") Long waitingId,
            @RequestBody String userId
    );

    @PostMapping("/my/deny/{waitingId}")

    Object denyWaiting(
            @PathVariable(value = "waitingId") Long waitingId,
            @RequestBody String userId
    );

    @PostMapping("/my/clip")

    Object viewMyClip(
            @RequestBody String userId
    );


    @PostMapping("/my/profile")

    Object viewMyProfile(
            @RequestBody String userId
    );

    @PutMapping(value="/my/profile/edit/photo",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})

    Object editPhoto(
            @RequestPart(value = "file") MultipartFile file,
            @RequestPart(name = "userId") String userId
    );

    @PutMapping(value = "/my/profile/edit/nick",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})

    Object editNick(
            @RequestPart(name = "req") String newNickname,
            @RequestPart(name = "userId") String userId
    );
}
