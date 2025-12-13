package com.monarchsolutions.sms.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    @GetMapping("")
    public ResponseEntity<?> getRoles(@RequestParam(defaultValue = "es") String lang,
                                      @RequestParam(defaultValue = "-1") int status_filter,
                                      @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
			Long   token_user_id = jwtUtil.extractUserId(token);
            List<RolesListResponse> roles = roleService.getRoles(token_user_id, lang, status_filter);

            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
