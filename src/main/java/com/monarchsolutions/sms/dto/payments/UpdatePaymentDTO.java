package com.monarchsolutions.sms.dto.payments;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

public class UpdatePaymentDTO {
  private Long payment_id;
  private Long payment_concept_id;
  private Date payment_month;
  private BigDecimal amount;
  private Long payment_status_id;
  private String validated_at;
  private LocalDateTime created_at;
  private LocalDateTime payment_date;
  private String comments;
  private Long payment_request_id;
  private Long payment_through_id;
  private String receipt_path;
  private String receipt_file_name;
  public Long getPayment_id() {
    return payment_id;
  }
  public void setPayment_id(Long payment_id) {
    this.payment_id = payment_id;
  }
  public Long getPayment_concept_id() {
    return payment_concept_id;
  }
  public void setPayment_concept_id(Long payment_concept_id) {
    this.payment_concept_id = payment_concept_id;
  }
  public Date getPayment_month() {
    return payment_month;
  }
  public void setPayment_month(Date payment_month) {
    this.payment_month = payment_month;
  }
  public BigDecimal getAmount() {
    return amount;
  }
  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }
  public Long getPayment_status_id() {
    return payment_status_id;
  }
  public void setPayment_status_id(Long payment_status_id) {
    this.payment_status_id = payment_status_id;
  }
  public String getValidated_at() {
    return validated_at;
  }
  public void setValidated_at(String validated_at) {
    this.validated_at = validated_at;
  }
  public LocalDateTime getCreated_at() {
    return created_at;
  }
  public void setCreated_at(LocalDateTime created_at) {
    this.created_at = created_at;
  }
  public String getComments() {
    return comments;
  }
  public void setComments(String comments) {
    this.comments = comments;
  }
  public Long getPayment_request_id() {
    return payment_request_id;
  }
  public void setPayment_request_id(Long payment_request_id) {
    this.payment_request_id = payment_request_id;
  }
  public Long getPayment_through_id() {
    return payment_through_id;
  }
  public void setPayment_through_id(Long payment_through_id) {
    this.payment_through_id = payment_through_id;
  }
  public String getReceipt_path() {
    return receipt_path;
  }
  public void setReceipt_path(String receipt_path) {
    this.receipt_path = receipt_path;
  }
  public String getReceipt_file_name() {
    return receipt_file_name;
  }
  public void setReceipt_file_name(String receipt_file_name) {
    this.receipt_file_name = receipt_file_name;
  }
  public LocalDateTime getPayment_date() {
    return payment_date;
  }
  public void setPayment_date(LocalDateTime payment_date) {
    this.payment_date = payment_date;
  }
  
}
