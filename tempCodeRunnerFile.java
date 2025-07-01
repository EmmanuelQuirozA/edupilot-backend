List<BreakdownEntry> fees = new ArrayList<>();
fees.add(new BreakdownEntry(
    "Initial Request",
    pr.getPr_pay_by(),
    prAmount.negate(),
    null
));

// 2) build fee entries until balance ≥ 0
BigDecimal running = prAmount.negate();
LocalDate cursor = pr.getPr_pay_by().toLocalDate().plusDays(freq);
LocalDate lastPaymentDate = resp.getPayments().stream()
    .map(p -> p.getPay_created_at().toLocalDate())
    .max(LocalDate::compareTo)
    .orElse(LocalDate.now());

// we’ll keep generating fees day by day until our running balance + payments ≥ 0
while (true) {
  // next fee date:
  if (cursor.isAfter(LocalDate.now()) && cursor.isAfter(lastPaymentDate)) break;

  BigDecimal feeAmt = "$".equals(feeType)
      ? lateFee.negate()
      : prAmount.multiply(lateFee)
          .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)
          .negate();

  fees.add(new BreakdownEntry("Late Fee", cursor.atStartOfDay(), feeAmt, null));
  running = running.add(feeAmt);

  // see if any payments on or before this date push us back ≥ 0
  BigDecimal paidUpToCursor = resp.getPayments().stream()
      .filter(p -> !p.getPay_created_at().toLocalDate().isAfter(cursor))
      .map(PaymentDetail::getAmount)
      .filter(Objects::nonNull)
      .reduce(BigDecimal.ZERO, BigDecimal::add);
  if (running.add(paidUpToCursor).compareTo(BigDecimal.ZERO) >= 0) {
    break;
  }

  cursor = cursor.plusDays(freq);
}

// 3) your payments entries:
List<BreakdownEntry> payments = resp.getPayments().stream()
    .map(p -> new BreakdownEntry(
        "Payment",
        p.getPay_created_at(),
        p.getAmount(),
        null
    ))
    .collect(Collectors.toList());

// 4) merge, sort and recompute final balances:
List<BreakdownEntry> combined = new ArrayList<>(fees);
combined.addAll(payments);
combined.sort(Comparator.comparing(BreakdownEntry::getDate));

running = BigDecimal.ZERO;
for (BreakdownEntry e : combined) {
  running = running.add(e.getAmount());
  e.setBalance(running);
}
resp.setBreakdown(combined);

// 5) now build your summary from exactly those two lists:
BigDecimal sumFees = fees.stream()
    .map(BreakdownEntry::getAmount)
    .reduce(BigDecimal.ZERO, BigDecimal::add)
    .negate();  // fees were negative
BigDecimal sumPayments = payments.stream()
    .map(BreakdownEntry::getAmount)
    .reduce(BigDecimal.ZERO, BigDecimal::add);
BigDecimal finalBalance = running;

resp.setPaymentInfo(List.of(new PaymentInfoSummary(
    sumPayments,                       // totalPaid
    (long) fees.size() - 1,            // periods (exclude the initial request entry)
    sumFees,                           // totalFee
    finalBalance.negate()              // pending (positive meaning “still due”)
)));