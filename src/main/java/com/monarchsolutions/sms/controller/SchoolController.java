package com.monarchsolutions.sms.controller;

import com.monarchsolutions.sms.dto.school.CreateSchoolRequest;
import com.monarchsolutions.sms.dto.school.UpdateSchoolRequest;
import com.monarchsolutions.sms.dto.school.SchoolsList;
import com.monarchsolutions.sms.service.SchoolService;
import com.monarchsolutions.sms.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/schools")
public class SchoolController {
    @Autowired
    private SchoolService schoolService;

    @Autowired
    private JwtUtil jwtUtil;

    // Endpoint to create a new school
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<String> createSchool(@RequestBody CreateSchoolRequest request) {
        try {
            // Call the service method (which will pass the JSON data to the SP)
            String jsonResponse = schoolService.createSchool(request);
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint for retrieving the list of schools.
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<?> getSchoolsList(@RequestHeader("Authorization") String authHeader,
                                            @RequestParam(required = false) Long school_id,
                                            @RequestParam(defaultValue = "en") String lang,
                                            @RequestParam(defaultValue = "-1") int status_filter) {
        try {
            String token = authHeader.substring(7);
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            List<SchoolsList> schools = schoolService.getSchoolsList(tokenSchoolId, tokenSchoolId, lang, status_filter);
            return ResponseEntity.ok(schools);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint for retrieving the list of related schools for a specific shcool.
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/listRelated")
    public ResponseEntity<?> getRelatedSchoolList(@RequestHeader("Authorization") String authHeader,
                                            @RequestParam(required = false) Long school_id,
                                            @RequestParam(defaultValue = "en") String lang) {
        try {
            String token = authHeader.substring(7);
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            List<SchoolsList> schools = schoolService.getRelatedSchoolList(tokenSchoolId, school_id, lang);
            return ResponseEntity.ok(schools);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Endpoint to update an existing school.
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @PutMapping("/update/{school_id}")
    public ResponseEntity<?> updateSchool(@RequestHeader("Authorization") String authHeader,
                                             @PathVariable("school_id") Long school_id,
                                             @RequestBody UpdateSchoolRequest request,
                                             @RequestParam(defaultValue = "es") String lang) {
        try {
            request.setSchool_id(school_id);
            // Extract the token (remove "Bearer " prefix)
            String token = authHeader.substring(7);
            // Extract schoolId from the token (if available)
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            // Call the service method to update and get the JSON response from the SP.
            String jsonResponse = schoolService.updateSchool(request, tokenSchoolId, school_id, lang);
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint to toggle or change the user's status.
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/update/{school_id}/status")
    public ResponseEntity<String> changeUserStatus(@PathVariable("school_id") Long school_id,
                                                    @RequestParam(defaultValue = "en") String lang,
                                                    @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            // System.out.println(tokenSchoolId+" - "+school_id+" - "+lang);
            String jsonResponse = schoolService.changeSchoolStatus(tokenSchoolId, school_id, lang);
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN','STUDENT')")
    @GetMapping("/school-image")
    public ResponseEntity<String> getSchoolImage(
        @RequestHeader("Authorization") String authHeader
    ) {
        // 2) Extract the token and get userId
        String token = authHeader.substring(7);
        Long school_id = jwtUtil.extractSchoolId(token);
        if (school_id!=null) {
            String school_image = schoolService.getSchoolImage(school_id);
            return ResponseEntity.ok(school_image);
        } else {
            return null;
        }
    }
    
}
