package com.monarchsolutions.sms.dto.student;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GetStudentDetails {
    private Long        studentId;
    private Long        userId;
    private String         registerId;
    private String         paymentReference;
    private String         email;
    private String         username;
    private String         fullName;
    private String         address;
    private String         commercialName;
    private String         groupName;
    private String         generation;
    private String         gradeGroup;
    private String         scholarLevelName;
    private LocalDate      birthDate;
    private String         phoneNumber;
    private String         taxId;
    private String         personalEmail;
    private Boolean        userEnabled;
    private Boolean        roleEnabled;
    private Boolean        schoolEnabled;
    private Boolean        groupEnabled;
    private String         userStatus;
    private String         roleStatus;
    private String         schoolStatus;
    private String         groupStatus;
    private LocalDate      joiningDate;
    private BigDecimal     tuition;
    private BigDecimal     defaultTuition;
    private BigDecimal     balance;

    public Long getStudentId() {
      return studentId;
    }
    public void setStudentId(Long studentId) {
      this.studentId = studentId;
    }
    public Long getUserId() {
      return userId;
    }
    public void setUserId(Long userId) {
      this.userId = userId;
    }
    public String getRegisterId() {
      return registerId;
    }
    public void setRegisterId(String registerId) {
      this.registerId = registerId;
    }
    public String getPaymentReference() {
      return paymentReference;
    }
    public void setPaymentReference(String paymentReference) {
      this.paymentReference = paymentReference;
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
    public String getFullName() {
      return fullName;
    }
    public void setFullName(String fullName) {
      this.fullName = fullName;
    }
    public String getAddress() {
      return address;
    }
    public void setAddress(String address) {
      this.address = address;
    }
    public String getCommercialName() {
      return commercialName;
    }
    public void setCommercialName(String commercialName) {
      this.commercialName = commercialName;
    }
    public String getGroupName() {
      return groupName;
    }
    public void setGroupName(String groupName) {
      this.groupName = groupName;
    }
    public String getGeneration() {
      return generation;
    }
    public void setGeneration(String generation) {
      this.generation = generation;
    }
    public String getGradeGroup() {
      return gradeGroup;
    }
    public void setGradeGroup(String gradeGroup) {
      this.gradeGroup = gradeGroup;
    }
    public String getScholarLevelName() {
      return scholarLevelName;
    }
    public void setScholarLevelName(String scholarLevelName) {
      this.scholarLevelName = scholarLevelName;
    }
    public LocalDate getBirthDate() {
      return birthDate;
    }
    public void setBirthDate(LocalDate birthDate) {
      this.birthDate = birthDate;
    }
    public String getPhoneNumber() {
      return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
    }
    public String getTaxId() {
      return taxId;
    }
    public void setTaxId(String taxId) {
      this.taxId = taxId;
    }
    public String getPersonalEmail() {
      return personalEmail;
    }
    public void setPersonalEmail(String personalEmail) {
      this.personalEmail = personalEmail;
    }
    public Boolean getUserEnabled() {
      return userEnabled;
    }
    public void setUserEnabled(Boolean userEnabled) {
      this.userEnabled = userEnabled;
    }
    public Boolean getRoleEnabled() {
      return roleEnabled;
    }
    public void setRoleEnabled(Boolean roleEnabled) {
      this.roleEnabled = roleEnabled;
    }
    public Boolean getSchoolEnabled() {
      return schoolEnabled;
    }
    public void setSchoolEnabled(Boolean schoolEnabled) {
      this.schoolEnabled = schoolEnabled;
    }
    public Boolean getGroupEnabled() {
      return groupEnabled;
    }
    public void setGroupEnabled(Boolean groupEnabled) {
      this.groupEnabled = groupEnabled;
    }
    public String getUserStatus() {
      return userStatus;
    }
    public void setUserStatus(String userStatus) {
      this.userStatus = userStatus;
    }
    public String getRoleStatus() {
      return roleStatus;
    }
    public void setRoleStatus(String roleStatus) {
      this.roleStatus = roleStatus;
    }
    public String getSchoolStatus() {
      return schoolStatus;
    }
    public void setSchoolStatus(String schoolStatus) {
      this.schoolStatus = schoolStatus;
    }
    public String getGroupStatus() {
      return groupStatus;
    }
    public void setGroupStatus(String groupStatus) {
      this.groupStatus = groupStatus;
    }
    public LocalDate getJoiningDate() {
      return joiningDate;
    }
    public void setJoiningDate(LocalDate joiningDate) {
      this.joiningDate = joiningDate;
    }
    public BigDecimal getTuition() {
      return tuition;
    }
    public void setTuition(BigDecimal tuition) {
      this.tuition = tuition;
    }
    public BigDecimal getDefaultTuition() {
      return defaultTuition;
    }
    public void setDefaultTuition(BigDecimal defaultTuition) {
      this.defaultTuition = defaultTuition;
    }
    public BigDecimal getBalance() {
      return balance;
    }
    public void setBalance(BigDecimal balance) {
      this.balance = balance;
    }
}
