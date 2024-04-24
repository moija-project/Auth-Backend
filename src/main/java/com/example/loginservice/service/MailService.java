package com.example.loginservice.service;

import com.example.loginservice.global.BaseException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static com.example.loginservice.global.BaseResponseStatus.UNABLE_TO_SEND_EMAIL;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    @Autowired
    TemplateEngine templateEngine;

    public void sendVerifyMail(String to, String sub, Context context) throws BaseException {
        sendMail(to,sub,context,"verify-mail-template.html");
    }

    public void sendPasswordMail(String to, String sub, Context context) throws BaseException {
        sendMail(to,sub,context,"password-mail-template.html");
    }
    private void sendMail(String to, String sub, Context context, String template) throws BaseException {
        try {
            MimeMessagePreparator preparatory = mimeMessage -> {
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                mimeMessageHelper.setTo(to); // 메일 수신자
                mimeMessageHelper.setSubject(sub); // 메일 제목
                String content = templateEngine.process(template, context);
                mimeMessageHelper.setText(content, true); // 메일 본문 내용, HTML 여부
            };
            javaMailSender.send(preparatory);

            System.out.println("email send success!");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new BaseException(UNABLE_TO_SEND_EMAIL);
        }

    }
}
