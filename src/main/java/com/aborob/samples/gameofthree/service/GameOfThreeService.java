package com.aborob.samples.gameofthree.service;

import com.aborob.samples.gameofthree.model.NumberMessage;

public interface GameOfThreeService {

    /**
     * Find another player waiting in queue, assign the number and state locks.
     *
     * @param sessionId
     * @param gameNumber
     * @return NumberMessage
     */
    public NumberMessage startGame(String sessionId, Integer gameNumber);

    /**
     * Process numbers addition and validation procedures then release other player lock.
     *
     * @param sessionId
     * @param additionNumber
     * @return NumberMessage
     */
    public NumberMessage addNumber(String sessionId, Integer additionNumber);
}
