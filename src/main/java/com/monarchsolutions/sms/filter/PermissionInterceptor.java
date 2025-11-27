package com.monarchsolutions.sms.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.annotation.RequirePermission;
import com.monarchsolutions.sms.service.PermissionService;
import com.monarchsolutions.sms.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class PermissionInterceptor implements HandlerInterceptor {

    private final PermissionService permissionService;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public PermissionInterceptor(PermissionService permissionService, JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.permissionService = permissionService;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequirePermission requirePermission = findAnnotation(handlerMethod);
        if (requirePermission == null) {
            return true;
        }

        String token = resolveToken(request);
        if (!StringUtils.hasText(token)) {
            writeError(response, HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
            return false;
        }

        if (!jwtUtil.validateToken(token)) {
            writeError(response, HttpStatus.UNAUTHORIZED, "Invalid or expired token");
            return false;
        }

        Long roleId = jwtUtil.extractRoleId(token);
        if (roleId == null) {
            writeError(response, HttpStatus.UNAUTHORIZED, "Token does not contain role_id");
            return false;
        }

        boolean allowed = permissionService.hasPermission(roleId, requirePermission.module(), requirePermission.action());
        if (!allowed) {
            String message = String.format("Role %d lacks '%s' permission on module '%s'", roleId, requirePermission.action(), requirePermission.module());
            writeError(response, HttpStatus.FORBIDDEN, message);
            return false;
        }

        return true;
    }

    private RequirePermission findAnnotation(HandlerMethod handlerMethod) {
        RequirePermission methodAnn = handlerMethod.getMethodAnnotation(RequirePermission.class);
        if (methodAnn != null) {
            return methodAnn;
        }
        return handlerMethod.getBeanType().getAnnotation(RequirePermission.class);
    }

    private String resolveToken(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    private void writeError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
