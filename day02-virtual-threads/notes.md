# Day 2 - Virtual Threads (Tue Nov 10, 2026)

## Load test - 10,000 tasks x Thread.sleep(100) (simulated blocking I/O)

| Executor | Threads used | Wall time |
|---|---|---|
| Fixed platform pool (newFixedThreadPool(200)) | 200 | 5.17 s |
| Virtual thread per task (newVirtualThreadPerTaskExecutor()) | 10,000 | 0.16 s |

- Platform: 10,000 / 200 threads = 50 sequential waves x 100 ms ~ 5 s. The pool ceiling was the limit, not the CPU.
- Virtual: all 10,000 sleeps overlap - when a virtual thread blocks, it unmounts from its carrier (platform) thread, freeing the carrier for other work. ~32x faster on blocking work.

## Pinning demo - 1,000 tasks, sleep(100) inside synchronized on one shared lock

- PinnedDemo result: 103.08 s (vs 0.16 s for 10,000 tasks unpinned).
- Two stacked causes: (1) monitor contention - only one task inside the block at a time, so the work serialises: 1,000 x 100 ms ~ 100 s. (2) Pinning - in Java 21 a virtual thread inside synchronized cannot unmount when it blocks, so its carrier platform thread is held hostage for the whole sleep.
- Rule: never hold a monitor across blocking I/O on a virtual thread; use ReentrantLock instead (a Java-level lock - parking on it unmounts properly). JDK 24 (JEP 491) removes synchronized pinning; on Java 21 the rule stands.
- Diagnostic: java -Djdk.tracePinnedThreads=full -cp target/classes com.saipramod.day02.PinnedDemo prints a stack trace whenever a virtual thread pins.

## Thread facts (verified by run)

- Virtual threads: isVirtual = true, isDaemon = true, always - the JVM will not wait for them; in a main method you join() or the program can exit first. Platform threads: isVirtual = false, isDaemon = false (daemon-ness inherited from the creating thread).
- ThreadLocal values are per virtual thread: 10,000 virtual threads x a big ThreadLocal value = 10,000 copies in the heap. Do not cache big objects (buffers, heavy formatters) in ThreadLocal on virtual threads.
- CompletableFuture (FutureBridge): composable async on a bounded platform-thread pool - same ~5 s as the plain pool, because the pool is still the ceiling. Virtual threads remove the pool from the equation.
