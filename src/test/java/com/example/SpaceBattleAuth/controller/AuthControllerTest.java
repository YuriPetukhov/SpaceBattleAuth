package com.example.SpaceBattleAuth.controller;

import com.example.SpaceBattleAuth.config.TestSecurityConfig;
import com.example.SpaceBattleAuth.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createGame_ShouldReturnGameId_WhenValidRequest() throws Exception {
        GameCreationRequest request = new GameCreationRequest(List.of("player1", "player2"));

        mockMvc.perform(post("/api/auth/create-game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").exists());
    }

    @Test
    void createGameAndGetToken_ShouldWorkCorrectly() throws Exception {
        // 1. Создаем игру
        GameCreationRequest createRequest = new GameCreationRequest(
                List.of("player1", "player2", "player3")
        );

        MvcResult createResult = mockMvc.perform(post("/api/auth/create-game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn();

        UUID gameId = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                GameIdResponse.class
        ).gameId();

        // 2. Получаем токен для каждого игрока
        for (String username : createRequest.playerUsernames()) {
            mockMvc.perform(post("/api/auth/token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    new TokenRequest(username, gameId))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists());
        }

        // 3. Проверяем отказ для несуществующего игрока
        mockMvc.perform(post("/api/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new TokenRequest("unknown-player", gameId))))
                .andExpect(status().isForbidden());
    }

    @Test
    void createGame_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Случай 1: Менее 2 игроков
        mockMvc.perform(post("/api/auth/create-game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new GameCreationRequest(List.of("single-player")))))
                .andExpect(status().isBadRequest());

        // Случай 2: Null вместо списка игроков
        mockMvc.perform(post("/api/auth/create-game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new GameCreationRequest(null))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getToken_ShouldReturnForbidden_ForNonParticipant() throws Exception {
        // 1. Создаем игру
        UUID gameId = createTestGame(List.of("player1", "player2"));

        // 2. Пробуем получить токен для неучастника
        mockMvc.perform(post("/api/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new TokenRequest("hacker", gameId))))
                .andExpect(status().isForbidden());
    }

    private UUID createTestGame(List<String> players) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/create-game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new GameCreationRequest(players))))
                .andReturn();

        return objectMapper.readValue(
                result.getResponse().getContentAsString(),
                GameIdResponse.class
        ).gameId();
    }
}