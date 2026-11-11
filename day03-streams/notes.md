# Day 3 - Streams & Collectors (Wed Nov 11, 2026)

## Checkpoints (fill in after running)
- fee by type: expect {CARD=60.0, UPI=1.5, CASH=0.0} (map print order may differ - values are the checkpoint)
- fee by kind: expect {CARD_DOMESTIC=15.0, CARD_INTERNATIONAL=45.0, UPI=1.5, CASH=0.0}
- partition: international=true -> 2 payments, 1500.0 / false -> 6 payments, 930.0
- max payment: 1000.0
- parallel sum: sequential ___ s vs parallel ___ s (both must print 5000000050000000)

## Stream internals - reference paragraph for Step 9 (own-words version still owed)
A stream runs only when a terminal operation (collect, sum, max, forEach) pulls elements through the pipeline - intermediate operations (filter, map, peek) only build the recipe, which is why peek prints nothing until a terminal op runs. Stateless operations (map, filter) handle one element at a time; stateful ones (sorted, distinct) must gather elements before they can emit. Parallel streams split the work across the common ForkJoinPool - roughly one thread per CPU core - so they help CPU-bound work like the 1..100M sum. Virtual threads solve the opposite problem: they make blocking waits cheap by unmounting from the carrier, and they do nothing for CPU-bound work, because no threading trick creates more cores.

## Facts
- groupingBy(classifier, downstream): the classifier makes the keys, the downstream collector reduces each group (summingDouble, counting, toList).
- partitioningBy is groupingBy with a boolean classifier - the map always has both true and false keys, even if one side is empty.
- max/min return Optional - handle with ifPresent / orElseThrow, never a bare .get().
- fee() switches on a String, so it needs a default; the Day 1 Fees switch switched on a sealed type, so it did not. Open set vs closed set, again.
