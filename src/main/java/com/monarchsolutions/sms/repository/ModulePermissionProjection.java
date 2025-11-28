package com.monarchsolutions.sms.repository;

public interface ModulePermissionProjection {
    Long getModuleId();
    String getModuleName();
    String getModuleKey();
    Long getModuleAccessControlId();
    Long getSchoolId();
    Boolean getEnabled();

    Long getRoleId();
    String getRoleName();
    String getRoleNameDisplay();

    Boolean getCreateAllowed();
    Boolean getReadAllowed();
    Boolean getUpdateAllowed();
    Boolean getDeleteAllowed();
}
