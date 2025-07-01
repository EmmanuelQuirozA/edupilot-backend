package com.monarchsolutions.sms.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.monarchsolutions.sms.util.JwtUtil;

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
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @GetMapping("")
    public ResponseEntity<?> getRoles(@RequestParam(defaultValue = "en") String lang,
                                      @RequestParam(defaultValue = "1") int role_level,
                                      @RequestParam(defaultValue = "-1") int status_filter,
                                      @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String userRole = jwtUtil.extractUserRole(token);
            List<RolesListResponse> roles = roleService.getRoles(lang, role_level, status_filter);
            
            // If the authenticated user is SCHOOL_ADMIN, filter out users with role_id 1 or 4.
            if ("SCHOOL_ADMIN".equalsIgnoreCase(userRole)) {
                roles = roles.stream()
                            .filter(r -> r.getRole_id() != 1 && r.getRole_id() != 4 )
                            .collect(Collectors.toList());
            }

            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
