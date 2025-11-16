package com.monarchsolutions.sms.dto.paymentRequests;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentRequestScheduleRule {

    private Long paymentRequestScheduledId;
    private Long schoolId;
    private Long groupId;
    private Long studentId;
    private Long paymentConceptId;
    private BigDecimal amount;
    private String feeType;
    private BigDecimal lateFee;
    private Integer lateFeeFrequency;
    private String comments;
    private String paymentMonth;
    private Boolean partialPayment;
    private LocalDate nextExecutionDate;
    private Integer paymentWindow;
    private Integer periodOfTimeId;
    private Integer intervalCount;
    private LocalDate endDate;
    private Long createdBy;

    public Long getPaymentRequestScheduledId() {
        return paymentRequestScheduledId;
    }

    public void setPaymentRequestScheduledId(Long paymentRequestScheduledId) {
        this.paymentRequestScheduledId = paymentRequestScheduledId;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getPaymentConceptId() {
        return paymentConceptId;
    }

    public void setPaymentConceptId(Long paymentConceptId) {
        this.paymentConceptId = paymentConceptId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public BigDecimal getLateFee() {
        return lateFee;
    }

    public void setLateFee(BigDecimal lateFee) {
        this.lateFee = lateFee;
    }

    public Integer getLateFeeFrequency() {
        return lateFeeFrequency;
    }

    public void setLateFeeFrequency(Integer lateFeeFrequency) {
        this.lateFeeFrequency = lateFeeFrequency;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getPaymentMonth() {
        return paymentMonth;
    }

    public void setPaymentMonth(String paymentMonth) {
        this.paymentMonth = paymentMonth;
    }

    public Boolean getPartialPayment() {
        return partialPayment;
    }

    public void setPartialPayment(Boolean partialPayment) {
        this.partialPayment = partialPayment;
    }

    public LocalDate getNextExecutionDate() {
        return nextExecutionDate;
    }

    public void setNextExecutionDate(LocalDate nextExecutionDate) {
        this.nextExecutionDate = nextExecutionDate;
    }

    public Integer getPaymentWindow() {
        return paymentWindow;
    }

    public void setPaymentWindow(Integer paymentWindow) {
        this.paymentWindow = paymentWindow;
    }

    public Integer getPeriodOfTimeId() {
        return periodOfTimeId;
    }

    public void setPeriodOfTimeId(Integer periodOfTimeId) {
        this.periodOfTimeId = periodOfTimeId;
    }

    public Integer getIntervalCount() {
        return intervalCount;
    }

    public void setIntervalCount(Integer intervalCount) {
        this.intervalCount = intervalCount;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
}
