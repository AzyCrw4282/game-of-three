package com.aborob.samples.gameofthree.service;

import com.aborob.samples.gameofthree.entity.GameState;
import com.aborob.samples.gameofthree.entity.NumberMessage;

public interface GameOfThreeService {

    /**
     * Find another player waiting in queue, assign the number and state locks.
     *
     * @param sessionId
     * @param gameNumber
     * @return NumberMessage
     */
    public NumberMessage startGame(final String sessionId, final Integer gameNumber) throws Exception;

    /**
     * Process numbers addition and validation procedures then release other player lock.
     *
     * @param sessionId
     * @param additionNumber
     * @return NumberMessage
     */
    public NumberMessage addNumber(final String sessionId, final Integer additionNumber) throws Exception;

    /**
     *
     * @param sessionId
     * @return
     */
    public GameState getGameState(final String sessionId) throws Exception;

    /**
     *
     * @param sessionId
     * @return
     */
    public boolean isGameStateExist(final String sessionId);
}
