package com.monarchsolutions.sms.dto.common;

public class UserLoginDTO {
    private Long userId;
    private Long schoolId;
    private Long roleId;
    private String email;
    private String username;
    private String password;
    private String roleName;
    private String fullName;
    private String address;
    private String commercialName;
    private String businessName;
    private String personalEmail;
    private Boolean enabledUser;
    private Boolean enabledRole;
    private Boolean enabledSchool;
    private String usernameOrEmail;

    // Getters and setters for each field
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getSchoolId() {
        return schoolId;
    }
    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }
    public Long getRoleId() {
        return roleId;
    }
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
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
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRoleName() {
        return roleName;
    }
    public void setRoleName(String roleName) {
        this.roleName = roleName;
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
    public String getBusinessName() {
        return businessName;
    }
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
    public String getPersonalEmail() {
        return personalEmail;
    }
    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }
    public Boolean getEnabledUser() {
        return enabledUser;
    }
    public void setEnabledUser(Boolean enabledUser) {
        this.enabledUser = enabledUser;
    }
    public Boolean getEnabledRole() {
        return enabledRole;
    }
    public void setEnabledRole(Boolean enabledRole) {
        this.enabledRole = enabledRole;
    }
    public Boolean getEnabledSchool() {
        return enabledSchool;
    }
    public void setEnabledSchool(Boolean enabledSchool) {
        this.enabledSchool = enabledSchool;
    }
    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }
    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }
}
