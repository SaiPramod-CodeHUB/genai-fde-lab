# Day 01: Records, Sealed Types, Pattern Matching (Code Walkthrough)

> Evidence file. Re-read before interviews. Each section covers what, why, and why not the alternative.
> Stack: Java 21 (Temurin 21.0.13), Maven, hand-written pom.xml. Plain Java lab — no Spring Boot in this project.
> Lab: `genai-fde-lab/day01-records`, package `com.saipramod.day01`.

---

## 1. Money.java: record, BigDecimal, compact-constructor validation order
**What:** `public record Money(BigDecimal amount, String currency)` with a compact constructor that validates before the fields are assigned.
**Why:** The record gives immutability plus a generated canonical constructor, accessors (`amount()`, `currency()`), `equals`, `hashCode` and `toString`. BigDecimal is exact (binary `double` makes 0.1 + 0.2 = 0.30000000000000004). Validation order matters: **null check first**, then `amount.signum() < 0`, because calling `signum()` on null throws an NPE and hides the real problem. Currency must be non-null and non-blank. The compact constructor can validate and normalise the parameters, but it cannot assign `this.amount` itself — the compiler assigns the fields automatically after the compact constructor body finishes, and if validation throws, no object is ever created (proven on Day 1: a negative amount threw `IllegalArgumentException` from `Money.<init>`).
**Why not the alternative:** A normal class is about 40 lines of boilerplate that can drift out of sync. `double` loses cents. `new BigDecimal(0.1)` brings in the binary error, so use the String constructor (`new BigDecimal("2.90")`), and compare values with `compareTo`, not `equals`, since `equals` also compares scale (`49.99` vs `49.990` are not equal under `equals`).

## 2. PaymentMethod.java + 3 records: sealed, permits, the subclass rule
**What:** `public sealed interface PaymentMethod permits CardPayment, UpiPayment, CashPayment`, with each implementation written as a record: `CardPayment(String last4, boolean international)`, `UpiPayment(String vpa)`, `CashPayment()`.
**Why:** `sealed` + `permits` closes the hierarchy, so the compiler knows the complete list of subtypes. Every permitted subclass must be `final`, `sealed` or `non-sealed`, and **records are implicitly final**, so they satisfy the rule without extra keywords. A class outside the `permits` list that tries to implement `PaymentMethod` fails at **compile time** — it is a build error, not a runtime exception.
**Why not the alternative:** With an open interface, anyone can add a subtype and no switch can ever be proven complete. Plain classes would also need an explicit `final` and all the boilerplate from section 1.

## 3. Order.java: a record holding a record and a sealed type
**What:** `public record Order(String id, Money total, PaymentMethod paymentMethod)` — a record whose components are a `Money` (a record) and a `PaymentMethod` (a sealed type).
**Why:** Composition of immutable values: the whole Order is immutable and compares by value all the way down, because the nested records bring their own `equals`/`hashCode`. Validation lives in the components — Money checks itself. **Order has no compact constructor and no validation of its own today**; that is an honest gap I could close later with the same compact-constructor pattern.
**Why not the alternative:** A mutable Order with setters can be changed halfway through processing (a race condition, or a total that no longer matches its lines). Re-validating Money inside Order would duplicate logic.

## 4. Fees.java: switch expression, `case null`, guard order, constants, no default
**What:** `return switch (method) { case null -> throw ...; case CardPayment c when c.international() -> INTL_CARD_FEE; case CardPayment c -> CARD_FEE; case UpiPayment u -> BigDecimal.ZERO; case CashPayment c -> CASH_FEE; };` — verified fees on Day 1: international card 2.90, domestic card 1.50, UPI 0, cash 0.50.
**Why:** A switch *expression* returns a value, with no `break`, no fall-through and no missing return path. `case null` first turns a vague NPE into a clear `IllegalArgumentException` (pattern matching never matches `null` implicitly — a switch on `null` without `case null` throws `NullPointerException`). The **guarded case comes before the general case**, otherwise the general case dominates it and the compiler rejects the guarded one. Note the other direction of the same rule: a guarded case never counts toward exhaustiveness, because the guard can be false at runtime (a domestic card) — the unguarded case underneath is what actually covers `CardPayment`. Fees are `private static final` constants: BigDecimal is immutable, so one shared instance is thread-safe, there is no repeated allocation, and the fees are named in one place. Production note: fees owned by the business belong in config or a database, not hard-coded.
**Why not default:** With a sealed type, the case list *is* the proof of completeness. A `default` would silently absorb a future `WalletPayment` and return a wrong fee in production instead of breaking the build.

## 5. Describer.java: instanceof patterns, flow scoping, closed vs open set
**What:** `Describer.describe(Object o)`: an explicit null check first (`null` falls through every `instanceof`, since `null instanceof X` is always false — and calling `o.getClass()` on null would NPE), then `o instanceof String s` and `o instanceof Money money` patterns, with a fallback returning `o.getClass().getSimpleName()`. Verified outputs on Day 1: `nothing: null value` / `text: hello (length 5)` / `money: 10.00 USD` / `something else: Integer` (the 42 was autoboxed).
**Why:** The pattern does the type test, cast and binding in one step — no manual `(Money) o` cast, which is where ClassCastExceptions used to come from. **Flow scoping:** the binding (`money`) exists only where the match is definitely true; the same rule, as a general pattern, also works after a negated early return (`if (!(o instanceof Money money)) return ...; money.amount();`).
**Closed vs open set:** For a **closed** (sealed) set, use a switch, because the compiler checks completeness. `instanceof` chains suit **open** sets like `Object`, where completeness can't be known — but the compiler won't catch a missing type there, so a fallback is on me.

## 6. The exhaustiveness experiment
**What I did:** Commented out `case UpiPayment upi -> BigDecimal.ZERO;` (Fees.java line 18) and ran `mvn clean compile`.
**Exact error:** `Fees.java:[14,16] the switch expression does not cover all possible input values`
**Follow-up:** Restored the UPI case — deliberately *not* a `default` — and the build passed again.
**What it proved:** Sealed + switch with no default = **compiler-enforced completeness**. A missing case is a build failure, not a production bug.
**Honest note:** The guarded/general case-order swap was *not* run as an experiment on Day 1 — case order is covered as a rule in section 4, not as a run result here.

## 7. The stale `target/` root cause
**Symptom:** Running the app threw `java.lang.Error: Unresolved compilation problem: A switch expression should have a default case` at `Fees.feeFor` (Fees.java:14) — a placeholder class produced by the IDE compiler — while terminal `mvn compile` reported BUILD SUCCESS. The two disagreed about the same code.
**Diagnosis order:** (1) `pwd`: confirm I'm in the right project folder (`/Users/saipramodreddy/genai-fde-lab/day01-records`). (2) `grep` the source on disk: line 18 showed the UPI case commented out, no `default` anywhere, and `sealed ... permits` present in PaymentMethod.java. (3) `mvn clean compile`: wipe `target/` and rebuild from source — this produced the real javac exhaustiveness error.
**Root cause:** Maven's incremental compilation reused the stale `Fees.class` already sitting in `target/` and never recompiled `Fees.java`. Terminal and IDE were executing two different builds of the truth.
**Lesson (one line):** *When the IDE and the build disagree, trust the clean build: check location, check the file on disk, then `mvn clean compile` — and fix it there rather than in the IDE.*
