package com.backend_spring.spring_back_test;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.jsonwebtoken.Jwts;
import jakarta.xml.bind.DatatypeConverter;

@SpringBootTest
public class GenerateKeySecretTest {
    @Test
    public void getKeySecret() {
        SecretKey secretKey = Jwts.SIG.HS256.key().build();
        String encodedKey = DatatypeConverter.printHexBinary(secretKey.getEncoded());
        System.out.printf("\nSecret Key: %s\n", encodedKey);
    }

}
