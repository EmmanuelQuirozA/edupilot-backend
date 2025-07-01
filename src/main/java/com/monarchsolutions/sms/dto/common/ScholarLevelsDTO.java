package com.monarchsolutions.sms.dto.common;

public class ScholarLevelsDTO {
    private Long scholarLevelId;
    private String name; // will contain nameEn or nameEs based on lang

    // Getters and setters
    public Long getScholarLevelId() {
        return scholarLevelId;
    }
    public void setScholarLevelId(Long scholarLevelId) {
        this.scholarLevelId = scholarLevelId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
