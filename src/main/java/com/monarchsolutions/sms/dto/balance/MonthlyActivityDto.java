package com.monarchsolutions.sms.dto.balance;

import java.util.List;

public class MonthlyActivityDto {
  private int month;               // 1=Jan … 12=Dec
  private List<DailyActivityDto> days;

  // ctor, getters & setters
  public MonthlyActivityDto(int month, List<DailyActivityDto> days) {
      this.month = month;
      this.days = days;
  }
  public int getMonth() {
    return month;
  }
  public void setMonth(int month) {
    this.month = month;
  }
  public List<DailyActivityDto> getDays() {
    return days;
  }
  public void setDays(List<DailyActivityDto> days) {
    this.days = days;
  }
}
