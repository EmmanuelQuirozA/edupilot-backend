package com.monarchsolutions.sms.dto.paymentRequests;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Map;

public class UpdatePaymentRequest {
  private Long user_school_id;
  private Long pr_school_id;
  private Long responsable_user_id;
  private BigDecimal amount;
  private String  pay_by;
  private String comments;
  private Long payment_status_id;
  private BigDecimal late_fee;
  private String fee_type;
  private int late_fee_frequency;
  private Date payment_month;
  private Boolean partial_payment;

  private Map<String,Object> data;

    public Map<String, Object> getData() {
        return data;
    }
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

  public Long getUser_school_id() {
    return user_school_id;
  }
  public void setUser_school_id(Long user_school_id) {
    this.user_school_id = user_school_id;
  }
  public Long getResponsable_user_id() {
    return responsable_user_id;
  }
  public void setResponsable_user_id(Long responsable_user_id) {
    this.responsable_user_id = responsable_user_id;
  }
  public Long getPr_school_id() {
    return pr_school_id;
  }
  public void setPr_school_id(Long pr_school_id) {
    this.pr_school_id = pr_school_id;
  }
  public BigDecimal getAmount() {
    return amount;
  }
  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }
  public String  getPay_by() {
    return pay_by;
  }
  public void setPay_by(String  pay_by) {
    this.pay_by = pay_by;
  }
  public String getComments() {
    return comments;
  }
  public void setComments(String comments) {
    this.comments = comments;
  }
  public Long getPayment_status_id() {
    return payment_status_id;
  }
  public void setPayment_status_id(Long payment_status_id) {
    this.payment_status_id = payment_status_id;
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
  public Boolean getPartial_payment() {
    return partial_payment;
  }
  public void setPartial_payment(Boolean partial_payment) {
    this.partial_payment = partial_payment;
  }
  
}
