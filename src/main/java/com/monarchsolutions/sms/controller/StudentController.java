package com.monarchsolutions.sms.controller;

import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.dto.student.CreateStudentRequest;
import com.monarchsolutions.sms.dto.student.GetStudent;
import com.monarchsolutions.sms.dto.student.GetStudentDetails;
import com.monarchsolutions.sms.dto.student.UpdateStudentRequest;
import com.monarchsolutions.sms.dto.student.ValidateStudentExist;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.service.StudentService;
import com.monarchsolutions.sms.util.JwtUtil;
import com.monarchsolutions.sms.validation.AdminGroup;
import com.monarchsolutions.sms.validation.SchoolAdminGroup;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    
    @Autowired
    private StudentService studentService;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private Validator validator;
    
    @Autowired
    private ObjectMapper objectMapper;

    // Endpoint to create a new user
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<String> createStudent(@RequestBody Object payload,
                                            @RequestHeader("Authorization") String authHeader,
                                            @RequestParam(defaultValue = "es") String lang) {
        try {
            // Extract the token (remove "Bearer " prefix)
            String token = authHeader.substring(7);
            Long responsible_user_id = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractUserRole(token);
            // Decide the validation group based on the role.
            Class<?> validationGroup = "SCHOOL_ADMIN".equalsIgnoreCase(role) ? SchoolAdminGroup.class : AdminGroup.class;
            
            List<CreateStudentRequest> requests = new ArrayList<>();
            
            // Check if payload is an array or a single object.
            // (Using ObjectMapper conversion instead of "instanceof List" on the already-converted type.)
            if (objectMapper.convertValue(payload, Object.class) instanceof List) {
                requests = objectMapper.convertValue(payload, new TypeReference<List<CreateStudentRequest>>() {});
            } else {
                CreateStudentRequest singleRequest = objectMapper.convertValue(payload, CreateStudentRequest.class);
                requests.add(singleRequest);
            }
            
            // Validate each request using the chosen validation group.
            for (CreateStudentRequest req : requests) {
                Set<ConstraintViolation<CreateStudentRequest>> violations = validator.validate(req, validationGroup);
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
            for (CreateStudentRequest req : requests) {
                jsonResponse = studentService.createStudent(tokenSchoolId, lang, responsible_user_id, req);
            }
            
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint for retrieving the list of students.
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @GetMapping("")
    public ResponseEntity<?> getStudentsList(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(required = false) Long student_id,
        @RequestParam(required = false) String register_id,
        @RequestParam(required = false) String full_name,
        @RequestParam(required = false) String payment_reference,
        @RequestParam(required = false) String generation,
        @RequestParam(required = false) String grade_group,
        @RequestParam(required = false) Boolean enabled,
        @RequestParam(defaultValue = "es") String lang,
        @RequestParam(defaultValue = "0")  Integer offset,
        @RequestParam(defaultValue = "10") Integer limit,
        @RequestParam(name = "export_all", defaultValue = "false") Boolean exportAll,
        @RequestParam(required = false) String order_by,
        @RequestParam(required = false) String order_dir
        ) throws Exception {
        try {
            // strip off "Bearer "
            String token    = authHeader.replaceFirst("^Bearer\\s+", "");
            Long   token_user_id = jwtUtil.extractUserId(token);
            PageResult<Map<String,Object>> page = studentService.getStudentsList(
                token_user_id,  
                student_id,
                register_id,
                full_name,
                payment_reference,
                generation,
                grade_group,
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

    // Endpoint to update an existing user.
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @PutMapping("/update/{user_id}")
    public ResponseEntity<String> updateStudent(@RequestBody UpdateStudentRequest request,
                                             @RequestHeader("Authorization") String authHeader,
                                             @RequestParam(defaultValue = "es") String lang,
                                             @PathVariable("user_id") Long user_id) {
        try {
            request.setUser_id(user_id);
            // Extract the token (remove "Bearer " prefix)
            String token = authHeader.substring(7);
            String role = jwtUtil.extractUserRole(token);
            // Decide the validation group based on the role.
            Class<?> validationGroup = "SCHOOL_ADMIN".equalsIgnoreCase(role) ? SchoolAdminGroup.class : AdminGroup.class;
            
            // Validate the payload using the chosen validation group.
            Set<ConstraintViolation<UpdateStudentRequest>> violations = validator.validate(request, validationGroup);
            if (!violations.isEmpty()) {
                String errorMessage = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining("; "));
                return ResponseEntity.badRequest().body(errorMessage);
            }

            // Extract schoolId from the token (if available)
            Long tokenSchoolId = jwtUtil.extractSchoolId(token);
            Long responsible_user_id = jwtUtil.extractUserId(token);
            // Call the service method (which will hash the password and pass the JSON data to the SP)
            String jsonResponse = studentService.updateStudent(tokenSchoolId, user_id, lang, responsible_user_id, request);
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint for retrieving the list of related schools for a specific shcool.
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN','STUDENT')")
    @GetMapping("/student-details/{student_id}")
    public ResponseEntity<?> getStudent(@RequestHeader("Authorization") String authHeader,
                                        @PathVariable(required = true) Long student_id,
                                        @RequestParam(defaultValue = "es") String lang) {
        try {
            String token = authHeader.substring(7);
            Long token_user_id = jwtUtil.extractUserId(token);
            List<GetStudent> student = studentService.getStudent(token_user_id, student_id, lang);
            return ResponseEntity.ok(student);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint for retrieving the list of related schools for a specific shcool.
    @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN','STUDENT')")
    @GetMapping("/read-only")
    public ResponseEntity<?> getStudentDetails(@RequestHeader("Authorization") String authHeader,
                                        @RequestParam(required = false) Long student_id,
                                        @RequestParam(defaultValue = "es") String lang) {
        try {
            String token = authHeader.substring(7);
            Long token_user_id = jwtUtil.extractUserId(token);
            GetStudentDetails student = studentService.getStudentDetails(token_user_id, student_id, lang);
            return ResponseEntity.ok(student);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
	
	@PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
    @GetMapping("/validate-exist")
    public ResponseEntity<List<ValidateStudentExist>> validateStudentExists(
        @RequestHeader("Authorization") String authHeader,
		@RequestParam(required = false) String register_id,
		@RequestParam(required = false) String payment_reference,
		@RequestParam(required = false) String username
    ) {
        // strip off "Bearer "
        String 	token    = authHeader.replaceFirst("^Bearer\\s+", "");
            Long 		token_user_id= jwtUtil.extractUserId(token);

        List<ValidateStudentExist> students = studentService.validateStudentExists(token_user_id, register_id, payment_reference, username);
        return ResponseEntity.ok(students);
    }

    
}
