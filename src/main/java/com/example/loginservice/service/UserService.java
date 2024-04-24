package com.example.loginservice.service;

import com.example.loginservice.dto.JwtToken;
import com.example.loginservice.dto.UserReq;
import com.example.loginservice.dto.UserRes;
import com.example.loginservice.etity.Account;
import com.example.loginservice.etity.EnableAccount;
import com.example.loginservice.etity.User;
import com.example.loginservice.global.BaseException;
import com.example.loginservice.jwt.JwtTokenProvider;
import com.example.loginservice.repository.EnableAccountRepository;
import com.example.loginservice.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static com.example.loginservice.etity.Authority.ROLE_USER;
import static com.example.loginservice.extractor.Genarator.changeToBornIn;
import static com.example.loginservice.global.BaseResponseStatus.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    EnableAccountRepository enableRepository;
    @Autowired
    ScoreService scoreService;
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    MailService mailService;
    @Value("${my.domain.name}")
    private String domain;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;


    @Autowired
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    TemplateEngine templateEngine;
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> op = userRepository.findByUsernameAndIsEnabledTrue(username);
        if(op.isEmpty()) {
            try {
                throw new BaseException(USER_NOT_EXISTS);
            } catch (BaseException e) {
                throw new RuntimeException(e);
            }
        }

        return (UserDetails) new Account(username,op.get().getPassword(),
                List.of(new SimpleGrantedAuthority(op.get().getAuthority().name())),
                op.get().getNickname(),op.get().getUuid());
    }
    @Transactional
    public void join(UserReq.UserJoinReq payload) throws BaseException, IOException {
        User user = User.builder()
                .username(payload.getUserId())
                .birth(payload.getBirth())
                .name(payload.getName())
                .uuid(String.valueOf(UUID.randomUUID()))
                .email(payload.getEmail())
                .reliabilityUser(2.5F)
                .password(passwordEncoder.encode(payload.getPassword()))
                .phoneNumber(payload.getPhoneNumber())
                .timeJoin(Timestamp.valueOf(LocalDateTime.now()))
                .gender(payload.isGender())
                .nickname(payload.getNickname())
                //일단 이메일 인증 전에는 이게 호출되면서 저장됨. 계정은 비활성화.
                .authority(ROLE_USER)
                .isEnabled(false)
                .isAccountNonLocked(true)
                .build();
        userRepository.saveAndFlush(user);
        if(user.getUsername().isEmpty()) {
           throw new BaseException(TASK_FAILED);
        }

        //여기에 이메일 전송 구현
        try {
            sendFormedEmail(domain, user.getEmail());
        }catch (Exception e) {
            System.out.println("이메일 전송이 뭔가 잘못되었기 때문에 그냥 안보낼래요.");
            System.out.println(e.getMessage());
        }
    }

    private void sendFormedEmail(String domain, String email) throws BaseException, IOException {
        String url = "http://" + domain + "/user/verify-email?code=" + genarateEmailCode(email);

        Context context = new Context();
        context.setVariable("verification_url", url);
        mailService.sendVerifyMail(email,"계정 활성화 요청",context);
    }

    //이거 여기있으면 안돼!! github에 올라가면 망해!!
    public String genarateEmailCode(String email) throws BaseException { //code 반환
        String uuid=UUID.randomUUID().toString().replaceAll("-","");
        enableRepository.save(EnableAccount.builder()
                .uuid(uuid)
                .userEmail(email)
                .ttl(1000 * 60 * 20)
                .build());
        return uuid;
    }

    public void accountEnable(String code) throws BaseException{
        //code = Arrays.toString(Base64.getDecoder().decode(code));
        code = code.trim();
        System.out.println(code);
        Optional<EnableAccount> emailOp =  enableRepository.findByUuid(code);
        //만료된 url의 경우 / 없는 userID나 디코딩이 안되는 경우 / 이미 유저인 경우
        if(emailOp.isEmpty()) {
            throw new BaseException(EXPIRED_VERIFY);
        }
        userRepository.updateEnable(emailOp.get().getUserEmail(),true);
    }
    @Transactional
    public JwtToken signIn(UserReq.UserLoginReq userLoginReq, HttpServletResponse response) throws BaseException {
        UserDetails user = loadUserByUsername(userLoginReq.getUsername());
        if(!passwordEncoder.matches(userLoginReq.getPassword(),user.getPassword())) {
            throw new BaseException(PASSWORD_NOT_MATCH);
        }

        // 1. username + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        //패스워드 값이 디코딩된걸로 넣는지 확인 필수
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(),userLoginReq.getPassword(),user.getAuthorities());

        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            // 3. 인증 정보를 기반으로 JWT 토큰 생성
            String accessToken = jwtTokenProvider.generateAccessToken(authentication);
            response.addCookie(new Cookie("REFRESH_TOKEN", jwtTokenProvider.generateRefreshToken(accessToken)){{setMaxAge(3600);setPath("/");}});
            return JwtToken.builder()
                    .accessToken(accessToken)
                    .grantType("Bearer")
                    .build();
        } catch (AuthenticationException |NoSuchElementException e) {
            throw new BaseException(LOGIN_EXPIRED);
        }
    }

    @Transactional
    public void checkNicknameDup(String newNickname) throws BaseException{
        if(userRepository.existsByNickname(newNickname) ) {
            throw new BaseException(DUPLICATE_NICKNAME);
        }
    }
    @Transactional
    public void checkEmailDup(String email) throws BaseException{
        if(userRepository.existsByEmail(email) ) {
            throw new BaseException(DUPLICATE_NICKNAME);
        }
    }
    @Transactional
    public void checkUserIdDup(String userid) throws BaseException{
        if(userRepository.existsByUsername(userid) ) {
            throw new BaseException(DUPLICATE_NICKNAME);
        }
    }
    public void signOut(HttpServletRequest request, HttpServletResponse response) throws BaseException, ServletException {
        //리프레시 토큰 삭제
        String refreshToken = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("REFRESH_TOKEN")).findFirst().get().getValue();
        if(redisUtils.existByToken(refreshToken)) {
            String authId = redisUtils.findByToken(refreshToken).getAuthId();
            redisUtils.deleteByAuthId(authId);
        }
        //액세스 토큰 빼앗기
        response.setHeader("Authorization","");
        //리프레시 토큰 빼앗기
        response.addCookie(new Cookie("REFRESH_TOKEN",""){{
            setPath("/");
        }});
    }

    public void unionGrant(UserReq.UserGrantReq userGrantReq,String userId) throws BaseException {
        Optional<User> userOp = userRepository.findByUsernameAndIsEnabledTrue(userGrantReq.getUserId());
        if(userOp.isPresent() && !scoreService.existsByGrantAndGranted(userId,userGrantReq.getUserId())){
            //병합 과정
            int prevNumScore = scoreService.countByGrantedId(userGrantReq.getUserId());
            float prevScore = userOp.get().getReliabilityUser();
            float score = ( prevScore * prevNumScore + userGrantReq.getUserReliability() ) / ( prevNumScore+1 );
            scoreService.saveGrant(userId,userGrantReq.getUserId(),score);
            userRepository.updateReliabilityUser(userGrantReq.getUserId(),score);
        } else {
            throw new BaseException(ALREADY_SCORED);
        }
    }

    public boolean checkAccount(UserReq.UserFindPasswordReq userFindPasswordReq) {
        Optional<User> userOp = userRepository.findByUsernameAndIsEnabledTrue(userFindPasswordReq.getUserId());
        if(userOp.isPresent()) {
            if(userOp.get().getEmail().equals(userFindPasswordReq.getEmail())) {
                if(userOp.get().getName().equals(userFindPasswordReq.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean matchPassword(String userId, String tmpPassword) throws BaseException {
        UserDetails user = loadUserByUsername(userId);
        if(!passwordEncoder.matches(tmpPassword,user.getPassword())) {
            throw new BaseException(PASSWORD_NOT_MATCH);
        }
        return false;
    }

    public void updatePassword(String userId, String newPassword) {
        userRepository.updatePassword(userId,newPassword);
    }

    public void dropOut(String username, UserReq.UserDropReq userDropReq) throws BaseException {
        Optional<User> userOp = userRepository.findByUsernameAndIsEnabledTrue(username);
        if(userOp.isPresent()){
            if(passwordEncoder.matches(userDropReq.getPassword(),userOp.get().getPassword())){
                User user = userOp.get();
                //락처리함 이게 맞나..?
                user.setAccountNonLocked(false);
                user.setEnabled(false);
                //어카운트 락되고, 활성화 중지된 상태에서의 가입일자는 탈퇴일자로
                user.setTimeJoin(Timestamp.valueOf(LocalDateTime.now()));
            }
        }
        throw new BaseException(PASSWORD_NOT_MATCH);
    }

    public UserRes.ProfileRes viewAnother(String userId,String myId) throws BaseException {
        Optional<User> userOptional = userRepository.findByUsernameAndIsEnabledTrue(userId);
        if(userOptional.isPresent()) {
            return UserRes.ProfileRes.builder()
                    .userId(userOptional.get().getUsername())
                    .reliabilityUser(userOptional.get().getReliabilityUser())
                    .nickname(userOptional.get().getNickname())
                    .bornIn(changeToBornIn(userOptional.get().getBirth()))
                    .gender(userOptional.get().isGender()?"여":"남")
                    .profilePhotoUrl(userOptional.get().getProfile())
                    .myGrant(scoreService.existsByGrantAndGranted(myId,userId)?scoreService.getMyGrantScore(myId,userId):null)
                    .build();
        }
        throw new BaseException(USER_NOT_EXISTS);
    }
}
