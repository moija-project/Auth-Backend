package com.example.loginservice.controller;

import com.example.loginservice.client.MyClient;
import com.example.loginservice.dto.MemListRes;
import com.example.loginservice.dto.MypageReq;
import com.example.loginservice.etity.Account;
import com.example.loginservice.global.BaseException;
import com.example.loginservice.global.BaseResponse;
import com.example.loginservice.global.BaseResponseStatus;
import com.example.loginservice.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.example.loginservice.global.BaseResponseStatus.FILE_FORMAT_ERROR;

@RestController
@RequiredArgsConstructor
@RequestMapping("/my")
public class MyController {
    @Autowired
    MyClient myClient;
    @Autowired
    ScoreService scoreService;
    @PostMapping("/team/list")
    public Object loadTeamList(
            @AuthenticationPrincipal Account account) throws BaseException {
        if(account == null)
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        return myClient.loadTeamList(account.getUsername());
    }

    @PostMapping("/member/{postId}")
    public BaseResponse<List<MemListRes>> loadMemberList(
            @PathVariable(value = "postId") Long postId,
            @AuthenticationPrincipal Account account
    ) throws BaseException {
        if(account == null)
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        List<MemListRes> response = myClient.loadMemberList(postId, account.getUsername());
        response.forEach(memListRes -> memListRes.setGrant(scoreService.existsByGrantAndGranted(account.getUsername(), memListRes.getUserId())));
        return new BaseResponse(response);
    }
    @PostMapping(value = "/member/kick/{postId}",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Object kickMember(
            @PathVariable(value = "postId") Long postId,
            @RequestPart (value = "user_nickname") String user,
            @AuthenticationPrincipal Account account
    )  {
        MypageReq.MyKickReq myKickReq = new MypageReq.MyKickReq(user);
        System.out.println(user);
        return myClient.kickMember(postId, myKickReq, account.getUsername());
    }
    @PostMapping("/waiting/list")
    public Object loadWaitingList(
            @AuthenticationPrincipal Account account) throws BaseException {
        if(account == null)
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        return myClient.loadWaitingList(account.getUsername());
    }
    @PostMapping("/send/list")
    public Object loadSendList(
            @AuthenticationPrincipal Account account) throws BaseException {
        if(account == null)
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        return myClient.loadSendList(account.getUsername());
    }

    @PostMapping("/waiting/{waitingId}")
    public Object viewWaiting(
            @PathVariable(value = "waitingId") Long waitingId,
            @AuthenticationPrincipal Account account
    ) throws BaseException {
        if(account == null)
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        return myClient.viewWaiting(waitingId, account.getUsername());
    }

    @PostMapping("/accept/{waitingId}")
    public Object acceptWaiting(
            @PathVariable(value = "waitingId") Long waitingId,
            @AuthenticationPrincipal Account account
    ) throws BaseException {
        if(account == null)
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        return myClient.acceptWaiting(waitingId, account.getUsername());
    }
    @PostMapping("/deny/{waitingId}")
    public Object denyWaiting(
        @PathVariable(value = "waitingId") Long waitingId,
        @AuthenticationPrincipal Account account
    ) throws BaseException {
        if(account == null)
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        return myClient.denyWaiting(waitingId, account.getUsername());
    }

    @PostMapping("/clip")
    public Object viewMyClip(
            @AuthenticationPrincipal Account account
    ) throws BaseException {
        if(account == null)
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        return myClient.viewMyClip(account.getUsername());
    }

    @PostMapping("/joined-team")
    public Object viewMyJoinTeam(
            @AuthenticationPrincipal Account account) throws BaseException {
        if(account == null)
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        return myClient.viewMyJoinTeam(account.getUsername());
    }

    @PostMapping("/profile")
    public Object viewMyProfile(
            @AuthenticationPrincipal(errorOnInvalidType = true) Account account) throws BaseException {
        if(account == null)
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        return myClient.viewMyProfile(account.getUsername());
    }
    @PatchMapping(value="/profile/edit/photo",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Object editPhoto(
            @RequestPart(name = "file") MultipartFile file,
            @AuthenticationPrincipal Account account
    ) throws BaseException {
        if(account == null)
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        if(file.getContentType().equals("image/png") || file.getContentType().equals("image/jpeg")){
            return myClient.editPhoto(file, account.getUsername());
        }
        System.out.println(file.getContentType());
        throw new BaseException(FILE_FORMAT_ERROR);
    }
    @PatchMapping(value = "/profile/edit/nick",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Object editNick(
            @RequestPart (value = "new_nickname") String user,
            @AuthenticationPrincipal Account account
    ) throws BaseException {
        if(account == null)
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        return myClient.editNick(user, account.getUsername());
    }



}
