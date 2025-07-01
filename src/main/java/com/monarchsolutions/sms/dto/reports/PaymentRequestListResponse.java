package com.monarchsolutions.sms.dto.reports;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

public class PaymentRequestListResponse {
  private Long payment_request_id;
  private BigDecimal pr_amount;
  private LocalDateTime pr_created_at;
  private LocalDateTime pr_pay_by;
  private String pr_comments;
  private BigDecimal late_fee;
  private String fee_type;
  private int late_fee_frequency;
  private Date payment_month;
  private Long payment_status_id;
  private String partial_payment_transformed;
  private Boolean partial_payment;
  private Long mass_upload;
  private Long pr_payment_status_id;
  private String ps_pr_name;
  private String pt_name;
  private BigDecimal total_amount_payments;
  private BigDecimal late_fee_total;
  private int late_periods;

  public Long getPayment_request_id() {
    return payment_request_id;
  }
  public void setPayment_request_id(Long payment_request_id) {
    this.payment_request_id = payment_request_id;
  }
  
  public BigDecimal getPr_amount() {
    return pr_amount;
  }
  public void setPr_amount(BigDecimal pr_amount) {
    this.pr_amount = pr_amount;
  }
  public LocalDateTime getPr_created_at() {
    return pr_created_at;
  }
  public void setPr_created_at(LocalDateTime pr_created_at) {
    this.pr_created_at = pr_created_at;
  }
  public LocalDateTime getPr_pay_by() {
    return pr_pay_by;
  }
  public void setPr_pay_by(LocalDateTime pr_pay_by) {
    this.pr_pay_by = pr_pay_by;
  }
  public BigDecimal getLate_fee() {
    return late_fee;
  }
  public void setLate_fee(BigDecimal late_fee) {
    this.late_fee = late_fee;
  }
  public String getFee_type() {
    return fee_type;
  }
  public void setFee_type(String fee_type) {
    this.fee_type = fee_type;
  }
  public int getLate_fee_frequency() {
    return late_fee_frequency;
  }
  public void setLate_fee_frequency(int late_fee_frequency) {
    this.late_fee_frequency = late_fee_frequency;
  }
  public Date getPayment_month() {
    return payment_month;
  }
  public void setPayment_month(Date payment_month) {
    this.payment_month = payment_month;
  }
  public String getPs_pr_name() {
    return ps_pr_name;
  }
  public void setPs_pr_name(String ps_pr_name) {
    this.ps_pr_name = ps_pr_name;
  }
  public String getPt_name() {
    return pt_name;
  }
  public void setPt_name(String pt_name) {
    this.pt_name = pt_name;
  }
  public BigDecimal getTotal_amount_payments() {
    return total_amount_payments;
  }
  public void setTotal_amount_payments(BigDecimal total_amount_payments) {
    this.total_amount_payments = total_amount_payments;
  }
  public BigDecimal getLate_fee_total() {
    return late_fee_total;
  }
  public void setLate_fee_total(BigDecimal late_fee_total) {
    this.late_fee_total = late_fee_total;
  }
  public int getLate_periods() {
    return late_periods;
  }
  public void setLate_periods(int late_periods) {
    this.late_periods = late_periods;
  }
  public String getPr_comments() {
    return pr_comments;
  }
  public void setPr_comments(String pr_comments) {
    this.pr_comments = pr_comments;
  }
  public String getPartial_payment_transformed() {
    return partial_payment_transformed;
  }
  public void setPartial_payment_transformed(String partial_payment_transformed) {
    this.partial_payment_transformed = partial_payment_transformed;
  }
  public Long getMass_upload() {
    return mass_upload;
  }
  public void setMass_upload(Long mass_upload) {
    this.mass_upload = mass_upload;
  }
  public Boolean getPartial_payment() {
    return partial_payment;
  }
  public void setPartial_payment(Boolean partial_payment) {
    this.partial_payment = partial_payment;
  }
  public Long getPayment_status_id() {
    return payment_status_id;
  }
  public void setPayment_status_id(Long payment_status_id) {
    this.payment_status_id = payment_status_id;
  }
  public Long getPr_payment_status_id() {
    return pr_payment_status_id;
  }
  public void setPr_payment_status_id(Long pr_payment_status_id) {
    this.pr_payment_status_id = pr_payment_status_id;
  }
  
}

