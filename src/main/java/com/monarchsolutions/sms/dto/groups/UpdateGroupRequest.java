package com.monarchsolutions.sms.dto.groups;

public class UpdateGroupRequest {
    private Long group_id;
    private Long scholar_level_id;
    private String name;
    private String generation;
    private String group;
    private Number grade;
    
    public Long getGroup_id() {
        return group_id;
    }
    public void setGroup_id(Long group_id) {
        this.group_id = group_id;
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
    public String getGroup() {
        return group;
    }
    public void setGroup(String group) {
        this.group = group;
    }
    public Number getGrade() {
        return grade;
    }
    public void setGrade(Number grade) {
        this.grade = grade;
    }
}
