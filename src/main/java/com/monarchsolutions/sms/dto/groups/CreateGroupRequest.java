package com.monarchsolutions.sms.dto.groups;

public class CreateGroupRequest {
    private Long school_id;
    private Long scholar_level_id;
    private String generation;
    private Number grade;
    private String group;
    
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
    public String getGeneration() {
        return generation;
    }
    public void setGeneration(String generation) {
        this.generation = generation;
    }
    public Number getGrade() {
        return grade;
    }
    public void setGrade(Number grade) {
        this.grade = grade;
    }
    public String getGroup() {
        return group;
    }
    public void setGroup(String group) {
        this.group = group;
    }
}
