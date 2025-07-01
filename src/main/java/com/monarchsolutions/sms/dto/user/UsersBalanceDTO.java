package com.monarchsolutions.sms.dto.user;

import java.math.BigDecimal;

public class UsersBalanceDTO {
  private Long user_id;
  private String full_name;
  private String role_name;
  private String generation;
  private String scholar_level_name;
  private String grade_group;
  private BigDecimal balance;
  public Long getUser_id() {
    return user_id;
  }
  public void setUser_id(Long user_id) {
    this.user_id = user_id;
  }
  public String getFull_name() {
    return full_name;
  }
  public void setFull_name(String full_name) {
    this.full_name = full_name;
  }
  public String getRole_name() {
    return role_name;
  }
  public void setRole_name(String role_name) {
    this.role_name = role_name;
  }
  public String getGeneration() {
    return generation;
  }
  public void setGeneration(String generation) {
    this.generation = generation;
  }
  public String getScholar_level_name() {
    return scholar_level_name;
  }
  public void setScholar_level_name(String scholar_level_name) {
    this.scholar_level_name = scholar_level_name;
  }
  public String getGrade_group() {
    return grade_group;
  }
  public void setGrade_group(String grade_group) {
    this.grade_group = grade_group;
  }
  public BigDecimal getBalance() {
    return balance;
  }
  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }
}
