package com.monarchsolutions.sms.dto.student;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StudentCountsResponse {

    @JsonProperty("total_students")
    private Long totalStudents;

    @JsonProperty("by_scholar_level")
    private List<StudentScholarLevelCount> byScholarLevel;

    public Long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public List<StudentScholarLevelCount> getByScholarLevel() {
        return byScholarLevel;
    }

    public void setByScholarLevel(List<StudentScholarLevelCount> byScholarLevel) {
        this.byScholarLevel = byScholarLevel;
    }
}
