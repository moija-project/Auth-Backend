package com.example.loginservice.service;

import com.example.loginservice.etity.RefreshToken;
import com.example.loginservice.global.BaseException;
import com.example.loginservice.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Ref;
import java.util.Optional;

import static com.example.loginservice.global.BaseResponseStatus.LOGIN_EXPIRED;
import static com.example.loginservice.global.BaseResponseStatus.NOT_EXISTS;

@Service
public class RedisUtils {
    @Autowired
    RefreshTokenRepository tokenRepository;

    public void createRefreshToken(RefreshToken token) {
        tokenRepository.save(token);
    }
    @Transactional
    public void deleteRefreshToken(String token) {
        tokenRepository.deleteByToken(token);
    }

    public boolean existByToken(String token) {
        return tokenRepository.existsByToken(token);
    }
    public void updateRefreshToken(RefreshToken token) throws BaseException {
        Optional<RefreshToken> tokenOptional = Optional.ofNullable(
                tokenRepository.findByAuthId(token.getAuthId())
                .orElseThrow(() -> new BaseException(LOGIN_EXPIRED)));

        if (tokenOptional.isPresent()) {
            token.setTtl(tokenOptional.get().getTtl());
            tokenRepository.save(token);
        }

    }

    public RefreshToken findByToken(String token) throws BaseException {
        Optional<RefreshToken> tokenOp = tokenRepository.findByToken(token);
        if(tokenOp.isPresent()) {
            return tokenOp.get();
        }
        throw new BaseException(NOT_EXISTS);
    }

    public RefreshToken findByAuthId(String userId) throws BaseException {
        Optional<RefreshToken> tokenOp = tokenRepository.findByAuthId(userId);
        if(tokenOp.isPresent())
            return tokenOp.get();
        throw new BaseException(NOT_EXISTS);
    }

    public boolean existByAuthId(String uuid) {
        return tokenRepository.existsByAuthId(uuid);
    }

    @Transactional
    public void deleteByAuthId(String authId) {
        tokenRepository.deleteById(authId);
    }
}