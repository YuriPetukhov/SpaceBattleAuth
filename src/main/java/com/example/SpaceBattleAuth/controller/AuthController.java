package com.example.SpaceBattleAuth.controller;

import com.example.SpaceBattleAuth.dto.GameCreationRequest;
import com.example.SpaceBattleAuth.dto.GameIdResponse;
import com.example.SpaceBattleAuth.dto.TokenRequest;
import com.example.SpaceBattleAuth.dto.TokenResponse;
import com.example.SpaceBattleAuth.service.GameService;
import com.example.SpaceBattleAuth.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final TokenService tokenService;
    private final GameService gameService;

    public AuthController(TokenService tokenService, GameService gameService) {
        this.tokenService = tokenService;
        this.gameService = gameService;
    }

    // Создание новой игры с участниками
    @PostMapping("/create-game")
    public ResponseEntity<GameIdResponse> createGame(@RequestBody GameCreationRequest request) {
        if (request.playerUsernames() == null || request.playerUsernames().size() < 2) {
            return ResponseEntity.badRequest().build();
        }
        UUID gameId = gameService.createGame(request.playerUsernames());
        return ResponseEntity.ok(new GameIdResponse(gameId));
    }

    // Получение jwt токена
    @PostMapping("/token")
    public ResponseEntity<TokenResponse> getToken(@RequestBody TokenRequest request) {
        if (!gameService.isPlayerInGame(request.gameId(), request.username())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String token = tokenService.generateToken(request.username(), request.gameId());
        return ResponseEntity.ok(new TokenResponse(token));
    }
}
