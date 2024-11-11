package com.hu3diger.order_processor.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @Value("${security.jwt.secret-key}")
    private String secretKey = "eb39a89fdf62c0fe006818e21b8016fcbddbe2383a9b5261a82c188ac7e65ddd";

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration = 3600000; // 1 hour in milliseconds

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtService = new JwtService();
        jwtService.secretKey = secretKey;
        jwtService.jwtExpiration = jwtExpiration;
        userDetails = new User("testUser", "password", new ArrayList<>());
    }

    @Test
    void shouldGenerateToken() {
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtService.getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("testUser", claims.getSubject());
    }

    @Test
    void shouldExtractUsername() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);

        assertEquals("testUser", username);
    }

    @Test
    void shouldValidateToken() {
        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void shouldInvalidateExpiredToken() {
        String token = jwtService.buildToken(new HashMap<>(), userDetails, 1);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertFalse(isValid);
    }


    @Test
    void shouldExtractClaim() {
        String token = jwtService.generateToken(userDetails);
        Date expirationDate = jwtService.extractClaim(token, Claims::getExpiration);

        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void shouldExtractAllClaims() {
        String token = jwtService.generateToken(userDetails);
        Claims claims = jwtService.extractAllClaims(token);

        assertEquals("testUser", claims.getSubject());
    }

    @Test
    void shouldReturnExpirationTime() {
        assertEquals(jwtExpiration, jwtService.getExpirationTime());
    }

    @Test
    void shouldGetSignInKey() {
        Key key = jwtService.getSignInKey();
        assertNotNull(key);
    }
}
