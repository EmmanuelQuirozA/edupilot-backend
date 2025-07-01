package com.monarchsolutions.sms.dto.paymentRequests;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreatePaymentRequestDTO {
    private Integer    student_id;
    private Integer    created_by;
    private Integer    payment_concept_id;
    private BigDecimal amount;
    private LocalDate  pay_by;
    private String     comments;
    private BigDecimal late_fee;
    private String     fee_type;
    private String     late_fee_frequency;
    private String     payment_month;
    private Boolean    partial_payment;
    public Integer getStudent_id() {
      return student_id;
    }
    public void setStudent_id(Integer student_id) {
      this.student_id = student_id;
    }
    public Integer getCreated_by() {
      return created_by;
    }
    public void setCreated_by(Integer created_by) {
      this.created_by = created_by;
    }
    public Integer getPayment_concept_id() {
      return payment_concept_id;
    }
    public void setPayment_concept_id(Integer payment_concept_id) {
      this.payment_concept_id = payment_concept_id;
    }
    public BigDecimal getAmount() {
      return amount;
    }
    public void setAmount(BigDecimal amount) {
      this.amount = amount;
    }
    public LocalDate getPay_by() {
      return pay_by;
    }
    public void setPay_by(LocalDate pay_by) {
      this.pay_by = pay_by;
    }
    public String getComments() {
      return comments;
    }
    public void setComments(String comments) {
      this.comments = comments;
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
    public String getLate_fee_frequency() {
      return late_fee_frequency;
    }
    public void setLate_fee_frequency(String late_fee_frequency) {
      this.late_fee_frequency = late_fee_frequency;
    }
    public String getPayment_month() {
      return payment_month;
    }
    public void setPayment_month(String payment_month) {
      this.payment_month = payment_month;
    }
    public Boolean getPartial_payment() {
      return partial_payment;
    }
    public void setPartial_payment(Boolean partial_payment) {
      this.partial_payment = partial_payment;
    }
    
}
