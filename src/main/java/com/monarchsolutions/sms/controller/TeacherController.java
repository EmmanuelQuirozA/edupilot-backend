package com.monarchsolutions.sms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.dto.teachers.CreateTeacherRequest;
import com.monarchsolutions.sms.service.TeacherService;
import com.monarchsolutions.sms.util.JwtUtil;
import com.monarchsolutions.sms.validation.AdminGroup;
import com.monarchsolutions.sms.validation.SchoolAdminGroup;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {
  
  @Autowired
  private TeacherService teacherService;
	
	@Autowired
	private Validator validator;
	
	@Autowired
	private ObjectMapper objectMapper;

  @Autowired
  private JwtUtil jwtUtil;
  // Endpoint for retrieving the list of teachers.
  @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
  @GetMapping("")
  public ResponseEntity<?> getTeachersList(
      @RequestHeader("Authorization") String authHeader,
      @RequestParam(required = false) Long user_id,
      @RequestParam(required = false) Long school_id,
      @RequestParam(required = false) String full_name,
      @RequestParam(required = false) Boolean enabled,
      @RequestParam(defaultValue = "es")          String lang,
      @RequestParam(defaultValue = "0")           Integer offset,
      @RequestParam(defaultValue = "10")          Integer limit,
      @RequestParam(name = "export_all", defaultValue = "false") Boolean exportAll,
      @RequestParam(required = false) String order_by,
      @RequestParam(required = false) String order_dir
  ) throws Exception {
      try {
          // strip off "Bearer "
          String token    = authHeader.replaceFirst("^Bearer\\s+", "");
          Long   token_user_id = jwtUtil.extractUserId(token);
          PageResult<Map<String,Object>> page;
          // If the authenticated user is SCHOOL_ADMIN, filter out teachers with role_id 1 or 4.
          page = teacherService.getTeacherList(
              token_user_id,
              user_id,
              school_id,
              full_name,
              enabled,
              lang,
              offset,
              limit,
              exportAll,
              order_by,
              order_dir
          );
          return ResponseEntity.ok(page);
      } catch (Exception e) {
          return ResponseEntity.badRequest().body(e.getMessage());
      }
  }

  // Endpoint to create a new user or multiple users.
	@PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
	@PostMapping("/create")
	public ResponseEntity<?> createTeacher(@RequestBody Object payload,
											@RequestHeader("Authorization") String authHeader,
											@RequestParam(defaultValue = "es") String lang) {
		try {
			// Extract the token (remove "Bearer " prefix)
			String token = authHeader.substring(7);
			Long responsible_user_id = jwtUtil.extractUserId(token);
			String role = jwtUtil.extractUserRole(token);
			// Decide the validation group based on the role.
			Class<?> validationGroup = "SCHOOL_ADMIN".equalsIgnoreCase(role) ? SchoolAdminGroup.class : AdminGroup.class;
			
			List<CreateTeacherRequest> requests = new ArrayList<>();
			
			// Check if payload is an array or a single object.
			// (Using ObjectMapper conversion instead of "instanceof List" on the already-converted type.)
			if (objectMapper.convertValue(payload, Object.class) instanceof List) {
				requests = objectMapper.convertValue(payload, new TypeReference<List<CreateTeacherRequest>>() {});
			} else {
				CreateTeacherRequest singleRequest = objectMapper.convertValue(payload, CreateTeacherRequest.class);
				requests.add(singleRequest);
			}
			
			// Validate each request using the chosen validation group.
			for (CreateTeacherRequest req : requests) {
				Set<ConstraintViolation<CreateTeacherRequest>> violations = validator.validate(req, validationGroup);
				if (!violations.isEmpty()) {
					String errorMessage = violations.stream()
							.map(ConstraintViolation::getMessage)
							.collect(Collectors.joining("; "));
					return ResponseEntity.badRequest().body(errorMessage);
				}
			}
			
			Long tokenSchoolId = jwtUtil.extractSchoolId(token);
			String jsonResponse = "";
			// Process each request (mass or single upload)
			for (CreateTeacherRequest req : requests) {
				jsonResponse = teacherService.createTeacher(tokenSchoolId, lang, responsible_user_id, req);
			}
			
			return ResponseEntity.ok(jsonResponse);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}
