package com.saipramod.day02;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FutureBridge {
    public static void main(String[] args) {
        int tasks = 10_000;

        long start = System.nanoTime();

        try (ExecutorService pool = Executors.newFixedThreadPool(200)) {
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (int i = 0; i < tasks; i++) {
                futures.add(CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }, pool));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }

        double seconds = (System.nanoTime() - start) / 1_000_000_000.0;
        System.out.printf("completableFuture: %,d tasks on a 200-thread pool took %.2f s%n", tasks, seconds);
    }
}
