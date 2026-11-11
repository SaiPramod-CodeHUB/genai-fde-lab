package com.saipramod.day02;

public class ThreadFacts {
    public static void main(String[] args) throws InterruptedException {
        Thread virtual = Thread.ofVirtual().start(() -> {
            Thread current = Thread.currentThread();
            System.out.println("virtual isVirtual = " + current.isVirtual());
            System.out.println("virtual isDaemon  = " + current.isDaemon());
        });
        virtual.join();

        Thread platform = Thread.ofPlatform().start(() -> {
            Thread current = Thread.currentThread();
            System.out.println("platform isVirtual = " + current.isVirtual());
            System.out.println("platform isDaemon  = " + current.isDaemon());
        });
        platform.join();
    }
}
