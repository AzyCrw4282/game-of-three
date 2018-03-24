package com.aborob.samples.gameofthree.repository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface PlayersWaitingQueueRepository {

    public CompletableFuture<Boolean> addPlayer(final String sessionId);

    public Optional<String> findWaitingRival(final String starterPlayerSession);
}
