package com.aborob.samples.gameofthree.service;

import com.aborob.samples.gameofthree.model.GameState;
import com.aborob.samples.gameofthree.model.NumberMessage;
import com.aborob.samples.gameofthree.repository.PlayersGameStateSessionsRepository;
import com.aborob.samples.gameofthree.repository.PlayersWaitingQueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("gameOfThreeService")
public class GameOfThreeServiceImpl implements GameOfThreeService {

    @Autowired
    private PlayersWaitingQueueRepository playersWaitingQueueRepository;

    @Autowired
    private PlayersGameStateSessionsRepository playersGameStateSessionsRepository;

    @Override
    public NumberMessage startGame(String sessionId, Integer gameNumber) {

        GameState gameState = this.playersGameStateSessionsRepository.getGameStateSession(sessionId);

        if (gameState.isGameOn()) {
            return new NumberMessage(
                    0, 0, NumberMessage.ERROR, "Game already started!");
        }
        if (gameNumber == null || gameNumber < 2) {
            return new NumberMessage(
                    0, 0, NumberMessage.ERROR, "Wrong number!");
        }

        Optional<String> nullableReceiver = this.playersWaitingQueueRepository.findWaitingRival(sessionId);
        if (!nullableReceiver.isPresent()) {
            return new NumberMessage(
                    0, 0, NumberMessage.ERROR, "Please wait for a rival!");
        }

        String rivalSessionId = nullableReceiver.get();
        this.playersGameStateSessionsRepository.startGameStateSession(
                sessionId, rivalSessionId, new Integer(gameNumber));

        return new NumberMessage(
                gameNumber, 0, NumberMessage.NO_ERROR, "Ok!");
    }

    @Override
    public NumberMessage addNumber(String sessionId, Integer additionNumber) {

        GameState gameState = this.playersGameStateSessionsRepository.getGameStateSession(sessionId);

        if (!gameState.isGameOn()) {
            return new NumberMessage(0, 0, NumberMessage.ERROR,
                    "Please start the game first by choosing a random number!");
        }
        if (gameState.isWaitRivalMove()) {
            return new NumberMessage(
                    gameState.getCurrentNumber(), 0, NumberMessage.WAIT,
                    "Wait rival move!");
        }

        Integer newNumber = new Integer(additionNumber + gameState.getCurrentNumber());
        if (additionNumber > 1 || additionNumber < -1 ||
                (newNumber % 3 != 0)) {
            this.playersGameStateSessionsRepository.switchGameState(
                    sessionId, new Integer(gameState.getCurrentNumber()));
            return new NumberMessage(
                    gameState.getCurrentNumber(), 0, NumberMessage.WRONG,
                    " Was wrong addition number!");
        }

        newNumber = newNumber / 3;
        if (newNumber == 1) {
            this.playersGameStateSessionsRepository.removeGameState(sessionId);
            return new NumberMessage(
                    newNumber, additionNumber, NumberMessage.WIN, "Winner! .. Refresh to play again!");
        } else {
            this.playersGameStateSessionsRepository.switchGameState(sessionId, new Integer(newNumber));
            return new NumberMessage(
                    newNumber, additionNumber, NumberMessage.NO_ERROR, "Ok!");
        }
    }

}
