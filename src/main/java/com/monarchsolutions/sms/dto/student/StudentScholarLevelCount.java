package com.monarchsolutions.sms.dto.student;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StudentScholarLevelCount {

    @JsonProperty("scholar_level_id")
    private Long scholarLevelId;

    @JsonProperty("scholar_level_name")
    private String scholarLevelName;

    @JsonProperty("student_count")
    private Long studentCount;

    public Long getScholarLevelId() {
        return scholarLevelId;
    }

    public void setScholarLevelId(Long scholarLevelId) {
        this.scholarLevelId = scholarLevelId;
    }

    public String getScholarLevelName() {
        return scholarLevelName;
    }

    public void setScholarLevelName(String scholarLevelName) {
        this.scholarLevelName = scholarLevelName;
    }

    public Long getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Long studentCount) {
        this.studentCount = studentCount;
    }
}
