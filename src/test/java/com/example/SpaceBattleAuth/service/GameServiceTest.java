package com.example.SpaceBattleAuth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameService();
    }

    @Test
    void shouldCreateGameAndCheckPlayers() {
        List<String> players = List.of("player1", "player2");
        UUID gameId = gameService.createGame(players);

        assertNotNull(gameId);
        assertTrue(gameService.isPlayerInGame(gameId, "player1"));
        assertTrue(gameService.isPlayerInGame(gameId, "player2"));
        assertFalse(gameService.isPlayerInGame(gameId, "outsider"));
    }
}
