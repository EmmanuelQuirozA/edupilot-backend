package com.monarchsolutions.sms.dto.reports;

import java.math.BigDecimal;

public class BalanceRechargeResponse {
    private Long balance_recharge_id;
    private Long user_id;
    private Long responsable_user_id;
    private String date;
    private BigDecimal amount;
    private String user_full_name;
    private String responsable_full_name;
    private String school_description;
    private String role_name;
    private String group_name;
    private String generation;
    private String grade_group;
    private String grade;
    private String group;
    private String scholar_level_id;
    private String scholar_level_name;

    public Long getBalance_recharge_id() {
        return balance_recharge_id;
    }
    public void setBalance_recharge_id(Long balance_recharge_id) {
        this.balance_recharge_id = balance_recharge_id;
    }
    public Long getUser_id() {
        return user_id;
    }
    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }
    public Long getResponsable_user_id() {
        return responsable_user_id;
    }
    public void setResponsable_user_id(Long responsable_user_id) {
        this.responsable_user_id = responsable_user_id;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public String getUser_full_name() {
        return user_full_name;
    }
    public void setUser_full_name(String user_full_name) {
        this.user_full_name = user_full_name;
    }
    public String getResponsable_full_name() {
        return responsable_full_name;
    }
    public void setResponsable_full_name(String responsable_full_name) {
        this.responsable_full_name = responsable_full_name;
    }
    public String getSchool_description() {
        return school_description;
    }
    public void setSchool_description(String school_description) {
        this.school_description = school_description;
    }
    public String getRole_name() {
        return role_name;
    }
    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }
    public String getGroup_name() {
        return group_name;
    }
    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }
    public String getGeneration() {
        return generation;
    }
    public void setGeneration(String generation) {
        this.generation = generation;
    }
    public String getGrade_group() {
        return grade_group;
    }
    public void setGrade_group(String grade_group) {
        this.grade_group = grade_group;
    }
    public String getGrade() {
        return grade;
    }
    public void setGrade(String grade) {
        this.grade = grade;
    }
    public String getGroup() {
        return group;
    }
    public void setGroup(String group) {
        this.group = group;
    }
    public String getScholar_level_id() {
        return scholar_level_id;
    }
    public void setScholar_level_id(String scholar_level_id) {
        this.scholar_level_id = scholar_level_id;
    }
    public String getScholar_level_name() {
        return scholar_level_name;
    }
    public void setScholar_level_name(String scholar_level_name) {
        this.scholar_level_name = scholar_level_name;
    }
}
