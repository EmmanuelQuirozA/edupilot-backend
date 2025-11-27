package com.monarchsolutions.sms.service;

import com.monarchsolutions.sms.entity.Module;
import com.monarchsolutions.sms.entity.Permission;
import com.monarchsolutions.sms.repository.ModuleRepository;
import com.monarchsolutions.sms.repository.PermissionRepository;
import java.util.Locale;
import java.util.Optional;
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
            case "c" -> permission.get().isCanCreate();
            case "r" -> permission.get().isCanRead();
            case "u" -> permission.get().isCanUpdate();
            case "d" -> permission.get().isCanDelete();
            default -> false;
        };
    }
}
