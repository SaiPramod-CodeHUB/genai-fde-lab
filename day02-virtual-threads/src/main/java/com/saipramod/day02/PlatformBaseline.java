package com.saipramod.day02;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlatformBaseline {
    public static void main(String[] args) {
        int tasks = 10_000;

        long start = System.nanoTime();

        try (ExecutorService pool = Executors.newFixedThreadPool(200)) {
            for (int i = 0; i < tasks; i++) {
                pool.execute(() -> {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }

        double seconds = (System.nanoTime() - start) / 1_000_000_000.0;
        System.out.printf("platform: %,d tasks on 200 threads took %.2f s%n", tasks, seconds);
    }
}
