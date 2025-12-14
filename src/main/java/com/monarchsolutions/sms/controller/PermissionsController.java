package com.monarchsolutions.sms.controller;

import com.monarchsolutions.sms.entity.Role;
import com.monarchsolutions.sms.service.PermissionService;
import com.monarchsolutions.sms.service.RoleService;
import com.monarchsolutions.sms.util.JwtUtil;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping("/module-access")
    public ResponseEntity<List<Map<String, Object>>> getModuleAccessList(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(value = "roleId", required = false) Long roleId,
            @RequestParam(value = "schoolId", required = false) Long schoolId,
            @RequestParam(value = "moduleKey", required = false) String moduleKey,
            @RequestParam(defaultValue = "es") String lang
    ) throws Exception {
        String token = authHeader.replaceFirst("^Bearer\\s+", "");
        Long tokenUserId = jwtUtil.extractUserId(token);
        Long userRoleId = jwtUtil.extractRoleId(token);

        Long toUseRoleId = null;

        if (roleId != null) {
                toUseRoleId=roleId;        
        } else {
                toUseRoleId=userRoleId;
        }

        List<Map<String, Object>> permissions = permissionService.getModuleAccessList(
                tokenUserId,
                toUseRoleId,
                schoolId,
                moduleKey,
                lang
        );

        return ResponseEntity.ok(permissions);
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPermission(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> payload,
            @RequestParam(defaultValue = "es") String lang
    ) throws Exception {
        String token = authHeader.replaceFirst("^Bearer\\s+", "");
        Long tokenUserId = jwtUtil.extractUserId(token);

        Map<String, Object> response = permissionService.createPermission(tokenUserId, payload, lang);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updatePermission(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("permissionId") Long permissionId,
            @RequestBody Map<String, Object> payload,
            @RequestParam(defaultValue = "es") String lang
    ) throws Exception {
        String token = authHeader.replaceFirst("^Bearer\\s+", "");
        Long tokenUserId = jwtUtil.extractUserId(token);

        Map<String, Object> response = permissionService.updatePermission(tokenUserId, permissionId, payload, lang);
        return ResponseEntity.ok(response);
    }
}
