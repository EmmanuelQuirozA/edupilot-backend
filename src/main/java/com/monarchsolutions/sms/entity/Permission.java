package com.monarchsolutions.sms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Long permissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(name = "c")
    private boolean createAllowed;

    @Column(name = "r")
    private boolean readAllowed;

    @Column(name = "u")
    private boolean updateAllowed;

    @Column(name = "d")
    private boolean deleteAllowed;

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public boolean isCreateAllowed() {
        return createAllowed;
    }

    public void setCreateAllowed(boolean createAllowed) {
        this.createAllowed = createAllowed;
    }

    public boolean isReadAllowed() {
        return readAllowed;
    }

    public void setReadAllowed(boolean readAllowed) {
        this.readAllowed = readAllowed;
    }

    public boolean isUpdateAllowed() {
        return updateAllowed;
    }

    public void setUpdateAllowed(boolean updateAllowed) {
        this.updateAllowed = updateAllowed;
    }

    public boolean isDeleteAllowed() {
        return deleteAllowed;
    }

    public void setDeleteAllowed(boolean deleteAllowed) {
        this.deleteAllowed = deleteAllowed;
    }
}
