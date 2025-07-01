package com.monarchsolutions.sms.dto.payments;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StudentPaymentsDTO {
  private Integer        paymentId;
  private Integer        studentId;
  private Integer        schoolId;
  private LocalDate      paymentMonth;
  private BigDecimal     amount;
  private Integer        paymentStatusId;
  private Integer        paymentThroughId;
  private Integer        paymentConceptId;
  private LocalDateTime  validatedAt;
  private LocalDateTime  paymentCreatedAt;
  private LocalDateTime  updatedAt;
  private String         comments;
  private String         partConceptName;
  private String         paymentThroughName;
  private String         paymentStatusName;
  private String         validatorFullName;
  private Integer        paymentRequestId;
  private String         receiptPath;
  private String         receiptFileName;
  
  public Integer getPaymentId() {
    return paymentId;
  }
  public void setPaymentId(Integer paymentId) {
    this.paymentId = paymentId;
  }
  public Integer getStudentId() {
    return studentId;
  }
  public void setStudentId(Integer studentId) {
    this.studentId = studentId;
  }
  public Integer getSchoolId() {
    return schoolId;
  }
  public void setSchoolId(Integer schoolId) {
    this.schoolId = schoolId;
  }
  public LocalDate getPaymentMonth() {
    return paymentMonth;
  }
  public void setPaymentMonth(LocalDate paymentMonth) {
    this.paymentMonth = paymentMonth;
  }
  public BigDecimal getAmount() {
    return amount;
  }
  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }
  public Integer getPaymentStatusId() {
    return paymentStatusId;
  }
  public void setPaymentStatusId(Integer paymentStatusId) {
    this.paymentStatusId = paymentStatusId;
  }
  public Integer getPaymentThroughId() {
    return paymentThroughId;
  }
  public void setPaymentThroughId(Integer paymentThroughId) {
    this.paymentThroughId = paymentThroughId;
  }
  public Integer getPaymentConceptId() {
    return paymentConceptId;
  }
  public void setPaymentConceptId(Integer paymentConceptId) {
    this.paymentConceptId = paymentConceptId;
  }
  public LocalDateTime getValidatedAt() {
    return validatedAt;
  }
  public void setValidatedAt(LocalDateTime validatedAt) {
    this.validatedAt = validatedAt;
  }
  public LocalDateTime getPaymentCreatedAt() {
    return paymentCreatedAt;
  }
  public void setPaymentCreatedAt(LocalDateTime paymentCreatedAt) {
    this.paymentCreatedAt = paymentCreatedAt;
  }
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
  public String getComments() {
    return comments;
  }
  public void setComments(String comments) {
    this.comments = comments;
  }
  public String getPartConceptName() {
    return partConceptName;
  }
  public void setPartConceptName(String partConceptName) {
    this.partConceptName = partConceptName;
  }
  public String getPaymentThroughName() {
    return paymentThroughName;
  }
  public void setPaymentThroughName(String paymentThroughName) {
    this.paymentThroughName = paymentThroughName;
  }
  public String getPaymentStatusName() {
    return paymentStatusName;
  }
  public void setPaymentStatusName(String paymentStatusName) {
    this.paymentStatusName = paymentStatusName;
  }
  public String getValidatorFullName() {
    return validatorFullName;
  }
  public void setValidatorFullName(String validatorFullName) {
    this.validatorFullName = validatorFullName;
  }
  public Integer getPaymentRequestId() {
    return paymentRequestId;
  }
  public void setPaymentRequestId(Integer paymentRequestId) {
    this.paymentRequestId = paymentRequestId;
  }
  public String getReceiptPath() {
    return receiptPath;
  }
  public void setReceiptPath(String receiptPath) {
    this.receiptPath = receiptPath;
  }
  public String getReceiptFileName() {
    return receiptFileName;
  }
  public void setReceiptFileName(String receiptFileName) {
    this.receiptFileName = receiptFileName;
  }
}
