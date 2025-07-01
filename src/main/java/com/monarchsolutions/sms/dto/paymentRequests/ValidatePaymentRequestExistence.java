package com.monarchsolutions.sms.dto.paymentRequests;

public class ValidatePaymentRequestExistence {
  private Long student_id;
  private String full_name;
  public Long getStudent_id() {
    return student_id;
  }
  public void setStudent_id(Long student_id) {
    this.student_id = student_id;
  }
  public String getFull_name() {
    return full_name;
  }
  public void setFull_name(String full_name) {
    this.full_name = full_name;
  }
}
