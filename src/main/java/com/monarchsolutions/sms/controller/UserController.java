package com.monarchsolutions.sms.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.dto.user.CreateUserRequest;
import com.monarchsolutions.sms.dto.user.UpdatePasswordRequest;
import com.monarchsolutions.sms.dto.user.UpdateUserRequest;
import com.monarchsolutions.sms.dto.user.UserBalanceDTO;
import com.monarchsolutions.sms.dto.user.UserDetails;
import com.monarchsolutions.sms.dto.user.UsersBalanceDTO;
import com.monarchsolutions.sms.service.UserService;
import com.monarchsolutions.sms.util.JwtUtil;
import com.monarchsolutions.sms.validation.AdminGroup;
import com.monarchsolutions.sms.validation.SchoolAdminGroup;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private Validator validator;
	
	@Autowired
	private ObjectMapper objectMapper;

	// Endpoint to create a new user or multiple users.
	@PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
	@PostMapping("/create")
	public ResponseEntity<?> createUser(@RequestBody Object payload,
											@RequestHeader("Authorization") String authHeader,
											@RequestParam(defaultValue = "en") String lang) {
		try {
			// Extract the token (remove "Bearer " prefix)
			String token = authHeader.substring(7);
			Long responsible_user_id = jwtUtil.extractUserId(token);
			String role = jwtUtil.extractUserRole(token);
			// Decide the validation group based on the role.
			Class<?> validationGroup = "SCHOOL_ADMIN".equalsIgnoreCase(role) ? SchoolAdminGroup.class : AdminGroup.class;
			
			List<CreateUserRequest> requests = new ArrayList<>();
			
			// Check if payload is an array or a single object.
			// (Using ObjectMapper conversion instead of "instanceof List" on the already-converted type.)
			if (objectMapper.convertValue(payload, Object.class) instanceof List) {
				requests = objectMapper.convertValue(payload, new TypeReference<List<CreateUserRequest>>() {});
			} else {
				CreateUserRequest singleRequest = objectMapper.convertValue(payload, CreateUserRequest.class);
				requests.add(singleRequest);
			}
			
			// Validate each request using the chosen validation group.
			for (CreateUserRequest req : requests) {
				Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(req, validationGroup);
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
			for (CreateUserRequest req : requests) {
				jsonResponse = userService.createUser(tokenSchoolId, lang, responsible_user_id, req);
			}
			
			return ResponseEntity.ok(jsonResponse);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// Endpoint to update an existing user.
	@PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
	@PutMapping("/update/{user_id}")
	public ResponseEntity<?> updateUser(@RequestBody UpdateUserRequest request,
											@RequestHeader("Authorization") String authHeader,
											@RequestParam(defaultValue = "en") String lang,
											@PathVariable("user_id") Long user_id) {
		try {
			request.setUser_id(user_id);
			// Extract the token (remove "Bearer " prefix)
			String token = authHeader.substring(7);
			String role = jwtUtil.extractUserRole(token);
			// Decide the validation group based on the role.
			Class<?> validationGroup = "SCHOOL_ADMIN".equalsIgnoreCase(role) ? SchoolAdminGroup.class : AdminGroup.class;
			
			// Validate the payload using the chosen validation group.
			Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request, validationGroup);
			if (!violations.isEmpty()) {
				String errorMessage = violations.stream()
						.map(ConstraintViolation::getMessage)
						.collect(Collectors.joining("; "));
				return ResponseEntity.badRequest().body(errorMessage);
			}
			
			// Extract data from the token.
			Long tokenSchoolId = jwtUtil.extractSchoolId(token);
			Long responsible_user_id = jwtUtil.extractUserId(token);
			// Call the service method (which will hash the password and pass the JSON data to the SP)
			String jsonResponse = userService.updateUser(tokenSchoolId, lang, user_id, responsible_user_id, request);
			return ResponseEntity.ok(jsonResponse);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// Endpoint to toggle or change the user's status.
	@PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
	@PostMapping("/update/{userId}/status")
	public ResponseEntity<?> changeUserStatus(@PathVariable("userId") Integer userId,
												@RequestParam(defaultValue = "en") String lang,
												@RequestHeader("Authorization") String authHeader) {
		try {
			// Extract the token (remove "Bearer " prefix)
			String token = authHeader.substring(7);
			// Extract data from the token
			Long tokenSchoolId = jwtUtil.extractSchoolId(token);
			Long responsible_user_id = jwtUtil.extractUserId(token);
			// Call the service method 
			String jsonResponse = userService.changeUserStatus(userId, lang, tokenSchoolId, responsible_user_id);
			
			return ResponseEntity.ok(jsonResponse);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// Endpoint for retrieving the list of users.
	@PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
	@GetMapping("")
	public ResponseEntity<?> getUsersList(
		@RequestHeader("Authorization") String authHeader,
		@RequestParam(required = false) Long user_id,
		@RequestParam(required = false) Long school_id,
		@RequestParam(required = false) Long role_id,
		@RequestParam(required = false) String full_name,
		@RequestParam(required = false) Boolean enabled,
		@RequestParam(defaultValue = "en")          String lang,
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
			// If the authenticated user is SCHOOL_ADMIN, filter out users with role_id 1 or 4.
			page = userService.getUsersList(
				token_user_id,
				user_id,
				school_id,
				role_id,
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

	// Endpoint for retrieving the user details
	@PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
	@GetMapping("/{userId}")
	public ResponseEntity<?> getUser(
		@RequestHeader("Authorization") String authHeader,
		@PathVariable("userId") Long userId,
		@RequestParam(defaultValue = "en") String lang
	) {
		try {
			String token = authHeader.substring(7);
			Long token_user_id = jwtUtil.extractUserId(token);
			UserDetails user;
			if (userId != null) {
				user = userService.getUser(token_user_id, userId, lang);
			} else {
				user = userService.getUser(token_user_id, token_user_id, lang);
			}

			return ResponseEntity.ok(user);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// Endpoint for retrieving the user details
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/self-details")
	public ResponseEntity<?> getUserSelfDetails(
		@RequestHeader("Authorization") String authHeader,
		@RequestParam(defaultValue = "en") String lang
	) {
		try {
			String token = authHeader.substring(7);
			Long token_user_id = jwtUtil.extractUserId(token);
			UserDetails user = userService.getUser(token_user_id, token_user_id, lang);

			return ResponseEntity.ok(user);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
  // @GetMapping("/balances")
  // public ResponseEntity<List<UserBalanceDTO>> getActiveUserBalances(
  //     @RequestHeader("Authorization") String authHeader,
	// 		@RequestParam(required = false) String search_criteria
  // ) {
  //   // strip off "Bearer "
  //   String token    = authHeader.replaceFirst("^Bearer\\s+", "");
  //   Long   schoolId = jwtUtil.extractSchoolId(token);

  //   List<UserBalanceDTO> balances = userService.getActiveUserBalances(schoolId, search_criteria);
  //   return ResponseEntity.ok(balances);
  // }
	
	@PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN','FINANCE')")
  @GetMapping("/balances")
  public ResponseEntity<List<UsersBalanceDTO>> getUsersBalance(
      @RequestHeader("Authorization") String authHeader,
			@RequestParam(required = false) String full_name,
			@RequestParam(defaultValue = "en") String lang
  ) {
    // strip off "Bearer "
    String 	token    = authHeader.replaceFirst("^Bearer\\s+", "");
		Long 		token_user_id= jwtUtil.extractUserId(token);

    List<UsersBalanceDTO> balances = userService.getUsersBalance(token_user_id, full_name, lang);
    return ResponseEntity.ok(balances);
  }

	@PreAuthorize("isAuthenticated()")
	@PutMapping("/password")
	public ResponseEntity<Map<String,Object>> updatePassword(
		@RequestHeader("Authorization") String authHeader,
		@RequestBody UpdatePasswordRequest req,
		@RequestParam(defaultValue = "en") String lang
	) {
		// Prepare the response map once
		Map<String,Object> resp = new LinkedHashMap<>();

		// Extract token_user_id
		String token    = authHeader.replaceFirst("^Bearer\\s+", "");
		Long   userId   = jwtUtil.extractUserId(token);

		try {
				userService.changePassword(userId, req);

				// success
				resp.put("success", true);
				resp.put("type",    "success");
				resp.put("title",   lang.equalsIgnoreCase("es") ? "Éxito" : "Success");
				resp.put("message",
					lang.equalsIgnoreCase("es")
						? "Contraseña actualizada correctamente."
						: "Password updated successfully."
				);
				return ResponseEntity.ok(resp);

		} catch (IllegalArgumentException iae) {
				// e.g. bad old password or user not found
				resp.put("success", false);
				resp.put("type",    "error");
				resp.put("title",
					lang.equalsIgnoreCase("es") ? "Error" : "Error"
				);
				// Choose message based on lang
				String msg = iae.getMessage();
				// If you want static translations instead of exception text:
				if (lang.equalsIgnoreCase("es")) {
					if ("Current password is incorrect".equals(msg)) {
						msg = "La contraseña actual es incorrecta.";
					} else if ("User not found".equals(msg)) {
						msg = "Usuario no encontrado.";
					}
				}
				resp.put("message", msg);
				return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(resp);

		} catch (Exception e) {
			resp.put("success", false);
			resp.put("type",    "error");
			resp.put("title",
				lang.equalsIgnoreCase("es") ? "Error inesperado" : "Unexpected error"
			);
			resp.put("message",
				lang.equalsIgnoreCase("es")
					? "Algo salió mal. Por favor intente de nuevo."
					: "Something went wrong. Please try again."
			);
			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(resp);
		}
	}
}
