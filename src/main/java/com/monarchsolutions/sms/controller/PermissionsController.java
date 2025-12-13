package com.monarchsolutions.sms.controller;

import com.monarchsolutions.sms.dto.permission.ModulePermissionResponse;
import com.monarchsolutions.sms.entity.Role;
import com.monarchsolutions.sms.service.PermissionService;
import com.monarchsolutions.sms.service.RoleService;
import com.monarchsolutions.sms.util.JwtUtil;
import com.monarchsolutions.sms.validation.AdminGroup;
import com.monarchsolutions.sms.validation.SchoolAdminGroup;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/permissions")
public class PermissionsController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getRolesForPermissions(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "onlyActive", defaultValue = "false") boolean onlyActive,
            @RequestParam(defaultValue = "es") String lang
        ) {
        String token = authHeader.substring(7);
        Long tokenUserId = jwtUtil.extractUserId(token);
        List<Role> roles = roleService.getRolesForUser(tokenUserId, search, onlyActive, lang);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/module")
    public ResponseEntity<List<ModulePermissionResponse>> getModulePermissions(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(name = "moduleKey") String moduleKey,
            @RequestParam(defaultValue = "es") String lang,
            @RequestParam(defaultValue = "true") boolean onlyActive
    ) {
        String token = authHeader.replaceFirst("^Bearer\\s+", "");
        Long tokenUserId = jwtUtil.extractUserId(token);
        Long roleId = jwtUtil.extractRoleId(token);
        Long tokenSchoolId = jwtUtil.extractSchoolId(token);
        String role = jwtUtil.extractUserRole(token);
        Boolean isAdmin = "ADMIN".equalsIgnoreCase(role) ? true : false;


        List<ModulePermissionResponse> modulePermissions = permissionService.getModulePermissionsForUser(
                tokenUserId,
                roleId,
                tokenSchoolId,
                moduleKey,
                lang,
                onlyActive,
                isAdmin
        );

        return ResponseEntity.ok(modulePermissions);
    }
}
