package com.monarchsolutions.sms.dto.reports;

import java.time.LocalDateTime;

public class PaymentRequestBreakdownResponse {
  private Long payment_id;
  private String concept;
  private String amount;
  private LocalDateTime date;
  private String payment_status_name;
  private String validator_full_name;
  public Long getPayment_id() {
    return payment_id;
  }
  public void setPayment_id(Long payment_id) {
    this.payment_id = payment_id;
  }
  public String getConcept() {
    return concept;
  }
  public void setConcept(String concept) {
    this.concept = concept;
  }
  public String getAmount() {
    return amount;
  }
  public void setAmount(String amount) {
    this.amount = amount;
  }
  public LocalDateTime getDate() {
    return date;
  }
  public void setDate(LocalDateTime date) {
    this.date = date;
  }
  public String getPayment_status_name() {
    return payment_status_name;
  }
  public void setPayment_status_name(String payment_status_name) {
    this.payment_status_name = payment_status_name;
  }
  public String getValidator_full_name() {
    return validator_full_name;
  }
  public void setValidator_full_name(String validator_full_name) {
    this.validator_full_name = validator_full_name;
  }
  
}
