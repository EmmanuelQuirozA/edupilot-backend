package com.monarchsolutions.sms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.monarchsolutions.sms.dto.school.CreateSchoolRequest;
import com.monarchsolutions.sms.dto.school.UpdateSchoolRequest;
import com.monarchsolutions.sms.dto.school.SchoolsList;
import com.monarchsolutions.sms.service.SchoolService;
import com.monarchsolutions.sms.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
											@RequestParam(defaultValue = "es") String lang,
											@RequestParam(defaultValue = "-1") int status_filter) {
		try {
			String token = authHeader.substring(7);
			// Long token_user_id = jwtUtil.extractUserId(token);
			Long token_school_id = jwtUtil.extractSchoolId(token);
			List<SchoolsList> schools = schoolService.getSchoolsList(token_school_id, school_id, lang, status_filter);
			System.out.println(token_school_id);
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
	@PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
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
	@PreAuthorize("hasAnyRole('ADMIN')")
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

	@PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN','STUDENT')")
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
	
}
