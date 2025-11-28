package com.monarchsolutions.sms.controller;

import com.monarchsolutions.sms.dto.groups.CreateGroupRequest;
import com.monarchsolutions.sms.dto.groups.GetClassesCatalog;
import com.monarchsolutions.sms.dto.groups.GroupsListResponse;
import com.monarchsolutions.sms.dto.groups.UpdateGroupRequest;
import com.monarchsolutions.sms.annotation.RequirePermission;
import com.monarchsolutions.sms.service.GroupService;
import com.monarchsolutions.sms.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private JwtUtil jwtUtil;

    // Endpoint for retrieving the list of groups.
    @RequirePermission(module = "classes", action = "r")
    @GetMapping("/list")
    public ResponseEntity<?> getGroupsList( @RequestHeader("Authorization") String authHeader,
                                            @RequestParam(required = false) Long school_id,
                                            @RequestParam(defaultValue = "es") String lang,
                                            @RequestParam(required = false) Integer status_filter) {
        try {
            String token = authHeader.substring(7);
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            List<GroupsListResponse> groups = groupService.getGroupsList(tokenSchoolId, school_id, lang, status_filter);
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
        // Endpoint to create a new group
    @RequirePermission(module = "classes", action = "c")
    @PostMapping("/create")
    public ResponseEntity<String> createGroup(@RequestHeader("Authorization") String authHeader,
                                             @RequestParam(defaultValue = "es") String lang,
                                             @RequestBody CreateGroupRequest request) {
        try {
            // Extract the token (remove "Bearer " prefix)
            String token = authHeader.substring(7);
            // Extract schoolId from the token (if available)
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            // Call the service method (which will hash the password and pass the JSON data to the SP)
            String jsonResponse = groupService.createGroup(tokenSchoolId, lang, request);
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint to update an existing group.
    @RequirePermission(module = "classes", action = "u")
    @PutMapping("/update/{group_id}")
    public ResponseEntity<String> updateGroup(@RequestHeader("Authorization") String authHeader,
                                             @PathVariable("group_id") Long group_id,
                                             @RequestParam(defaultValue = "es") String lang,
                                             @RequestBody UpdateGroupRequest request) {
        try {
            request.setGroup_id(group_id);
            // Extract the token (remove "Bearer " prefix)
            String token = authHeader.substring(7);
            // Extract schoolId from the token (if available)
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            // Call the service method (which will pass the JSON data to the SP)
            String jsonResponse = groupService.updateGroup(tokenSchoolId, group_id, lang, request);
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint to toggle or change the group's status.
    @RequirePermission(module = "classes", action = "u")
    @PostMapping("/update/{group_id}/status")
    public ResponseEntity<String> changeGroupStatus( @RequestHeader("Authorization") String authHeader,
                                                    @RequestParam(defaultValue = "es") String lang,
                                                    @PathVariable("group_id") Long group_id) {
        try {
            String token = authHeader.substring(7);
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            String jsonResponse = groupService.changeGroupStatus(tokenSchoolId, group_id, lang);
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
	
        @RequirePermission(module = "classes", action = "r")
    @GetMapping("/catalog")
    public ResponseEntity<List<GetClassesCatalog>> getClassesCatalog(
        @RequestHeader("Authorization") String authHeader,
		@RequestParam(required = false) Long school_id,
		@RequestParam(defaultValue = "es") String lang
    ) {
        // strip off "Bearer "
        String 	token    = authHeader.replaceFirst("^Bearer\\s+", "");
            Long 		token_user_id= jwtUtil.extractUserId(token);

        List<GetClassesCatalog> balances = groupService.getClassesCatalog(token_user_id, school_id, lang);
        return ResponseEntity.ok(balances);
    }
    

}
