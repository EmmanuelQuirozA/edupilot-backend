package com.monarchsolutions.sms.dto.reports;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentsResponse {
    private Long payment_id;
    private Long student_id;
    private Long school_id;
    private String payment_month;
    private BigDecimal amount;
    private Long payment_status_id;
    private LocalDateTime validated_at;
    private LocalDateTime pay_created_at;
    private LocalDateTime updated_at;
    private String comments;
    private String pt_name;
    private String payment_reference;
    private String generation;
    private String email;
    private String personal_email;
    private String student_full_name;
    private String address;
    private String phone_number;
    private String school_description;
    private String scholar_level_name;
    private Boolean g_enabled;
    private Boolean u_enabled;
    private Boolean sc_enabled;
    private String school_status;
    private String user_status;
    private String group_status;
    private String payment_status_name; 
    private String grade_group;
    private String validator_full_name;
    private String validator_phone_number;
    private String validator_username;
    private Long payment_request_id;

    public Long getPayment_id() {
        return payment_id;
    }
    public void setPayment_id(Long payment_id) {
        this.payment_id = payment_id;
    }
    public Long getStudent_id() {
        return student_id;
    }
    public void setStudent_id(Long student_id) {
        this.student_id = student_id;
    }
    public Long getSchool_id() {
        return school_id;
    }
    public void setSchool_id(Long school_id) {
        this.school_id = school_id;
    }
    public String getPayment_month() {
        return payment_month;
    }
    public void setPayment_month(String payment_month) {
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
    public LocalDateTime getValidated_at() {
        return validated_at;
    }
    public void setValidated_at(LocalDateTime validated_at) {
        this.validated_at = validated_at;
    }
    public LocalDateTime getPay_created_at() {
        return pay_created_at;
    }
    public void setPay_created_at(LocalDateTime pay_created_at) {
        this.pay_created_at = pay_created_at;
    }
    public LocalDateTime getUpdated_at() {
        return updated_at;
    }
    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    public String getPt_name() {
        return pt_name;
    }
    public void setPt_name(String pt_name) {
        this.pt_name = pt_name;
    }
    public String getPayment_reference() {
        return payment_reference;
    }
    public void setPayment_reference(String payment_reference) {
        this.payment_reference = payment_reference;
    }
    public String getGeneration() {
        return generation;
    }
    public void setGeneration(String generation) {
        this.generation = generation;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPersonal_email() {
        return personal_email;
    }
    public void setPersonal_email(String personal_email) {
        this.personal_email = personal_email;
    }
    public String getStudent_full_name() {
        return student_full_name;
    }
    public void setStudent_full_name(String student_full_name) {
        this.student_full_name = student_full_name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getPhone_number() {
        return phone_number;
    }
    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
    public String getSchool_description() {
        return school_description;
    }
    public void setSchool_description(String school_description) {
        this.school_description = school_description;
    }
    public String getScholar_level_name() {
        return scholar_level_name;
    }
    public void setScholar_level_name(String scholar_level_name) {
        this.scholar_level_name = scholar_level_name;
    }
    public Boolean getG_enabled() {
        return g_enabled;
    }
    public void setG_enabled(Boolean g_enabled) {
        this.g_enabled = g_enabled;
    }
    public Boolean getU_enabled() {
        return u_enabled;
    }
    public void setU_enabled(Boolean u_enabled) {
        this.u_enabled = u_enabled;
    }
    public Boolean getSc_enabled() {
        return sc_enabled;
    }
    public void setSc_enabled(Boolean sc_enabled) {
        this.sc_enabled = sc_enabled;
    }
    public String getSchool_status() {
        return school_status;
    }
    public void setSchool_status(String school_status) {
        this.school_status = school_status;
    }
    public String getUser_status() {
        return user_status;
    }
    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }
    public String getGroup_status() {
        return group_status;
    }
    public void setGroup_status(String group_status) {
        this.group_status = group_status;
    }
    public String getPayment_status_name() {
        return payment_status_name;
    }
    public void setPayment_status_name(String payment_status_name) {
        this.payment_status_name = payment_status_name;
    }
    public String getGrade_group() {
        return grade_group;
    }
    public void setGrade_group(String grade_group) {
        this.grade_group = grade_group;
    }
    public String getValidator_full_name() {
        return validator_full_name;
    }
    public void setValidator_full_name(String validator_full_name) {
        this.validator_full_name = validator_full_name;
    }
    public String getValidator_phone_number() {
        return validator_phone_number;
    }
    public void setValidator_phone_number(String validator_phone_number) {
        this.validator_phone_number = validator_phone_number;
    }
    public String getValidator_username() {
        return validator_username;
    }
    public void setValidator_username(String validator_username) {
        this.validator_username = validator_username;
    }
    public Long getPayment_request_id() {
        return payment_request_id;
    }
    public void setPayment_request_id(Long payment_request_id) {
        this.payment_request_id = payment_request_id;
    }
    
}
