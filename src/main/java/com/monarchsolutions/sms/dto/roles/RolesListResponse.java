package com.monarchsolutions.sms.dto.roles;

public class RolesListResponse {
    private Long role_id;
    private String role_name;
    private String role_description;
    private String role_status;
    private Boolean enabled;
    private Long school_id;
    private Boolean is_super_admin;
    private String name_es;
    private String name_en;
    private String description_es;
    private String description_en;
    
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
    public Long getSchool_id() {
        return school_id;
    }
    public void setSchool_id(Long school_id) {
        this.school_id = school_id;
    }
    public Boolean getIs_super_admin() {
        return is_super_admin;
    }
    public void setIs_super_admin(Boolean is_super_admin) {
        this.is_super_admin = is_super_admin;
    }
    public String getName_es() {
        return name_es;
    }
    public void setName_es(String name_es) {
        this.name_es = name_es;
    }
    public String getName_en() {
        return name_en;
    }
    public void setName_en(String name_en) {
        this.name_en = name_en;
    }
    public String getDescription_es() {
        return description_es;
    }
    public void setDescription_es(String description_es) {
        this.description_es = description_es;
    }
    public String getDescription_en() {
        return description_en;
    }
    public void setDescription_en(String description_en) {
        this.description_en = description_en;
    }
}
