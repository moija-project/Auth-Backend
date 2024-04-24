package com.example.loginservice.extractor;

import com.example.loginservice.global.BaseException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static com.example.loginservice.global.BaseResponseStatus.BAD_ACCESS;

public class Cryptography {
    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    // 문자열을 HMACSHA256으로 암호화하는 메서드
    public static String calculateHmac(String data, String key) throws BaseException {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), HMAC_SHA256_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e ) {
            throw new BaseException(BAD_ACCESS);
        }
    }

    // HMACSHA256 암호화된 값을 복호화하는 메서드
    public static boolean verifyHmac(String data, String key, String hmac) throws BaseException {
        String calculatedHmac = calculateHmac(data, key);
        return calculatedHmac != null && calculatedHmac.equals(hmac);
    }

}
