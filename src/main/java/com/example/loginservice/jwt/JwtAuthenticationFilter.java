package com.example.loginservice.jwt;//package com.example.moija_project.jwt;

import com.example.loginservice.global.BaseException;
import com.example.loginservice.global.BaseResponse;
import com.example.loginservice.global.BaseResponseStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.util.StringUtils;
import org.springframework.web.ErrorResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;


import static com.example.loginservice.global.BaseResponseStatus.*;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            // 액세스토큰 가지고 왔니????
            String token = resolveAccessToken(request);

            // 유효성 검사해서 넘길지 말지
            try {
                if (token != null && jwtTokenProvider.validateToken(token)) {
                    // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    authentication.getAuthorities().forEach(GrantedAuthority::getAuthority);
                    chain.doFilter(request, response);//재귀 아니고 chain에 있는 함수!!!!!!!!!
                }else {

                    chain.doFilter(request,response);
                }
                //provider 120번째줄 인근에서 던진 exception을 받아서
            } catch (ExpiredJwtException e) {
                String refreshToken = resolveRefreshToken(request);
                //액세스토큰과 같은 사용자임을 확인하기 위해 둘다 사용
                if (jwtTokenProvider.isValidRefreshToken(refreshToken, token)) {
                    //로그아웃 하는 경우 반환의 토큰을 모두 빼앗는다.
                    if(request.getRequestURI().equals("/user/logout")){
                        response.setHeader("Authorization", "Bearer " );
                        response.addCookie(new Cookie("REFRESH_TOKEN", "") {{
                            setPath("/");
                        }});
                    //만료된 액세스토큰임을 공지하고, 새로운 RT / AT를 발급하여 준다.
                    } else {
                        String newAT = jwtTokenProvider.reGenerateAccessToken(token);
                        response.setHeader("Authorization", "Bearer " + newAT );
                        response.addCookie(new Cookie("REFRESH_TOKEN", jwtTokenProvider.generateRefreshToken(token)) {{
                            setPath("/");
                        }});
                    }

                }
                setErrorResponse(response, EXPIRED_ACCESS_TOKEN);
            }
        } catch(BaseException be) {
            setErrorResponse(response, be.getStatus());
        } catch(ExpiredJwtException ee) {
            setErrorResponse(response, LOGIN_EXPIRED);
        }
    }

    public static void setErrorResponse(HttpServletResponse response, BaseResponseStatus responseStatus) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        BaseResponse<ErrorResponse> error = new BaseResponse<>(responseStatus);
        String s = objectMapper.writeValueAsString(error);

        /**
         * 한글 출력을 위해 getWriter() 사용
         */
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(s);
    }

    // Request Header에서 토큰 정보 추출
    private String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    private String resolveRefreshToken(ServletRequest req) throws BaseException {
        HttpServletRequest request = (HttpServletRequest) req;
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals("REFRESH_TOKEN"))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElseThrow();
        }
        throw new BaseException(LOGIN_FIRST);
    }
}