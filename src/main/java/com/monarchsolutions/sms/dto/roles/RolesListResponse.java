package com.monarchsolutions.sms.dto.roles;

public class RolesListResponse {
    private Long role_id;
    private String role_name;
    private String role_description;
    private String role_status;
    private Boolean enabled;
    private Integer role_level;
    
    public Long getRole_id() {
        return role_id;
    }
    public void setRole_id(Long role_id) {
        this.role_id = role_id;
    }
    public String getRole_name() {
        return role_name;
    }
    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }
    public String getRole_description() {
        return role_description;
    }
    public void setRole_description(String role_description) {
        this.role_description = role_description;
    }
    public String getRole_status() {
        return role_status;
    }
    public void setRole_status(String role_status) {
        this.role_status = role_status;
    }
    public Boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    public Integer getRole_level() {
        return role_level;
    }
    public void setRole_level(Integer role_level) {
        this.role_level = role_level;
    }
}
