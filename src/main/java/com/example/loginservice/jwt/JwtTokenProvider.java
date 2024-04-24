package com.example.loginservice.jwt;

import com.example.loginservice.etity.Account;
import com.example.loginservice.etity.RefreshToken;
import com.example.loginservice.etity.User;
import com.example.loginservice.global.BaseException;
import com.example.loginservice.repository.UserRepository;
import com.example.loginservice.service.RedisUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.loginservice.global.BaseResponseStatus.BAD_ACCESS;
import static com.example.loginservice.global.BaseResponseStatus.USER_NOT_EXISTS;

@Slf4j
@Component
public class JwtTokenProvider {
    @Value("${my.domain.name}")
    private String domain;
    private final Key key;
    @Autowired
    UserRepository userRepository;

    @Autowired
    RedisUtils redisUtils;
    /**
     * 이상한 조합이 들어올 경우 리프레시토큰을 만료시키는 것을 구현해야함
     * 리프레시 토큰의 블랙리스트...?
     *
     * */

    //객체 생성 -> 비밀키를 적당한 객체로 만들어서 필드로 저장
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    //가입 시 토큰 생성 및 정보 저장
    public String generateAccessToken(Authentication authentication) throws BaseException {
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        //System.out.println("authorities : "+authorities);

        User user = userRepository.findByUsernameAndIsEnabledTrue(authentication.getName()).orElseThrow((()-> new BaseException(USER_NOT_EXISTS)));

        return doGenerateAccessToken(user.getUuid(),Map.of("username",user.getUsername(),"nickname",user.getNickname(),"https://moija.kr/claims/what-role", authorities));
    }
    public String reGenerateAccessToken(String accessToken) {
        Claims claims = parseClaims(accessToken);
        return doGenerateAccessToken(claims.getSubject(),Map.of("username",claims.get("username"),"nickname",claims.get("nickname"),"https://moija.kr/claims/what-role",claims.get("https://moija.kr/claims/what-role")));
    }
    private String doGenerateAccessToken(String id, Map<String,Object> claims){
        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + 240000);//4분 60*4*1000
        return Jwts.builder()
                .setHeaderParam("type","jwt")
                //내 도메인 넣어서 어디용인지 설정
                .setIssuer(domain)
                //언제 생성되었는지
                .setIssuedAt(new Date())
                .setClaims(claims)
                .setSubject(id)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    public String generateRefreshToken(String accessToken) throws BaseException {
        long now = (new Date()).getTime();

        Date refreshTokenExpiresIn = new Date(now + 36000000);
        String refreshToken = Jwts.builder()
                //내 도메인 넣어서 어디용인지 설정
                .setIssuer(domain)
                //언제 생성되었는지
                .setIssuedAt(new Date())
                //userid
                .setSubject(parseClaims(accessToken).getSubject())
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        //db저장용, ttl설정으로 인해 해당시간이 지나면 자동으로 사라짐.
        RefreshToken redisToken = RefreshToken.builder()
                .ttl(2*86400000)
                .token(refreshToken)
                .authId(parseClaims(accessToken).getSubject())
                .build();
        if(redisUtils.existByAuthId(parseClaims(accessToken).getSubject())) {
            redisUtils.updateRefreshToken(redisToken);
            return refreshToken;
        }
        //redis에 refresh token 저장(2일 뒤 만료ttl이 )
        redisUtils.createRefreshToken(redisToken);
        return refreshToken;
    }

    // 액세스토큰으로부터 인증 정보
    public Authentication getAuthentication(String accessToken) {
        // Jwt 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("https://moija.kr/claims/what-role") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기

        Collection<GrantedAuthority> authorities = Arrays.stream(claims.get("https://moija.kr/claims/what-role").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // 인터페이스에 맞춰라~
        UserDetails principal = (UserDetails) new Account((String) claims.get("username"), "", authorities,(String) claims.get("nickname"),claims.getId());
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰 정보를 검증하는 메서드, 유효기간 만료된 토큰만 던짐.
    public boolean validateToken(String token) throws ExpiredJwtException {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            System.out.println("이 토큰은 안돼");
        } catch (UnsupportedJwtException e) {
            System.out.println("내 토큰 아니야");
        } catch (IllegalArgumentException e) {
            System.out.println("클레임이 비어있어.");
        }
        return false;
    }


    // accessToken
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
    public boolean isValidRefreshToken(String refreshToken, String accessToken) throws BaseException {
        try {
            RefreshToken foundRefreshToken = redisUtils.findByToken(refreshToken);
            String memberId = getAuthentication(accessToken).getName();
            Optional<User> user = userRepository.findByUsernameAndIsEnabledTrue(memberId);
            if(user.isEmpty()) {
                throw new BaseException(BAD_ACCESS);
            }

            if (user.get().getUuid().equals(foundRefreshToken.getAuthId())) {
                System.out.println("리프레시 토큰은 유효합니다.");
                return validateToken(foundRefreshToken.getToken());
            }
        } catch (EntityNotFoundException e) {
            return false;
        }
        return false;
    }
}