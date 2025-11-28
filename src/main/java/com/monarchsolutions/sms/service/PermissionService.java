package com.monarchsolutions.sms.service;

import com.monarchsolutions.sms.entity.Module;
import com.monarchsolutions.sms.entity.Permission;
import com.monarchsolutions.sms.dto.permission.ModulePermissionResponse;
import com.monarchsolutions.sms.repository.ModuleRepository;
import com.monarchsolutions.sms.repository.PermissionRepository;
import com.monarchsolutions.sms.repository.ModulePermissionProjection;
import java.util.Locale;
import java.util.Optional;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final ModuleRepository moduleRepository;

    public PermissionService(PermissionRepository permissionRepository, ModuleRepository moduleRepository) {
        this.permissionRepository = permissionRepository;
        this.moduleRepository = moduleRepository;
    }

    /**
     * Check whether the role has permission to perform an action on a module.
     *
     * @param roleId    role identifier coming from JWT
     * @param moduleKey logical module key (e.g., students, payments)
     * @param action    CRUD action (c, r, u, d)
     * @return true if permission exists and is enabled
     */
    @Transactional(readOnly = true)
    public boolean hasPermission(Long roleId, String moduleKey, String action) {
        if (roleId == null || moduleKey == null || action == null) {
            return false;
        }

        Optional<Module> module = moduleRepository.findByKey(moduleKey);
        if (module.isEmpty()) {
            return false;
        }

        Optional<Permission> permission = permissionRepository.findByRole_RoleIdAndModule_ModuleId(roleId, module.get().getModuleId());
        if (permission.isEmpty()) {
            return false;
        }

        String normalizedAction = action.toLowerCase(Locale.ROOT);
        return switch (normalizedAction) {
            case "c" -> permission.get().isCreateAllowed();
            case "r" -> permission.get().isReadAllowed();
            case "u" -> permission.get().isUpdateAllowed();
            case "d" -> permission.get().isDeleteAllowed();
            default -> false;
        };
    }

    @Transactional(readOnly = true)
    public List<ModulePermissionResponse> getModulePermissionsForUser(
            Long tokenUserId,
            Long roleId,
            String moduleKey,
            String lang,
            boolean onlyActive
    ) {
        if (tokenUserId == null || roleId == null || moduleKey == null || moduleKey.isBlank()) {
            return List.of();
        }

        List<ModulePermissionProjection> projections = permissionRepository.findModulePermissionsForRole(
                tokenUserId,
                roleId,
                moduleKey,
                lang,
                onlyActive
        );

        return projections.stream()
                .map(projection -> {
                    ModulePermissionResponse response = new ModulePermissionResponse();
                    response.setModuleId(projection.getModuleId());
                    response.setModuleName(projection.getModuleName());
                    response.setModuleKey(projection.getModuleKey());
                    response.setModuleAccessControlId(projection.getModuleAccessControlId());
                    response.setSchoolId(projection.getSchoolId());
                    response.setEnabled(projection.getEnabled());
                    response.setRoleId(projection.getRoleId());
                    response.setRoleName(projection.getRoleName());
                    response.setRoleNameDisplay(projection.getRoleNameDisplay());
                    response.setCreateAllowed(projection.getCreateAllowed());
                    response.setReadAllowed(projection.getReadAllowed());
                    response.setUpdateAllowed(projection.getUpdateAllowed());
                    response.setDeleteAllowed(projection.getDeleteAllowed());
                    return response;
                })
                .toList();
    }
}
