package com.aborob.samples.gameofthree.repository;

import com.aborob.samples.gameofthree.model.GameState;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.Map;
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
    public CompletableFuture<Boolean> addGameStateSession(String sessionId, GameState gameState) {

        this.players.put(sessionId, gameState);
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public GameState getGameStateSession(String sessionId) {

        return this.players.get(sessionId);
    }

    @Async
    @Override
    public CompletableFuture<Boolean> startGameStateSession(
            String sessionIdStarter, String sessionIdRival, Integer number) {

        GameState gameStateStarter = this.players.get(sessionIdStarter);

        gameStateStarter.setGameOn(true);
        gameStateStarter.setRivalSession(new String(sessionIdRival));
        gameStateStarter.setWaitRivalMove(true);
        gameStateStarter.setCurrentNumber(number);

        GameState secondPlayerGameState = this.players.get(sessionIdRival);
        secondPlayerGameState.setGameOn(true);
        secondPlayerGameState.setRivalSession(new String(sessionIdStarter));
        secondPlayerGameState.setWaitRivalMove(false);
        secondPlayerGameState.setCurrentNumber(number);

        return CompletableFuture.completedFuture(true);
    }

    @Async
    @Override
    public CompletableFuture<Boolean> switchGameState(String sessionIdStarter, Integer NewNumber) {

        GameState gameStateStarter = this.players.get(sessionIdStarter);
        gameStateStarter.setWaitRivalMove(true);
        gameStateStarter.setCurrentNumber(NewNumber);

        GameState secondPlayerGameState = this.players.get(gameStateStarter.getRivalSession());
        secondPlayerGameState.setWaitRivalMove(false);
        secondPlayerGameState.setCurrentNumber(NewNumber);

        return CompletableFuture.completedFuture(true);
    }

    @Async
    @Override
    public CompletableFuture<Boolean> resetGameState(String sessionIdStarter) {

        GameState oldGameState = this.players.get(sessionIdStarter);

        GameState gameStateStarter = new GameState();
        gameStateStarter.setGameOn(false);
        GameState secondPlayerGameState = new GameState();
        secondPlayerGameState.setGameOn(false);
        this.players.put(new String(oldGameState.getRivalSession()), secondPlayerGameState);
        this.players.put(new String(sessionIdStarter), gameStateStarter);

        return CompletableFuture.completedFuture(true);
    }
}
