package com.example.loginservice.controller;

import com.example.loginservice.dto.JwtToken;
import com.example.loginservice.dto.UserReq;
import com.example.loginservice.dto.UserRes;
import com.example.loginservice.etity.Account;
import com.example.loginservice.global.BaseException;
import com.example.loginservice.global.BaseResponse;
import com.example.loginservice.jwt.JwtAuthenticationFilter;
import com.example.loginservice.service.RedisUtils;
import com.example.loginservice.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static com.example.loginservice.global.BaseResponseStatus.BAD_ACCESS;
import static com.example.loginservice.global.BaseResponseStatus.SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping(value = "/verify-email")
    public BaseResponse<Void> verifyEmail(
            @RequestParam(value = "code")String code
    ) throws BaseException {
        userService.accountEnable(code);
        return new BaseResponse<Void>(SUCCESS);
    }

    @PostMapping("/login")
    public BaseResponse<JwtToken> signIn(
            @RequestBody UserReq.UserLoginReq userLoginReq, HttpServletResponse response
    ) throws BaseException {
        JwtToken token= userService.signIn(userLoginReq,response);
        return new BaseResponse<>(token);
    }
    @PostMapping("/join")
    public BaseResponse<Void> join(
            @RequestBody UserReq.UserJoinReq userJoinReq
    ) throws BaseException, IOException {
        userService.join(userJoinReq);
        return new BaseResponse<Void>(SUCCESS);
    }
    @GetMapping("/logout")
    public BaseResponse<Void> signOut(
            HttpServletRequest request,
            HttpServletResponse response,
            @AuthenticationPrincipal String userId
    ) throws BaseException, ServletException {

        userService.signOut(request,response);

        return new BaseResponse<>(SUCCESS);
    }

    /** 스캔 공격의 위험성이 있지 않을까 곰곰히 생각해보고
     * 이에 관련된 취약점 찾아보기
     */
    @GetMapping("/id-dup")
    public BaseResponse<Void> checkIdDup(
            @RequestParam("checkId") String userId
    ) throws BaseException {
        userService.checkUserIdDup(userId);
        return new BaseResponse<>(SUCCESS);
    }
    @GetMapping("/n-dup")
    public BaseResponse<Void> checkNDup(
            @RequestParam("checkNick") String userNickname
    ) throws BaseException {
        userService.checkNicknameDup(userNickname);
        return new BaseResponse<>(SUCCESS);
    }
    @GetMapping("/e-dup")
    public BaseResponse<Void> checkEDup(
            @RequestParam("checkEmail") String userEmail
    ) throws BaseException {
        userService.checkEmailDup(userEmail);
        return new BaseResponse<>(SUCCESS);
    }
    @PostMapping("/grant")
    public BaseResponse<Void> grantAnotherUser(
            @RequestBody UserReq.UserGrantReq userGrantReq,
            @AuthenticationPrincipal Account account
    ) throws BaseException {
        userService.unionGrant(userGrantReq, account.getUsername());
        return new BaseResponse<>(SUCCESS);
    }

    @PostMapping("/dropout")
    public BaseResponse<Void> dropoutUser(
            @RequestBody UserReq.UserDropReq userDropReq,
            @AuthenticationPrincipal Account account
    ) throws BaseException {
        userService.dropOut(account.getUsername(),userDropReq);
        return new BaseResponse<>(SUCCESS);
    }
    @PostMapping("/profile")
    public BaseResponse<UserRes.ProfileRes> viewAnotherProfile(
            @RequestBody Map<String,String> userIdMap,
            @AuthenticationPrincipal Account account
    ) throws BaseException {
        if(!userIdMap.containsKey("user_id")){
            throw new BaseException(BAD_ACCESS);
        }
        UserRes.ProfileRes response = userService.viewAnother(userIdMap.get("user_id"), account.getUsername());
        return new BaseResponse<>(response);
    }
}
