package com.monarchsolutions.sms.dto.paymentRequests;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreatePaymentRecurrenceDTO {
    private Integer    school_id;
    private Integer    group_id;
    private Integer    student_id;
    private Integer    payment_concept_id;
    private BigDecimal amount;
    private String     fee_type;
    private BigDecimal late_fee;
    private Integer    late_fee_frequency;
    private String     period;
    private Integer    interval_count;
    private LocalDate  start_date;
    private LocalDate  end_date;
    private String     comments;
    private LocalDate  payment_month;

    public Integer getSchool_id() {
        return school_id;
    }
    public void setSchool_id(Integer school_id) {
        this.school_id = school_id;
    }
    public Integer getGroup_id() {
        return group_id;
    }
    public void setGroup_id(Integer group_id) {
        this.group_id = group_id;
    }

    public Integer getStudent_id() {
        return student_id;
    }
    public void setStudent_id(Integer student_id) {
        this.student_id = student_id;
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
    public String getFee_type() {
        return fee_type;
    }
    public void setFee_type(String fee_type) {
        this.fee_type = fee_type;
    }
    public BigDecimal getLate_fee() {
        return late_fee;
    }
    public void setLate_fee(BigDecimal late_fee) {
        this.late_fee = late_fee;
    }
    public Integer getLate_fee_frequency() {
        return late_fee_frequency;
    }
    public void setLate_fee_frequency(Integer late_fee_frequency) {
        this.late_fee_frequency = late_fee_frequency;
    }
    public String getPeriod() {
        return period;
    }
    public void setPeriod(String period) {
        this.period = period;
    }
    public Integer getInterval_count() {
        return interval_count;
    }
    public void setInterval_count(Integer interval_count) {
        this.interval_count = interval_count;
    }
    public LocalDate getStart_date() {
        return start_date;
    }
    public void setStart_date(LocalDate start_date) {
        this.start_date = start_date;
    }
    public LocalDate getEnd_date() {
        return end_date;
    }
    public void setEnd_date(LocalDate end_date) {
        this.end_date = end_date;
    }
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    public LocalDate getPayment_month() {
        return payment_month;
    }
    public void setPayment_month(LocalDate payment_month) {
        this.payment_month = payment_month;
    }
}
