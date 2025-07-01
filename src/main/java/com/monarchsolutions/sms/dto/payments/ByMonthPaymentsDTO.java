package com.monarchsolutions.sms.dto.payments;

import java.math.BigDecimal;
import java.util.List;

public class ByMonthPaymentsDTO {
  private int month;               // 1=Jan … 12=Dec
  private BigDecimal total;        // sum of this month’s payment amounts
  private List<StudentPaymentsDTO> items;  // your existing DTO

  // ctor, getters & setters
  public ByMonthPaymentsDTO(int month, BigDecimal total, List<StudentPaymentsDTO> items) {
      this.month = month;
      this.total = total;
      this.items = items;
  }
  public int getMonth() {
    return month;
  }
  public void setMonth(int month) {
    this.month = month;
  }
  public BigDecimal getTotal() {
    return total;
  }
  public void setTotal(BigDecimal total) {
    this.total = total;
  }
  public List<StudentPaymentsDTO> getItems() {
    return items;
  }
  public void setItems(List<StudentPaymentsDTO> items) {
    this.items = items;
  }
}
