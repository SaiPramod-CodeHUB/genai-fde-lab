# GenAI / FDE Lab - Sai Pramod Reddy Gopireddy

Hands-on labs from a 3-month Java 21 + Spring Boot 3 + Spring AI + GenAI / FDE study plan
(Nov 2026 - Feb 2027). Each day is a standalone Maven project (Java 21, JDK only, no
external dependencies) with its own notes and verified results.

| Day | Project | Topic | Key result |
|---|---|---|---|
| 1 | `day01-records` | Records, sealed classes, pattern matching | Exhaustive switch over a sealed `PaymentMethod` hierarchy; a stale-`Fees.class` root cause found via `mvn clean` |
| 2 | `day02-virtual-threads` | Virtual threads vs platform threads | 10,000 blocking tasks: 5.17 s (200-thread pool) vs 0.16 s (virtual); pinning demo 103.08 s |
| 3 | `day03-streams` | Streams & Collectors (Java 8) | `groupingBy` fee totals, `partitioningBy`, `Optional`-safe max, parallel vs sequential streams |

## Run a lab

```bash
cd day01-records
mvn -q compile exec:java -Dexec.mainClass="com.saipramod.day01.App"
```

Main classes:
- Day 1: `com.saipramod.day01.App`
- Day 2: `com.saipramod.day02.PlatformBaseline`, `VirtualBaseline`, `PinnedDemo`, `ThreadFacts`, `FutureBridge`
- Day 3: `com.saipramod.day03.StreamsLab`, `ParallelSum`

Each project folder has a `notes.md` with the concepts, checkpoints, and root causes.
Day 1 also has `CODE_WALKTHROUGH.md` - a line-by-line explanation of the code.
