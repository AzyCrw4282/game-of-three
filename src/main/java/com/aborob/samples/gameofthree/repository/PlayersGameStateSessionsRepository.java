package com.aborob.samples.gameofthree.repository;

import com.aborob.samples.gameofthree.model.GameState;

import java.util.concurrent.CompletableFuture;

public interface PlayersGameStateSessionsRepository {

    public CompletableFuture<Boolean> addGameStateSession(String sessionId, GameState gameState);

    public GameState getGameStateSession(String sessionId);

    public CompletableFuture<Boolean> startGameStateSession(
            String sessionIdStarter, String sessionIdRival, Integer number);

    public CompletableFuture<Boolean> switchGameState(String sessionIdStarter, Integer NewNumber);

    public CompletableFuture<Boolean> resetGameState(String sessionIdStarter);
}
