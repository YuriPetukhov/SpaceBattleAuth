package com.example.SpaceBattleAuth.dto;

import java.util.UUID;

public record TokenRequest(String username, UUID gameId) {}

