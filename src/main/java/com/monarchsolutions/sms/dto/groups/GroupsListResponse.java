package com.monarchsolutions.sms.dto.groups;

public class GroupsListResponse {
    private Long group_id;
    private Long school_id;
    private Long scholar_level_id;
    private String name;
    private String generation;
    private String grade_group;
    private String grade;
    private String group;
    private String scholar_level_name; 
    private Boolean enabled;
    private String group_status;
    private String school_description;
    
    public Long getGroup_id() {
        return group_id;
    }
    public void setGroup_id(Long group_id) {
        this.group_id = group_id;
    }
    public Long getSchool_id() {
        return school_id;
    }
    public void setSchool_id(Long school_id) {
        this.school_id = school_id;
    }
    public Long getScholar_level_id() {
        return scholar_level_id;
    }
    public void setScholar_level_id(Long scholar_level_id) {
        this.scholar_level_id = scholar_level_id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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
    public String getScholar_level_name() {
        return scholar_level_name;
    }
    public void setScholar_level_name(String scholar_level_name) {
        this.scholar_level_name = scholar_level_name;
    }
    public Boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    public String getGroup_status() {
        return group_status;
    }
    public void setGroup_status(String group_status) {
        this.group_status = group_status;
    }
    public String getSchool_description() {
        return school_description;
    }
    public void setSchool_description(String school_description) {
        this.school_description = school_description;
    }
    
}
