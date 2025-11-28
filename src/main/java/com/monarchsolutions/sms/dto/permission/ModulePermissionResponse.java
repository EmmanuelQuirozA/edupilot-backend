package com.monarchsolutions.sms.dto.permission;

public class ModulePermissionResponse {

    private Long moduleId;
    private String moduleName;
    private String moduleKey;
    private Long moduleAccessControlId;
    private Long schoolId;
    private Boolean enabled;

    private Long roleId;
    private String roleName;
    private String roleNameDisplay;

    private Boolean createAllowed;
    private Boolean readAllowed;
    private Boolean updateAllowed;
    private Boolean deleteAllowed;

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

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleNameDisplay() {
        return roleNameDisplay;
    }

    public void setRoleNameDisplay(String roleNameDisplay) {
        this.roleNameDisplay = roleNameDisplay;
    }

    public Boolean getCreateAllowed() {
        return createAllowed;
    }

    public void setCreateAllowed(Boolean createAllowed) {
        this.createAllowed = createAllowed;
    }

    public Boolean getReadAllowed() {
        return readAllowed;
    }

    public void setReadAllowed(Boolean readAllowed) {
        this.readAllowed = readAllowed;
    }

    public Boolean getUpdateAllowed() {
        return updateAllowed;
    }

    public void setUpdateAllowed(Boolean updateAllowed) {
        this.updateAllowed = updateAllowed;
    }

    public Boolean getDeleteAllowed() {
        return deleteAllowed;
    }

    public void setDeleteAllowed(Boolean deleteAllowed) {
        this.deleteAllowed = deleteAllowed;
    }
}
