package com.monarchsolutions.sms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.dto.school.CreateSchoolRequest;
import com.monarchsolutions.sms.dto.school.UpdateSchoolRequest;
import com.monarchsolutions.sms.dto.school.SchoolsList;
import com.monarchsolutions.sms.service.SchoolService;
import com.monarchsolutions.sms.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.monarchsolutions.sms.annotation.RequirePermission;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schools")
public class SchoolController {
	@Autowired
	private SchoolService schoolService;

	@Autowired
	private JwtUtil jwtUtil;

	@RequirePermission(module = "schools", action = "r")
	@GetMapping("/paged")
	public ResponseEntity<?> getSchools(
		@RequestHeader("Authorization") 	String authHeader,
		@RequestParam(required = false) 	Long  school_id,
		@RequestParam(defaultValue = "es")  String lang,
		@RequestParam(defaultValue = "-1") 	Integer status_filter,
		@RequestParam(required = false)  	String school_name,
		@RequestParam(defaultValue = "0")   Integer offset,
		@RequestParam(defaultValue = "10")  Integer limit,
		@RequestParam(defaultValue = "0")   boolean export_all,
		@RequestParam(required = false)     String order_by,
		@RequestParam(required = false)     String order_dir
	) {
		try {
			String token = authHeader.substring(7);
			Long   userId = jwtUtil.extractUserId(token);

			PageResult<Map<String,Object>> page = schoolService.getSchools(
				userId,
				school_id,
				lang,
				status_filter,
				school_name,
				offset,
				limit,
				export_all,
				order_by,
				order_dir
			);

			return ResponseEntity.ok(page);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// Endpoint to create a new school
	@RequirePermission(module = "schools", action = "c")
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
	@RequirePermission(module = "schools", action = "r")
        @GetMapping("/list")
        public ResponseEntity<?> getSchoolsList(@RequestHeader("Authorization") String authHeader,
                                                                                @RequestParam(required = false) Long school_id,
                                                                                @RequestParam(defaultValue = "es") String lang,
                                                                                @RequestParam(defaultValue = "-1") int status_filter) {
                try {
                        String token = authHeader.substring(7);
                        // Long token_user_id = jwtUtil.extractUserId(token);
                        Long token_user_id = jwtUtil.extractUserId(token);
                        List<SchoolsList> schools = schoolService.getSchoolsList(token_user_id, school_id, lang, status_filter);
                        // System.out.println(token_user_id);
                        return ResponseEntity.ok(schools);
                } catch (Exception e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                }
        }

        @RequirePermission(module = "schools", action = "r")
        @GetMapping("/details")
        public ResponseEntity<?> getSchoolDetails(
                        @RequestHeader("Authorization") String authHeader,
                        @RequestParam("school_id") Long schoolId,
                        @RequestParam(defaultValue = "es") String lang
        ) {
                try {
                        String token = authHeader.substring(7);
                        Long tokenUserId = jwtUtil.extractUserId(token);
                        Map<String, Object> details = schoolService.getSchoolDetails(tokenUserId, schoolId, lang);
                        return ResponseEntity.ok(details);
                } catch (Exception e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                }
        }

        // Endpoint for retrieving the list of related schools for a specific shcool.
        @RequirePermission(module = "schools", action = "r")
        @GetMapping("/listRelated")
        public ResponseEntity<?> getRelatedSchoolList(@RequestHeader("Authorization") String authHeader,
											@RequestParam(required = false) Long school_id,
											@RequestParam(defaultValue = "es") String lang) {
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
	@RequirePermission(module = "schools", action = "u")
	@PutMapping(
				path     = "/update/{school_id}",
		produces = MediaType.APPLICATION_JSON_VALUE
	)

	public ResponseEntity<?> updateSchool(
		@RequestHeader("Authorization") String authHeader,
		@PathVariable("school_id")         Long   schoolId,
		@RequestParam(defaultValue="es")   String lang,
		@RequestPart(value="request", required = false) UpdateSchoolRequest request,
		@RequestPart(value="image", required=false) MultipartFile image
	) {
		try {
			// 1) Validate image if present (PNG only)
			if (image != null && !image.isEmpty()) {
				if (!MediaType.IMAGE_PNG_VALUE.equals(image.getContentType())) {
					return ResponseEntity
						.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
						.body("{\"title\":\"Invalid File\",\"message\":\"Only PNG images are allowed\",\"type\":\"error\"}");
				}
				String ext = StringUtils.getFilenameExtension(image.getOriginalFilename());
				if (!"png".equalsIgnoreCase(ext)) {
					return ResponseEntity
						.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
						.body("{\"title\":\"Invalid File\",\"message\":\"Only .png extension is allowed\",\"type\":\"error\"}");
				}
			}

			// 2) ensure dto exists and has its ID
			if (request == null) {
				request = new UpdateSchoolRequest();
			}
			request.setSchool_id(schoolId);

			// 3) Auth
			String token         = authHeader.replaceFirst("^Bearer\\s+", "");
			Long   p_token_user_id = jwtUtil.extractUserId(token);

			// 4) Delegate
			String jsonResponse = schoolService.updateSchool(
				p_token_user_id, schoolId, request, lang, image
			);
			return ResponseEntity.ok(jsonResponse);

		} catch (Exception e) {
			String msg = e.getMessage() != null
				? e.getMessage().replace("\"","'")
				: "Unknown error";
			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("{\"title\":\"Update Failed\",\"message\":\"" + msg + "\",\"type\":\"error\"}");
		}
	}


	// Endpoint to toggle or change the user's status.
	@RequirePermission(module = "schools", action = "u")
	@PostMapping("/update/{school_id}/status")
	public ResponseEntity<String> changeUserStatus(@PathVariable("school_id") Long school_id,
												@RequestParam(defaultValue = "es") String lang,
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

	@RequirePermission(module = "schools", action = "r")
	@GetMapping("/school-image")
	public ResponseEntity<String> getSchoolImage(
		@RequestHeader("Authorization") String authHeader,
		@RequestParam(required = false) Long school_id
	) {
		// 2) Extract the token and get userId
		String token = authHeader.substring(7);
		Long token_user_id = jwtUtil.extractUserId(token);
		String school_image = schoolService.getSchoolImage(token_user_id, school_id);
		return ResponseEntity.ok(school_image);
	}

	@RequirePermission(module = "schools", action = "r")
	@GetMapping("/commercial-name")
	public ResponseEntity<String> getCommercialName(
		@RequestHeader("Authorization") String authHeader
	) {
		String token = authHeader.substring(7);
		Long tokenSchoolId = jwtUtil.extractSchoolId(token);
		String commercialName = schoolService.getSchoolCommercialName(tokenSchoolId);
		return ResponseEntity.ok(commercialName);
	}

	@GetMapping("/user-school")
	public ResponseEntity<String> getuserSchool(@RequestHeader("Authorization") String authHeader) {
		// 2) Extract the token and get userId
		String token = authHeader.substring(7);
		Long token_user_id = jwtUtil.extractUserId(token);
		String school = schoolService.getUserSchool(token_user_id);
		return ResponseEntity.ok(school);
	}
	
}
