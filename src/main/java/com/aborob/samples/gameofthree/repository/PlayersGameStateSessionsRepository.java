package com.aborob.samples.gameofthree.repository;

import com.aborob.samples.gameofthree.entity.GameState;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface PlayersGameStateSessionsRepository {

    public CompletableFuture<Boolean> addGameStateSession(final String sessionId, final GameState gameState);

    public Optional<GameState> getGameStateSession(final String sessionId);

    public boolean isGameStateSessionExist(final String sessionId);

    public CompletableFuture<Boolean> startGameStateSession(
            final String sessionIdStarter, final String sessionIdRival, final Integer number);

    public CompletableFuture<Boolean> switchGameState(final String sessionIdStarter, final Integer NewNumber);

    public CompletableFuture<Boolean> removeGameState(final String sessionIdStarter);
}
