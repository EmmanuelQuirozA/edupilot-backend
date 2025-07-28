package com.monarchsolutions.sms.dto.balance;

import java.util.List;

public class YearlyActivityDto {
  private int                      year;
  private List<MonthlyActivityDto> months;

  // ctor, getters & setters
  public YearlyActivityDto(int year, List<MonthlyActivityDto> months) {
      this.year   = year;
      this.months = months;
  }
  public int getYear() {
    return year;
  }
  public void setYear(int year) {
    this.year = year;
  }
  public List<MonthlyActivityDto> getMonths() {
    return months;
  }
  public void setMonths(List<MonthlyActivityDto> months) {
    this.months = months;
  }
}
