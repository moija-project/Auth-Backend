package com.example.loginservice.controller;

import com.example.loginservice.dto.UserReq;
import com.example.loginservice.global.BaseException;
import com.example.loginservice.global.BaseResponse;
import com.example.loginservice.service.MailService;
import com.example.loginservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;

import java.security.SecureRandom;
import java.util.Date;

import static com.example.loginservice.global.BaseResponseStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/find")
public class FindController {
    @Autowired
    UserService userService;
    MailService mailService;

    @PostMapping("/password")
    public BaseResponse<Void> findPassword(
            @RequestBody UserReq.UserFindPasswordReq userFindPasswordReq
    ) throws BaseException {
        if (userService.checkAccount(userFindPasswordReq)) {
            String tmpPassword = getRamdomPassword(10);
            Context context = new Context();
            context.setVariable("tmp_password",tmpPassword);
            mailService.sendPasswordMail("to", "sub", context);
            userService.updatePassword(userFindPasswordReq.getUserId(), tmpPassword);

            return new BaseResponse<>(SUCCESS);
        }
        throw new BaseException(USER_NOT_EXISTS);
    }

    @PostMapping("/enroll")//메일로 간 임시 비밀번호, 새 비밀번호 입력
    public BaseResponse<Void> enrollPassword(
            @RequestBody UserReq.UserNewPasswordReq userNewPasswordReq
    ) throws BaseException {
        if (userService.matchPassword(userNewPasswordReq.getUserId(), userNewPasswordReq.getTmpPassword())) {
            userService.updatePassword(userNewPasswordReq.getUserId(), userNewPasswordReq.getNewPassword());
        } else {
            return new BaseResponse<>(PASSWORD_NOT_MATCH);
        }
        return new BaseResponse<>(SUCCESS);
    }
    //getRamdomPassword(10)를 호출하면 10 글자의 임시비밀번호가 생성됩니다. 출처: https://byul91oh.tistory.com/466 [개꼬 [: 개발하는 꼬바리]:티스토리]
    public String getRamdomPassword(int size) {
        char[] charSet = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '!', '@', '#', '$', '%', '^', '&'};
        StringBuffer sb = new StringBuffer();
        SecureRandom sr = new SecureRandom();
        sr.setSeed(new Date().getTime());
        int idx = 0;
        int len = charSet.length;
        for (int i = 0; i < size; i++) {
            idx = (int) (len * Math.random());
            idx = sr.nextInt(len);
            // 강력한 난수를 발생시키기 위해 SecureRandom을 사용한다.
            sb.append(charSet[idx]);        }
            return sb.toString();    }
        }
