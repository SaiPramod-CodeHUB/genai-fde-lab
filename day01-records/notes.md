# Day 1 - Records, Sealed Classes, Pattern Matching (Mon Nov 9, 2026)

## Records
- A record is a final class with private final fields, a canonical constructor, accessors named after the components (amount(), not getAmount()), and compiler-generated equals/hashCode/toString - value equality, not identity.
- The compact constructor validates (and can normalise by reassigning the parameter) before the compiler assigns the fields; it cannot assign this.field.
- Money validates in order: amount null -> IAE (checked first, because signum() would NPE on null), amount negative -> IAE, currency null/blank -> IAE.
- BigDecimal for money, never double: 0.1 + 0.2 = 0.30000000000000004 in binary floating point. Note BigDecimal.equals is scale-sensitive: 49.99 != 49.990.
- Records fit DTOs and value objects; they do not fit JPA entities (final class, no no-arg constructor, value equality instead of identity) or inheritance (a record already extends java.lang.Record and is final).

## Sealed classes + pattern matching
- `sealed interface PaymentMethod permits CardPayment, UpiPayment, CashPayment` - a closed set: only the permitted types may implement it, and each subtype must be final, sealed, or non-sealed (records are implicitly final).
- Because the set is closed, a switch over it can be exhaustive with no default; the compiler proves every case is covered.
- Adding a type is a compile-guided change: (1) write the new record, (2) add it to permits - compilation fails until you do, (3) add its case to every exhaustive switch - compilation fails until you do. The failures are the feature.
- A guarded case (case X x when ...) never counts toward exhaustiveness, and a guarded case must come before the general case for the same type, or the compiler reports a dominance error.
- A switch on null throws NullPointerException unless case null is present; with case null, Fees throws a clear IllegalArgumentException instead. null matches no type pattern, so the position of case null is convention, not a compiler requirement.

## Root cause of the day - the stale class file
With the UpiPayment case commented out, the IDE run and an incremental mvn compile disagreed: the build reused a stale Fees.class in target/ and reported BUILD SUCCESS, while mvn clean compile gave the true error - "the switch expression does not cover all possible input values". Lesson: when the IDE and the terminal disagree, suspect stale artifacts; mvn clean is the truth test. A non-exhaustive sealed switch is fixed by restoring the case, never by adding a default.
