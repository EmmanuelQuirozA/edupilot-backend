package com.monarchsolutions.sms.dto.school;

import java.util.List;

public class GetSchoolsResponse {
    private List<SchoolSummary> schools;
    private Integer total_count;

    public List<SchoolSummary> getSchools() {
        return schools;
    }

    public void setSchools(List<SchoolSummary> schools) {
        this.schools = schools;
    }

    public Integer getTotal_count() {
        return total_count;
    }

    public void setTotal_count(Integer total_count) {
        this.total_count = total_count;
    }
}
