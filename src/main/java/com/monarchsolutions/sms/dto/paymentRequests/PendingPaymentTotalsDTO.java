package com.monarchsolutions.sms.dto.paymentRequests;

import java.math.BigDecimal;

public class PendingPaymentTotalsDTO {
  private BigDecimal pendingTotalAmount;
  private Long studentsWithPendingCount;

  public PendingPaymentTotalsDTO(BigDecimal pendingTotalAmount, Long studentsWithPendingCount) {
    this.pendingTotalAmount = pendingTotalAmount;
    this.studentsWithPendingCount = studentsWithPendingCount;
  }

  public PendingPaymentTotalsDTO() {
  }

  public BigDecimal getPendingTotalAmount() {
    return pendingTotalAmount;
  }

  public void setPendingTotalAmount(BigDecimal pendingTotalAmount) {
    this.pendingTotalAmount = pendingTotalAmount;
  }

  public Long getStudentsWithPendingCount() {
    return studentsWithPendingCount;
  }

  public void setStudentsWithPendingCount(Long studentsWithPendingCount) {
    this.studentsWithPendingCount = studentsWithPendingCount;
  }
}
