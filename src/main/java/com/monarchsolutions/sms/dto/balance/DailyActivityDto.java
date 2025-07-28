package com.monarchsolutions.sms.dto.balance;

import java.util.List;

public class DailyActivityDto {
  private int day;
  private List<AccountActivityRecord> items;

  // ctor, getters & setters
  public DailyActivityDto(int day, List<AccountActivityRecord> items) {
    this.day = day;
    this.items = items;
  }
  public int getDay() {
    return day;
  }
  public void setDay(int day) {
    this.day = day;
  }
  public List<AccountActivityRecord> getItems() {
    return items;
  }
  public void setItems(List<AccountActivityRecord> items) {
    this.items = items;
  }
}
