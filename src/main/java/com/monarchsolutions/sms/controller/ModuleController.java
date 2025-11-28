package com.monarchsolutions.sms.controller;

import com.monarchsolutions.sms.dto.catalogs.ModuleAccessResponse;
import com.monarchsolutions.sms.service.ModuleService;
import com.monarchsolutions.sms.util.JwtUtil;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/modules")
public class ModuleController {

    private final ModuleService moduleService;
    private final JwtUtil jwtUtil;

    public ModuleController(ModuleService moduleService, JwtUtil jwtUtil) {
        this.moduleService = moduleService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/access-control")
    public ResponseEntity<List<ModuleAccessResponse>> getAccessibleModules(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "es") String lang,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "true") boolean onlyActive,
            @RequestParam(required = false) String moduleKey
    ) {
        String token = authHeader.replaceFirst("^Bearer\\s+", "");
        Long tokenUserId = jwtUtil.extractUserId(token);

        List<ModuleAccessResponse> modules = moduleService.getAccessibleModules(
                tokenUserId,
                search,
                onlyActive,
                lang,
                moduleKey
        );

        return ResponseEntity.ok(modules);
    }
}
