package com.saipramod.day03;

import java.util.stream.LongStream;

public class ParallelSum {
    public static void main(String[] args) {
        long startSeq = System.nanoTime();
        long sequential = LongStream.rangeClosed(1, 100_000_000).sum();
        double seqSeconds = (System.nanoTime() - startSeq) / 1_000_000_000.0;

        long startPar = System.nanoTime();
        long parallel = LongStream.rangeClosed(1, 100_000_000).parallel().sum();
        double parSeconds = (System.nanoTime() - startPar) / 1_000_000_000.0;

        System.out.printf("sequential: %d in %.2f s%n", sequential, seqSeconds);
        System.out.printf("parallel:   %d in %.2f s%n", parallel, parSeconds);
    }
}
