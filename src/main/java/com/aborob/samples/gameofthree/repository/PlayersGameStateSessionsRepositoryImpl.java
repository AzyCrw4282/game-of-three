package com.aborob.samples.gameofthree.repository;

import com.aborob.samples.gameofthree.entity.GameState;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Asynchronous sessions and players game states repository and processor.
 */
@Repository("playersGameStateSessionsRepository")
public class PlayersGameStateSessionsRepositoryImpl implements PlayersGameStateSessionsRepository {

    private Map<String, GameState> players = new ConcurrentHashMap<>();

    @Async
    @Override
    public CompletableFuture<Boolean> addGameStateSession(final String sessionId, final GameState gameState) {

        players.put(sessionId, gameState);
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public Optional<GameState> getGameStateSession(final String sessionId) {

        return Optional.ofNullable(players.get(sessionId));
    }

    @Override
    public boolean isGameStateSessionExist(final String sessionId) {

        return players.containsKey(sessionId);
    }

    @Async
    @Override
    public CompletableFuture<Boolean> startGameStateSession(
            final String sessionIdStarter, final String sessionIdRival, final Integer number) {

        GameState gameStateStarter = players.get(sessionIdStarter);

        gameStateStarter.setGameOn(true);
        gameStateStarter.setRivalSession(sessionIdRival);
        gameStateStarter.setWaitRivalMove(true);
        gameStateStarter.setCurrentNumber(number);

        GameState secondPlayerGameState = players.get(sessionIdRival);
        secondPlayerGameState.setGameOn(true);
        secondPlayerGameState.setRivalSession(sessionIdStarter);
        secondPlayerGameState.setWaitRivalMove(false);
        secondPlayerGameState.setCurrentNumber(number);

        return CompletableFuture.completedFuture(true);
    }

    @Async
    @Override
    public CompletableFuture<Boolean> switchGameState(final String sessionIdStarter, final Integer NewNumber) {

        GameState gameStateStarter = players.get(sessionIdStarter);
        gameStateStarter.setWaitRivalMove(true);
        gameStateStarter.setCurrentNumber(NewNumber);

        GameState secondPlayerGameState = players.get(gameStateStarter.getRivalSession());
        secondPlayerGameState.setWaitRivalMove(false);
        secondPlayerGameState.setCurrentNumber(NewNumber);

        return CompletableFuture.completedFuture(true);
    }

    @Async
    @Override
    public CompletableFuture<Boolean> removeGameState(final String sessionIdStarter) {

        GameState oldGameState = players.get(sessionIdStarter);
        players.remove(oldGameState.getRivalSession());
        players.remove(sessionIdStarter);
        return CompletableFuture.completedFuture(true);
    }
}
