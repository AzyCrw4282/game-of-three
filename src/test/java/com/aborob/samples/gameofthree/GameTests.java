package com.aborob.samples.gameofthree;

import com.aborob.samples.gameofthree.model.GameState;
import com.aborob.samples.gameofthree.model.NumberMessage;
import com.aborob.samples.gameofthree.repository.PlayersGameStateSessionsRepository;
import com.aborob.samples.gameofthree.repository.PlayersWaitingQueueRepository;
import com.aborob.samples.gameofthree.service.GameOfThreeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GameTests {

    @Autowired
    private GameOfThreeService gameOfThreeService;

    @Autowired
    private PlayersWaitingQueueRepository playersWaitingQueueRepository;

    @Autowired
    private PlayersGameStateSessionsRepository playersGameStateSessionsRepository;

    private final static String TEST_SESSION_1 = "CDBD2387FHC28UCF7GB273FG";
    private final static String TEST_SESSION_2 = "VJ989DYU23HFUWHEDDIUWEWY";
    private final static int GAME_NUMBER = 8;

    @Test
    public void gameTest() throws Exception {

        // Add 2 sessions to queue and initial game states
        this.playersWaitingQueueRepository.addPlayer(TEST_SESSION_1);
        GameState gameState_1 = new GameState();
        gameState_1.setGameOn(false);
        this.playersGameStateSessionsRepository.addGameStateSession(TEST_SESSION_1, gameState_1);
        this.playersWaitingQueueRepository.addPlayer(TEST_SESSION_2);
        GameState gameState_2 = new GameState();
        gameState_2.setGameOn(false);
        this.playersGameStateSessionsRepository.addGameStateSession(TEST_SESSION_2, gameState_2);

        // Start game with random number by session 1
        NumberMessage numberMessage = null;
        numberMessage = this.gameOfThreeService.startGame(TEST_SESSION_1, 1);
        assertThat(numberMessage.getStatus()).isEqualTo(NumberMessage.ERROR);
        numberMessage = this.gameOfThreeService.startGame(TEST_SESSION_1, GAME_NUMBER);
        assertThat(numberMessage.getStatus()).isEqualTo(NumberMessage.NO_ERROR);

        // Trying to start a game while it is already underway
        numberMessage = this.gameOfThreeService.startGame(TEST_SESSION_2, 5);
        assertThat(numberMessage.getStatus()).isEqualTo(NumberMessage.ERROR);

        // Second session move
        numberMessage = this.gameOfThreeService.addNumber(TEST_SESSION_2, 1);
        assertThat(numberMessage.getStatus()).isEqualTo(NumberMessage.NO_ERROR);

        // Second session lock
        numberMessage = this.gameOfThreeService.addNumber(TEST_SESSION_2, 0);
        assertThat(numberMessage.getStatus()).isEqualTo(NumberMessage.WAIT);

        // Winner move by the first session
        numberMessage = this.gameOfThreeService.addNumber(TEST_SESSION_1, 0);
        assertThat(numberMessage.getStatus()).isEqualTo(NumberMessage.WIN);
    }

}
