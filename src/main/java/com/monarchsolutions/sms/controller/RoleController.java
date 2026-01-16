package com.monarchsolutions.sms.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.monarchsolutions.sms.util.JwtUtil;
import com.monarchsolutions.sms.annotation.RequirePermission;
import com.monarchsolutions.sms.dto.roles.RolesListResponse;
import com.monarchsolutions.sms.service.RoleService;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    
    @Autowired
    private RoleService roleService;

    @Autowired
    private JwtUtil jwtUtil;

    // Endpoint for retrieving the list of roles.
    @GetMapping("")
    public ResponseEntity<?> getRoles(@RequestParam(defaultValue = "es") String lang,
                                      @RequestParam(defaultValue = "-1") int status_filter,
                                      @RequestParam(required = false) Long school_id,
                                      @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
			Long   token_user_id = jwtUtil.extractUserId(token);
            List<RolesListResponse> roles = roleService.getRoles(token_user_id, school_id, lang, status_filter);

            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RequirePermission(module = "roles", action = "c")
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createRole(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> payload,
            @RequestParam(defaultValue = "es") String lang) throws Exception {
        String token = authHeader.replaceFirst("^Bearer\\s+", "");
        Long tokenUserId = jwtUtil.extractUserId(token);
        Map<String, Object> response = roleService.createRole(tokenUserId, payload, lang);
        return ResponseEntity.ok(response);
    }

    @RequirePermission(module = "roles", action = "u")
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateRole(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("role_id") Long roleId,
            @RequestBody Map<String, Object> payload,
            @RequestParam(defaultValue = "es") String lang) throws Exception {
        String token = authHeader.replaceFirst("^Bearer\\s+", "");
        Long tokenUserId = jwtUtil.extractUserId(token);
        Map<String, Object> response = roleService.updateRole(tokenUserId, roleId, payload, lang);
        return ResponseEntity.ok(response);
    }

}
