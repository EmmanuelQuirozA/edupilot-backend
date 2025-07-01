package com.monarchsolutions.sms.dto.userLogs;

public class UserLogsListDto {
    private Long activity_log_users_id;
    private String responsible_fullname;
    private String target_fullname;
    private String school_commercial_name;
    private String username;
    private String full_name;
    private String created_at;
    private String role_name;
    private String category; 
    private String log_description; 
    private String log_action;
    
    public Long getActivity_log_users_id() {
        return activity_log_users_id;
    }
    public void setActivity_log_users_id(Long activity_log_users_id) {
        this.activity_log_users_id = activity_log_users_id;
    }
    public String getResponsible_fullname() {
        return responsible_fullname;
    }
    public void setResponsible_fullname(String responsible_fullname) {
        this.responsible_fullname = responsible_fullname;
    }
    public String getTarget_fullname() {
        return target_fullname;
    }
    public void setTarget_fullname(String target_fullname) {
        this.target_fullname = target_fullname;
    }
    public String getSchool_commercial_name() {
        return school_commercial_name;
    }
    public void setSchool_commercial_name(String school_commercial_name) {
        this.school_commercial_name = school_commercial_name;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getFull_name() {
        return full_name;
    }
    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }
    public String getCreated_at() {
        return created_at;
    }
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
    public String getRole_name() {
        return role_name;
    }
    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getLog_description() {
        return log_description;
    }
    public void setLog_description(String log_description) {
        this.log_description = log_description;
    }
    public String getLog_action() {
        return log_action;
    }
    public void setLog_action(String log_action) {
        this.log_action = log_action;
    }
    
}
