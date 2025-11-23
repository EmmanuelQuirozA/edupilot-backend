package com.monarchsolutions.sms.dto.user;

import java.time.LocalDate;

public class UserDetailsCache {
  private Long user_id;
  private Long school_id;
  private String email;
  private String username;
  private String role_name;
  private String full_name;
  private String first_name;
  private LocalDate birth_date;
  private String school_name;
  public Long getUser_id() {
    return user_id;
  }
  public void setUser_id(Long user_id) {
    this.user_id = user_id;
  }
  public Long getSchool_id() {
    return school_id;
  }
  public void setSchool_id(Long school_id) {
    this.school_id = school_id;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }
  public String getRole_name() {
    return role_name;
  }
  public void setRole_name(String role_name) {
    this.role_name = role_name;
  }
  public String getFull_name() {
    return full_name;
  }
  public void setFull_name(String full_name) {
    this.full_name = full_name;
  }
  
  public LocalDate getBirth_date() {
    return birth_date;
  }
  public void setBirth_date(LocalDate birth_date) {
    this.birth_date = birth_date;
  }
  public String getFirst_name() {
    return first_name;
  }
  public void setFirst_name(String first_name) {
    this.first_name = first_name;
  }
  public String getSchool_name() {
    return school_name;
  }
  public void setSchool_name(String school_name) {
    this.school_name = school_name;
  }
}
