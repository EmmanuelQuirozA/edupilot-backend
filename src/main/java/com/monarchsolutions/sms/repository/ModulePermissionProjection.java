package com.monarchsolutions.sms.repository;

public interface ModulePermissionProjection {
    Long getModuleId();
    String getModuleName();
    String getModuleKey();
    Long getModuleAccessControlId();
    Long getSchoolId();
    Byte getEnabled();
    Long sortOrder();
    String getIcon();

    Long getRoleId();
    String getRoleName();
    String getRoleNameDisplay();

    Byte getCreateAllowed();
    Byte getReadAllowed();
    Byte getUpdateAllowed();
    Byte getDeleteAllowed();
}
