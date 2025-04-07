package com.example.SpaceBattleAuth.dto;

import java.util.List;

public record GameCreationRequest(List<String> playerUsernames) {}
