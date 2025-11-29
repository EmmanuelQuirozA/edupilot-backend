package com.monarchsolutions.sms.repository;

public interface ModuleAccessProjection {
    Long getModuleId();
    String getModuleName();
    String getModuleKey();
    Long getModuleAccessControlId();
    Long getSchoolId();
    Boolean getEnabled();
    Long getSortOrder();
}
