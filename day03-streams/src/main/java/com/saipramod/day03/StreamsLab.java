package com.saipramod.day03;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.summingDouble;

public class StreamsLab {

    static double fee(Payment p) {
        return switch (p.type()) {
            case "CARD" -> p.amount() * (p.international() ? 0.03 : 0.02);
            case "UPI" -> p.amount() * 0.01;
            default -> 0.0;
        };
    }

    public static void main(String[] args) {
        List<Payment> payments = List.of(
                new Payment("CARD", 100, false),
                new Payment("CARD", 250, false),
                new Payment("CARD", 400, false),
                new Payment("CARD", 500, true),
                new Payment("CARD", 1000, true),
                new Payment("UPI", 60, false),
                new Payment("UPI", 90, false),
                new Payment("CASH", 30, false)
        );

        // Step 4 - total fee by payment type
        Map<String, Double> feeByType = payments.stream()
                .collect(groupingBy(Payment::type, summingDouble(StreamsLab::fee)));
        System.out.println("fee by type: " + feeByType);

        // Step 5 - same grouping, but CARD split into domestic / international
        Map<String, Double> feeByKind = payments.stream()
                .collect(groupingBy(
                        p -> p.type().equals("CARD")
                                ? (p.international() ? "CARD_INTERNATIONAL" : "CARD_DOMESTIC")
                                : p.type(),
                        summingDouble(StreamsLab::fee)));
        System.out.println("fee by kind: " + feeByKind);

        // Step 6a - partition by international: count + amount total per side
        Map<Boolean, List<Payment>> byInternational = payments.stream()
                .collect(partitioningBy(Payment::international));
        byInternational.forEach((intl, list) -> {
            double total = list.stream().mapToDouble(Payment::amount).sum();
            System.out.printf("international=%s -> %d payments, %.1f%n", intl, list.size(), total);
        });

        // Step 6b - max payment, Optional handled safely (never a bare .get())
        Optional<Payment> max = payments.stream().max(comparingDouble(Payment::amount));
        max.ifPresent(p -> System.out.println("max payment: " + p.amount()));
    }
}
