package com.monarchsolutions.sms.dto.userLogs.payments;

public class ChangePaymentDto {
  private String field;
  private String from;
  private String to;
  private String comments;

  public ChangePaymentDto(String field, String from, String to, String comments) {
    this.field = field;
    this.from = from;
    this.to = to;
    this.comments = comments;
}

  public String getField() {
    return field;
  }
  public void setField(String field) {
    this.field = field;
  }
  public String getFrom() {
    return from;
  }
  public void setFrom(String from) {
    this.from = from;
  }
  public String getTo() {
    return to;
  }
  public void setTo(String to) {
    this.to = to;
  }
  public String getComments() {
    return comments;
  }
  public void setComments(String comments) {
    this.comments = comments;
  }
  
}
