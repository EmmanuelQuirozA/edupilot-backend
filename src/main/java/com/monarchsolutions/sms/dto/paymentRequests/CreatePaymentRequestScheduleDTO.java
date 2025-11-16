package com.monarchsolutions.sms.dto.paymentRequests;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreatePaymentRequestScheduleDTO {
    private Integer    payment_concept_id;
    private Integer    period_of_time_id;
    private String     rule_name_es;
    private String     rule_name_en;
    private BigDecimal amount;
    private String     fee_type;
    private BigDecimal late_fee;
    private String     late_fee_frequency;
    private String     interval_count;
    private String     comments;
    private LocalDate  start_date;
    private LocalDate  end_date;
    private LocalDate  next_execution_date;
    private LocalDate  payment_window;

    public LocalDate getPayment_window() {
        return payment_window;
    }

    public void setPayment_window(LocalDate payment_window) {
        this.payment_window = payment_window;
    }

    public Integer getPayment_concept_id() {
        return payment_concept_id;
    }

    public void setPayment_concept_id(Integer payment_concept_id) {
        this.payment_concept_id = payment_concept_id;
    }

    public Integer getPeriod_of_time_id() {
        return period_of_time_id;
    }

    public void setPeriod_of_time_id(Integer period_of_time_id) {
        this.period_of_time_id = period_of_time_id;
    }

    public String getRule_name_es() {
        return rule_name_es;
    }

    public void setRule_name_es(String rule_name_es) {
        this.rule_name_es = rule_name_es;
    }

    public String getRule_name_en() {
        return rule_name_en;
    }

    public void setRule_name_en(String rule_name_en) {
        this.rule_name_en = rule_name_en;
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

    public String getLate_fee_frequency() {
        return late_fee_frequency;
    }

    public void setLate_fee_frequency(String late_fee_frequency) {
        this.late_fee_frequency = late_fee_frequency;
    }

    public String getInterval_count() {
        return interval_count;
    }

    public void setInterval_count(String interval_count) {
        this.interval_count = interval_count;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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

    public LocalDate getNext_execution_date() {
        return next_execution_date;
    }

    public void setNext_execution_date(LocalDate next_execution_date) {
        this.next_execution_date = next_execution_date;
    }
}
