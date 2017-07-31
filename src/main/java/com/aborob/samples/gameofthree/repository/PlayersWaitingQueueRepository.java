package com.aborob.samples.gameofthree.repository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface PlayersWaitingQueueRepository {

    public CompletableFuture<Boolean> addPlayer(String sessionId);

    public Optional<String> findWaitingRival(String starterPlayerSession);
}
