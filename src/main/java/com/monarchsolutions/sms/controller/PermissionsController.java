package com.monarchsolutions.sms.controller;

import com.monarchsolutions.sms.annotation.RequirePermission;
import com.monarchsolutions.sms.entity.Role;
import com.monarchsolutions.sms.service.RoleService;
import com.monarchsolutions.sms.util.JwtUtil;
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
    private JwtUtil jwtUtil;

    @RequirePermission(module = "catalogs", action = "r")
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
}
