package com.monarchsolutions.sms.dto.student;

public class ValidateStudentExist {
  private Long register_id;
  private Long payment_reference;
  private Long username;
  public Long getRegister_id() {
    return register_id;
  }
  public void setRegister_id(Long register_id) {
    this.register_id = register_id;
  }
  public Long getPayment_reference() {
    return payment_reference;
  }
  public void setPayment_reference(Long payment_reference) {
    this.payment_reference = payment_reference;
  }
  public Long getUsername() {
    return username;
  }
  public void setUsername(Long username) {
    this.username = username;
  }
}
