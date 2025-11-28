package com.monarchsolutions.sms.controller;

import com.monarchsolutions.sms.dto.common.UserLoginDTO;
import com.monarchsolutions.sms.dto.user.UserDetailsCache;
import com.monarchsolutions.sms.service.AuthService;
import com.monarchsolutions.sms.service.UserService;
import com.monarchsolutions.sms.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthService authService;

	@Autowired
	private UserService userService;


	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	@PostMapping("/login")
	public ResponseEntity<?> login(
	@RequestParam(defaultValue = "es") String lang,
	@RequestBody UserLoginDTO loginRequest) throws Exception {
		// Retrieve user by username or email via stored procedure
		UserLoginDTO user = authService.getUserByUsernameOrEmail(
				loginRequest.getUsernameOrEmail(), lang
		);
		if (user == null) {
			String errorMsg = "Invalid username or email";
			if ("es".equalsIgnoreCase(lang)) {
				errorMsg = "wrong_credentials";
			}
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMsg);
		}
		// Check the hashed password
		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			String errorMsg = "wrong_credentials";
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMsg);
		}
		
		if (!user.getEnabledUser()||!user.getEnabledRole()) {
			String errorMsg = "user_disabled";
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMsg);
		}

		if (user.getEnabledSchool()!=null&&!user.getEnabledSchool()) {
			String errorMsg = "user_disabled";
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMsg);
		}

		// Assume the role is provided in user.getRoleName() (adjust as necessary)
		String token = jwtUtil.generateToken(user.getUserId(), user.getSchoolId(), user.getUsername(), user.getRoleName(), user.getRoleId());
		
		Long token_user_id = jwtUtil.extractUserId(token);
		UserDetailsCache userDetails = userService.getUserBasic(token_user_id, user.getUserId(), lang);

		// Custom response DTO with token and user details
		return ResponseEntity.ok(new LoginResponse(token, userDetails));
	}
}

// A simple DTO for login responses
class LoginResponse {
	private String token;
	private Object user; // you can use a specific DTO type

	public LoginResponse(String token, Object user) {
		this.token = token;
		this.user = user;
	}

	public String getToken() {
		return token;
	}
	public Object getUser() {
		return user;
	}
}
