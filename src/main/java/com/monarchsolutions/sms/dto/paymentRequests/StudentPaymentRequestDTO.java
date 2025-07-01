package com.monarchsolutions.sms.dto.paymentRequests;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StudentPaymentRequestDTO {
    private Integer       paymentRequestId;
    private String        paymentReference;
    private String        studentFullName;
    private String        generation;
    private String        scholarLevelName;
    private String        gradeGroup;
    private BigDecimal    prAmount;
    private LocalDateTime prCreatedAt;
    private LocalDateTime prPayBy;
    private BigDecimal    lateFee;
    private String        feeType;
    private Integer       lateFeeFrequency;
    private LocalDate     paymentMonth;
    private Integer       studentId;
    private Integer       paymentStatusId;
    private String        psPrName;
    private String        ptName;
    private BigDecimal    totalAmountPayments;
    private LocalDateTime latestPaymentDate;
    private BigDecimal    lateFeeTotal;
    public Integer getPaymentRequestId() {
      return paymentRequestId;
    }
    public void setPaymentRequestId(Integer paymentRequestId) {
      this.paymentRequestId = paymentRequestId;
    }
    public String getPaymentReference() {
      return paymentReference;
    }
    public void setPaymentReference(String paymentReference) {
      this.paymentReference = paymentReference;
    }
    public String getStudentFullName() {
      return studentFullName;
    }
    public void setStudentFullName(String studentFullName) {
      this.studentFullName = studentFullName;
    }
    public String getGeneration() {
      return generation;
    }
    public void setGeneration(String generation) {
      this.generation = generation;
    }
    public String getScholarLevelName() {
      return scholarLevelName;
    }
    public void setScholarLevelName(String scholarLevelName) {
      this.scholarLevelName = scholarLevelName;
    }
    public String getGradeGroup() {
      return gradeGroup;
    }
    public void setGradeGroup(String gradeGroup) {
      this.gradeGroup = gradeGroup;
    }
    public BigDecimal getPrAmount() {
      return prAmount;
    }
    public void setPrAmount(BigDecimal prAmount) {
      this.prAmount = prAmount;
    }
    public LocalDateTime getPrCreatedAt() {
      return prCreatedAt;
    }
    public void setPrCreatedAt(LocalDateTime prCreatedAt) {
      this.prCreatedAt = prCreatedAt;
    }
    public LocalDateTime getPrPayBy() {
      return prPayBy;
    }
    public void setPrPayBy(LocalDateTime prPayBy) {
      this.prPayBy = prPayBy;
    }
    public BigDecimal getLateFee() {
      return lateFee;
    }
    public void setLateFee(BigDecimal lateFee) {
      this.lateFee = lateFee;
    }
    public String getFeeType() {
      return feeType;
    }
    public void setFeeType(String feeType) {
      this.feeType = feeType;
    }
    public Integer getLateFeeFrequency() {
      return lateFeeFrequency;
    }
    public void setLateFeeFrequency(Integer lateFeeFrequency) {
      this.lateFeeFrequency = lateFeeFrequency;
    }
    public LocalDate getPaymentMonth() {
      return paymentMonth;
    }
    public void setPaymentMonth(LocalDate paymentMonth) {
      this.paymentMonth = paymentMonth;
    }
    public Integer getStudentId() {
      return studentId;
    }
    public void setStudentId(Integer studentId) {
      this.studentId = studentId;
    }
    public Integer getPaymentStatusId() {
      return paymentStatusId;
    }
    public void setPaymentStatusId(Integer paymentStatusId) {
      this.paymentStatusId = paymentStatusId;
    }
    public String getPsPrName() {
      return psPrName;
    }
    public void setPsPrName(String psPrName) {
      this.psPrName = psPrName;
    }
    public String getPtName() {
      return ptName;
    }
    public void setPtName(String ptName) {
      this.ptName = ptName;
    }
    public BigDecimal getTotalAmountPayments() {
      return totalAmountPayments;
    }
    public void setTotalAmountPayments(BigDecimal totalAmountPayments) {
      this.totalAmountPayments = totalAmountPayments;
    }
    public LocalDateTime getLatestPaymentDate() {
      return latestPaymentDate;
    }
    public void setLatestPaymentDate(LocalDateTime latestPaymentDate) {
      this.latestPaymentDate = latestPaymentDate;
    }
    public BigDecimal getLateFeeTotal() {
      return lateFeeTotal;
    }
    public void setLateFeeTotal(BigDecimal lateFeeTotal) {
      this.lateFeeTotal = lateFeeTotal;
    }
}
