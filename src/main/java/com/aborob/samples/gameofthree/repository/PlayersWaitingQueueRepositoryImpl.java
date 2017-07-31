package com.aborob.samples.gameofthree.repository;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Waiting players queue.
 */
@Repository("playersWaitingQueueRepository")
public class PlayersWaitingQueueRepositoryImpl implements PlayersWaitingQueueRepository {

    private Set<String> queue = Collections.synchronizedSet(new LinkedHashSet<String>());

    @Async
    @Override
    public CompletableFuture<Boolean> addPlayer(String sessionId) {

        synchronized (this.queue) {

            this.queue.add(new String(sessionId));
        }
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public Optional<String> findWaitingRival(String starterPlayerSession) {

        String receiver = null;
        synchronized (this.queue) {

            if (this.queue.size() >= 2) {

                if (this.queue.remove(starterPlayerSession)) {
                    Iterator<String> iterator = this.queue.iterator();
                    receiver = iterator.next();
                    this.queue.remove(receiver);
                }
            }
        }
        return Optional.ofNullable(receiver);
    }
}
