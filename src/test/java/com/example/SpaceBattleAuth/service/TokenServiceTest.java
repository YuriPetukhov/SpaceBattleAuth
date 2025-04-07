package com.example.SpaceBattleAuth.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.crypto.SecretKey;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class TokenServiceTest {

    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    private TokenService tokenService;

    private SecretKey key;

    @BeforeEach
    void setUp() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    @Test
    void contextLoads() {
        assertNotNull(tokenService);
    }

    @Test
    void shouldGenerateAndValidateToken() {
        String token = tokenService.generateToken("testUser", UUID.randomUUID());
        assertTrue(tokenService.validateToken(token));
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        String username = "testUser";
        UUID gameId = UUID.randomUUID();

        String token = tokenService.generateToken(username, gameId);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        var claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(username, claims.getSubject());
        assertEquals(gameId.toString(), claims.get("gameId"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void validateToken_ShouldReturnTrueForValidToken() {
        String username = "testUser";
        UUID gameId = UUID.randomUUID();
        String token = tokenService.generateToken(username, gameId);

        assertTrue(tokenService.validateToken(token));
    }

    @Test
    void validateToken_ShouldReturnFalseForInvalidToken() {
        assertFalse(tokenService.validateToken("invalid.token.string"));
    }

    @Test
    void getUsernameFromToken_ShouldReturnCorrectUsername() {
        String username = "testUser";
        UUID gameId = UUID.randomUUID();
        String token = tokenService.generateToken(username, gameId);

        assertEquals(username, tokenService.getUsernameFromToken(token));
    }

    @Test
    void getGameIdFromToken_ShouldReturnCorrectGameId() {
        String username = "testUser";
        UUID gameId = UUID.randomUUID();
        String token = tokenService.generateToken(username, gameId);

        assertEquals(gameId, tokenService.getGameIdFromToken(token));
    }
}