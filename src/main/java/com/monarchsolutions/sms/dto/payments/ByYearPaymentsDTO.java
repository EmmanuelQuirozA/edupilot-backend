package com.monarchsolutions.sms.dto.payments;

import java.util.List;

public class ByYearPaymentsDTO {
  private int year;
  private List<ByMonthPaymentsDTO> months;

  // ctor, getters & setters
  public ByYearPaymentsDTO(int year, List<ByMonthPaymentsDTO> months) {
      this.year   = year;
      this.months = months;
  }
  public int getYear() {
    return year;
  }
  public void setYear(int year) {
    this.year = year;
  }
  public List<ByMonthPaymentsDTO> getMonths() {
    return months;
  }
  public void setMonths(List<ByMonthPaymentsDTO> months) {
    this.months = months;
  }
}
