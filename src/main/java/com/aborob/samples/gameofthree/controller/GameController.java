package com.aborob.samples.gameofthree.controller;

import com.aborob.samples.gameofthree.entity.GameState;
import com.aborob.samples.gameofthree.entity.NumberMessage;
import com.aborob.samples.gameofthree.repository.PlayersGameStateSessionsRepository;
import com.aborob.samples.gameofthree.service.GameOfThreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * GameController is responsible for exposing webSockets and pushing notification for sessions.
 */
@Controller
public class GameController {

	@Autowired
	private GameOfThreeService gameOfThreeService;

	@Autowired
	private PlayersGameStateSessionsRepository playersGameStateSessionsRepository;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	private final static String QUEUE_RIVAL_NUMBER = "/queue/rival/number";

	/**
	 * Handles game' first number and assign users as rivals.
	 *
	 * @param numberMessage
	 * @param messageRequest
	 * @throws Exception
	 */
	@MessageMapping("/random_number")
	public void random_number(@Payload NumberMessage numberMessage, Message<Object> messageRequest) throws Exception {

		final String sessionId = messageRequest.getHeaders()
				.get(SimpMessageHeaderAccessor.USER_HEADER, Principal.class).getName();
		if (!validateSession(sessionId)) {
			return;
		}
		final Integer numberStarter = numberMessage.getNumber();

		final NumberMessage numberMessageResponse = gameOfThreeService.startGame(sessionId, numberStarter);

		if (numberMessageResponse.getStatus() == NumberMessage.NO_ERROR) {

			GameState gameState = gameOfThreeService.getGameState(sessionId);
			simpMessagingTemplate.convertAndSendToUser(gameState.getRivalSession(),
					QUEUE_RIVAL_NUMBER, numberMessageResponse);
			numberMessageResponse.setStatus(NumberMessage.WAIT);
			numberMessageResponse.setMessage("Wait rival move!");
		}
		simpMessagingTemplate.convertAndSendToUser(sessionId, QUEUE_RIVAL_NUMBER, numberMessageResponse);

	}

	/**
	 * Handles further additions until game session reset.
	 *
	 * @param numberMessage
	 * @param messageRequest
	 * @throws Exception
	 */
	@MessageMapping("/addition_number")
	public void addition_number(
			@Payload NumberMessage numberMessage, Message<Object> messageRequest) throws Exception {

		final String sessionId = messageRequest.getHeaders()
				.get(SimpMessageHeaderAccessor.USER_HEADER, Principal.class).getName();

		if (!validateSession(sessionId)) {
			return;
		}
		String rivalSessionId = null;

		rivalSessionId = gameOfThreeService.getGameState(sessionId).getRivalSession();

		final NumberMessage numberMessageResponse =
				gameOfThreeService.addNumber(sessionId, numberMessage.getAddition());

		if (numberMessageResponse.getStatus() == NumberMessage.NO_ERROR
				|| numberMessageResponse.getStatus() == NumberMessage.WRONG) {

			simpMessagingTemplate.convertAndSendToUser(rivalSessionId, QUEUE_RIVAL_NUMBER, numberMessageResponse);
			simpMessagingTemplate.convertAndSendToUser(sessionId, QUEUE_RIVAL_NUMBER, numberMessageResponse);
		} else if (numberMessageResponse.getStatus() == NumberMessage.WIN) {
			simpMessagingTemplate.convertAndSendToUser(sessionId, QUEUE_RIVAL_NUMBER, numberMessageResponse);
			numberMessageResponse.setMessage("Loser! .. Refresh to play again!");
			simpMessagingTemplate.convertAndSendToUser(rivalSessionId, QUEUE_RIVAL_NUMBER, numberMessageResponse);
		} else {
			simpMessagingTemplate.convertAndSendToUser(sessionId, QUEUE_RIVAL_NUMBER, numberMessageResponse);
		}

	}

	private boolean validateSession(final String sessionId) {

		final boolean sessionExist = gameOfThreeService.isGameStateExist(sessionId);

		if (!sessionExist) {
			simpMessagingTemplate.convertAndSendToUser(sessionId, QUEUE_RIVAL_NUMBER,
					new NumberMessage(0, 0, NumberMessage.ERROR,
							"Refresh to play again!"));
		}
		return sessionExist;
	}

	/**
	 * Basic general exceptions handler.
	 *
	 * @param e
	 */
	@MessageExceptionHandler
	public void handleException(Throwable e) {

		e.printStackTrace();
	}

}
