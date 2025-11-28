package com.monarchsolutions.sms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "role_name", nullable = false, length = 255)
    private String roleName;

    @Column(name = "role_name_display", nullable = false, length = 255)
    private String roleNameDisplay;

    @Column(name = "role_description_display", nullable = false, length = 255)
    private String roleDescriptionDisplay;

    @Column(name = "enabled")
    private Boolean enabled;

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

    public String getRoleDescriptionDisplay() {
        return roleDescriptionDisplay;
    }

    public void setRoleDescriptionDisplay(String roleDescriptionDisplay) {
        this.roleDescriptionDisplay = roleDescriptionDisplay;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
