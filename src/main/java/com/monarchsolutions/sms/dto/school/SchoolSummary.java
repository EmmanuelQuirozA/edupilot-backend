package com.monarchsolutions.sms.dto.school;

public class SchoolSummary {
    private Long school_id;
    private Long related_school_id;
    private String description;
    private String commercial_name;
    private Boolean enabled;
    private String school_status;
    private String image;
    private String plan_name;
    private boolean is_parent_school;

    public Long getSchool_id() {
        return school_id;
    }

    public void setSchool_id(Long school_id) {
        this.school_id = school_id;
    }

    public Long getRelated_school_id() {
        return related_school_id;
    }

    public void setRelated_school_id(Long related_school_id) {
        this.related_school_id = related_school_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCommercial_name() {
        return commercial_name;
    }

    public void setCommercial_name(String commercial_name) {
        this.commercial_name = commercial_name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getSchool_status() {
        return school_status;
    }

    public void setSchool_status(String school_status) {
        this.school_status = school_status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPlan_name() {
        return plan_name;
    }

    public void setPlan_name(String plan_name) {
        this.plan_name = plan_name;
    }

    public boolean isIs_parent_school() {
        return is_parent_school;
    }

    public void setIs_parent_school(boolean is_parent_school) {
        this.is_parent_school = is_parent_school;
    }
}
