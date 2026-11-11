package com.saipramod.day02;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PinnedDemo {
    private static final Object LOCK = new Object();

    public static void main(String[] args) {
        int tasks = 1_000;

        long start = System.nanoTime();

        try (ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < tasks; i++) {
                pool.execute(() -> {
                    synchronized (LOCK) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                });
            }
        }

        double seconds = (System.nanoTime() - start) / 1_000_000_000.0;
        System.out.printf("pinned: %,d tasks, sleep inside synchronized, took %.2f s%n", tasks, seconds);
    }
}
