package com.aborob.samples.gameofthree.controller;

import com.aborob.samples.gameofthree.model.GameState;
import com.aborob.samples.gameofthree.model.NumberMessage;
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

		String sessionId = messageRequest.getHeaders()
				.get(SimpMessageHeaderAccessor.USER_HEADER, Principal.class).getName();
		if (!this.validateSession(sessionId)) {
			return;
		}
		Integer numberStarter = numberMessage.getNumber();

		NumberMessage numberMessageResponse = this.gameOfThreeService.startGame(sessionId, numberStarter);

		if (numberMessageResponse.getStatus() == NumberMessage.NO_ERROR) {

			GameState gameState = this.playersGameStateSessionsRepository.getGameStateSession(sessionId);
			this.simpMessagingTemplate.convertAndSendToUser(gameState.getRivalSession(),
					QUEUE_RIVAL_NUMBER, numberMessageResponse);
			numberMessageResponse.setStatus(NumberMessage.WAIT);
			numberMessageResponse.setMessage("Wait rival move!");
			this.simpMessagingTemplate.convertAndSendToUser(sessionId, QUEUE_RIVAL_NUMBER, numberMessageResponse);
		} else {
			this.simpMessagingTemplate.convertAndSendToUser(sessionId, QUEUE_RIVAL_NUMBER, numberMessageResponse);
		}

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

		String sessionId = messageRequest.getHeaders()
				.get(SimpMessageHeaderAccessor.USER_HEADER, Principal.class).getName();

		if (!this.validateSession(sessionId)) {
			return;
		}
		String rivalSessionId = null;

		try {

			rivalSessionId = new String(
					this.playersGameStateSessionsRepository.getGameStateSession(sessionId).getRivalSession());
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		NumberMessage numberMessageResponse = this.gameOfThreeService.addNumber(sessionId, numberMessage.getAddition());

		if (numberMessageResponse.getStatus() == NumberMessage.NO_ERROR
				|| numberMessageResponse.getStatus() == NumberMessage.WRONG) {

			this.simpMessagingTemplate.convertAndSendToUser(rivalSessionId,
					QUEUE_RIVAL_NUMBER, numberMessageResponse);
			this.simpMessagingTemplate.convertAndSendToUser(sessionId,
					QUEUE_RIVAL_NUMBER, numberMessageResponse);
		} else if (numberMessageResponse.getStatus() == NumberMessage.WIN) {
			this.simpMessagingTemplate.convertAndSendToUser(sessionId,
					QUEUE_RIVAL_NUMBER, numberMessageResponse);
			numberMessageResponse.setMessage("Loser! .. Refresh to play again!");
			this.simpMessagingTemplate.convertAndSendToUser(rivalSessionId,
					QUEUE_RIVAL_NUMBER, numberMessageResponse);
		} else {
			this.simpMessagingTemplate.convertAndSendToUser(sessionId,
					QUEUE_RIVAL_NUMBER, numberMessageResponse);
		}

	}

	private boolean validateSession(String sessionId) {

		boolean sessionExist = this.playersGameStateSessionsRepository.getGameStateSession(sessionId)
				== null ? false : true;

		if (!sessionExist) {
			this.simpMessagingTemplate.convertAndSendToUser(sessionId, QUEUE_RIVAL_NUMBER,
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
