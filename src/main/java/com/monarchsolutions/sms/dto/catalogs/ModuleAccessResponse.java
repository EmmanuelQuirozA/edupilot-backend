package com.monarchsolutions.sms.dto.catalogs;

public class ModuleAccessResponse {
    private Long moduleId;
    private String moduleName;
    private String moduleKey;
    private Long moduleAccessControlId;
    private Long schoolId;
    private Boolean enabled;

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleKey() {
        return moduleKey;
    }

    public void setModuleKey(String moduleKey) {
        this.moduleKey = moduleKey;
    }

    public Long getModuleAccessControlId() {
        return moduleAccessControlId;
    }

    public void setModuleAccessControlId(Long moduleAccessControlId) {
        this.moduleAccessControlId = moduleAccessControlId;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
