package com.monarchsolutions.sms.dto.paymentRequests;

import java.math.BigDecimal;

public class PendingPaymentSummaryDTO {
  private BigDecimal pendingTotal;
  private BigDecimal lateFeeTotal;

  public PendingPaymentSummaryDTO(BigDecimal pendingTotal, BigDecimal lateFeeTotal) {
    this.pendingTotal = pendingTotal;
    this.lateFeeTotal = lateFeeTotal;
  }

  public PendingPaymentSummaryDTO() {
  }

  public BigDecimal getPendingTotal() {
    return pendingTotal;
  }

  public void setPendingTotal(BigDecimal pendingTotal) {
    this.pendingTotal = pendingTotal;
  }

  public BigDecimal getLateFeeTotal() {
    return lateFeeTotal;
  }

  public void setLateFeeTotal(BigDecimal lateFeeTotal) {
    this.lateFeeTotal = lateFeeTotal;
  }
}
