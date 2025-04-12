package com.example.SpaceBattleAuth.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    private final Map<UUID, Set<String>> games = new ConcurrentHashMap<>();

    public UUID createGame(List<String> playerUsernames) {
        UUID gameId = UUID.randomUUID();
        games.put(gameId, new HashSet<>(playerUsernames));
        return gameId;
    }

    public boolean isPlayerInGame(UUID gameId, String username) {
        return games.getOrDefault(gameId, Collections.emptySet())
                .contains(username);
    }
}

