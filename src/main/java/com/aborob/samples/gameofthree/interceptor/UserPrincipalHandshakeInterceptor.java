package com.aborob.samples.gameofthree.interceptor;

import com.aborob.samples.gameofthree.entity.GameState;
import com.aborob.samples.gameofthree.repository.PlayersGameStateSessionsRepository;
import com.aborob.samples.gameofthree.repository.PlayersWaitingQueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Arrays;
import java.util.Map;

/**
 * Add players to waiting queue by their unique session ID.
 */
public class UserPrincipalHandshakeInterceptor extends DefaultHandshakeHandler {

    @Autowired
    private PlayersWaitingQueueRepository playersWaitingQueueRepository;

    @Autowired
    private PlayersGameStateSessionsRepository playersGameStateSessionsRepository;

    @Override
    protected Principal determineUser(ServerHttpRequest serverHttpRequest, WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {

        Principal principal = serverHttpRequest.getPrincipal();
        if (principal == null) {

            if (serverHttpRequest instanceof ServletServerHttpRequest) {
                ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) serverHttpRequest;
                HttpSession session = servletRequest.getServletRequest().getSession();
                final String sessionId = session.getId();
                this.playersWaitingQueueRepository.addPlayer(sessionId);
                final GameState gameState = new GameState();
                gameState.setGameOn(false);
                this.playersGameStateSessionsRepository.addGameStateSession(sessionId, gameState);
                principal = new UsernamePasswordAuthenticationToken(sessionId, "",
                        Arrays.asList(new SimpleGrantedAuthority("ANONYMOUS")));
            }
        }
        return principal;
    }
}