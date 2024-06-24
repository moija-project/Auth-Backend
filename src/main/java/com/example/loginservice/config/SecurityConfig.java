package com.example.loginservice.config;

import com.example.loginservice.jwt.JwtAuthenticationFilter;
import com.example.loginservice.jwt.JwtTokenProvider;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        System.out.println("======AuthService ver. cloud-0.4.8======");

        return httpSecurity
                // REST API이므로 basic auth 및 csrf 보안을 사용하지 않음
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                //.formLogin(h -> h.loginPage("/user/login"))
                // JWT를 사용하기 때문에 세션을 사용하지 않음
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> request
                        // 해당 API에 대해서는 모든 요청을 허가
                        .requestMatchers(HttpMethod.GET,"/post/list","/post/page","/post/search","/post/title/**","/post/picture/**","/user/verify-email**","/user/id-dup","/user/n-dup","/user/e-dup").permitAll()
                        .requestMatchers(HttpMethod.POST,"/user/login","/user/join").permitAll()
                        // USER 권한이 있어야 요청할 수 있음
                        .requestMatchers(HttpMethod.PATCH, "/my/**").hasRole("USER")
                        .requestMatchers("/user/**","/post/**","/find/**","/my/**","/message/**").hasRole("USER")
                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                        // 이 밖에 모든 요청에 대해서 인증을 필요로 한다는 설정
                        .anyRequest().authenticated()	// 어떠한 요청이라도 인증필요
                )
                //.httpBasic(Customizer.withDefaults())
                // JWT 인증을 위하여 직접 구현한 필터를 UsernamePasswordAuthenticationFilter 전에 실행
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt Encoder 사용
        BCryptPasswordEncoder scpwd = new BCryptPasswordEncoder();
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


}