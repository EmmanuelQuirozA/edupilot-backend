package com.monarchsolutions.sms.repository;

import com.monarchsolutions.sms.entity.Permission;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByRole_RoleIdAndModule_ModuleId(Long roleId, Long moduleId);
}
