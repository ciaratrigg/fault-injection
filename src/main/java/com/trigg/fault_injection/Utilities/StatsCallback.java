package com.trigg.fault_injection.Utilities;

import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.model.Statistics;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class StatsCallback extends ResultCallbackTemplate<StatsCallback, Statistics> {

    private final CompletableFuture<Statistics> future = new CompletableFuture<>();

    @Override
    public void onNext(Statistics stats) {
        future.complete(stats);  // Capture the first statistics object
        try {
            this.close();            // Close stream after first result
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<Statistics> getFuture() {
        return future;
    }
}

