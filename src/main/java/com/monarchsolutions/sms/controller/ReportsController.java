package com.monarchsolutions.sms.controller;

import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.dto.paymentRequests.UpdatePaymentRequest;
import com.monarchsolutions.sms.dto.student.GetStudentDetails;
import com.monarchsolutions.sms.service.ReportsService;
import com.monarchsolutions.sms.service.StudentService;
import com.monarchsolutions.sms.util.JwtUtil;


import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.monarchsolutions.sms.annotation.RequirePermission;


@RestController
@RequestMapping("/api/reports")
public class ReportsController {
    
	@Autowired
	private StudentService studentService;

	@Autowired
	private ReportsService reportsService;

	@Autowired
	private JwtUtil jwtUtil;
	
	// Endpoint to retrieve the payments pivot report.
        @RequirePermission(module = "finance", action = "r")
        @GetMapping("/payments/report")
        public ResponseEntity<?> getPaymentsPivotReport(
		@RequestHeader("Authorization") String authHeader,
		@RequestParam(required = false) Long student_id,
		@RequestParam(required = false) Long payment_id,
		@RequestParam(required = false) Long payment_request_id,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start_date,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end_date,
		@RequestParam(required = false) Boolean group_status,
		@RequestParam(required = false) Boolean user_status,
		@RequestParam(required = false) String student_full_name,
		@RequestParam(required = false) String payment_reference,
		@RequestParam(required = false) String generation,
		@RequestParam(required = false) String grade_group,
		@RequestParam(required = false) String scholar_level,
		@RequestParam(defaultValue = "es") String lang,
		@RequestParam(defaultValue = "0") Integer offset,
		@RequestParam(defaultValue = "10") Integer limit,
		@RequestParam(name = "export_all", defaultValue = "false") Boolean exportAll,
		@RequestParam(name = "show_debt_only", defaultValue = "false") Boolean showDebtOnly,
		@RequestParam(required = false) String order_by,
		@RequestParam(required = false) String order_dir
	) {
		try {
			// strip off "Bearer "
			String token    = authHeader.replaceFirst("^Bearer\\s+", "");
			Long   school_id = jwtUtil.extractSchoolId(token);
			String role      = jwtUtil.extractUserRole(token);
			Long   token_user_id = jwtUtil.extractUserId(token);
			Long   effectiveStudentId = student_id;
    	if ("STUDENT".equalsIgnoreCase(role)) {
				// pass userId as token_user_id, null for student_id
				GetStudentDetails details =
					studentService.getStudentDetails(token_user_id, null, lang);
				effectiveStudentId = details.getStudentId();
			}

			PageResult<Map<String,Object>> page = reportsService.getPaymentsPivotReport(
				school_id,
				effectiveStudentId,
				payment_id,
				payment_request_id,
				start_date,
				end_date,
				group_status,
				user_status,
				student_full_name,
				payment_reference,
				generation,
				grade_group,
				scholar_level,
				lang,
				offset,
				limit,
				exportAll,
				showDebtOnly,
				order_by,
				order_dir
			);

			return ResponseEntity.ok(page);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// Endpoint for retrieving the list of paymentDetails.
        @RequirePermission(module = "finance", action = "r")
        @GetMapping("/payments")
        public ResponseEntity<?> getPayments(
		@RequestHeader("Authorization") String authHeader,
		@RequestParam(required = false) Long school_id,
		@RequestParam(required = false) Long student_id,
		@RequestParam(required = false) Long payment_id,
		@RequestParam(required = false) Long payment_request_id,
		@RequestParam(required = false) String student_full_name,
		@RequestParam(required = false) String payment_reference,
		@RequestParam(required = false) String generation,
		@RequestParam(required = false) String grade_group,
		@RequestParam(required = false) String pt_name,
		@RequestParam(required = false) String scholar_level_name,
		@RequestParam(required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate payment_month,
		@RequestParam(required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date payment_created_at,
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

			PageResult<Map<String,Object>> page = reportsService.getPayments(
				token_user_id,
				school_id,
				student_id,
				payment_id,
				payment_request_id,
				student_full_name,
				payment_reference,
				generation,
				grade_group,
				pt_name,
				scholar_level_name,
				payment_month,
				payment_created_at,
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

	// Endpoint for retrieving the list of paymentDetails.
	
	// Endpoint to retrieve the payments pivot report.
        @RequirePermission(module = "finance", action = "r")
        @GetMapping("/paymentrequests")
        public ResponseEntity<?> getPaymentRequests(
		@RequestHeader("Authorization") String authHeader,
		@RequestParam(required = false) Long student_id,
		@RequestParam(required = false) Long school_id,
		@RequestParam(required = false) Long payment_request_id,
		@RequestParam(required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
		LocalDate pr_created_start,
		@RequestParam(required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
		LocalDate pr_created_end,
		@RequestParam(required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
		LocalDate pr_pay_by_start,
		@RequestParam(required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
		LocalDate pr_pay_by_end,
		@RequestParam(required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
		LocalDate payment_month,
		@RequestParam(required = false) String ps_pr_name,
		@RequestParam(required = false) String pt_name,
		@RequestParam(required = false) String payment_reference,
		@RequestParam(required = false) String student_full_name,
		@RequestParam(required = false) Boolean sc_enabled,
		@RequestParam(required = false) Boolean u_enabled,
		@RequestParam(required = false) Boolean g_enabled,
		@RequestParam(required = false) Integer pr_payment_status_id,
		@RequestParam(required = false) String grade_group,
		@RequestParam(defaultValue = "es") String lang,
		@RequestParam(required = false) String order_by,
		@RequestParam(required = false) String order_dir,
		@RequestParam(defaultValue = "0")  Integer offset,
		@RequestParam(defaultValue = "10") Integer limit,
		@RequestParam(name = "export_all", defaultValue = "false") Boolean exportAll
	) {
		try {
			// 1) strip off "Bearer "
			String token       = authHeader.replaceFirst("^Bearer\\s+", "");
			Long   tokenUserId = jwtUtil.extractUserId(token);
			String role        = jwtUtil.extractUserRole(token);

			// 2) if STUDENT, override student_id with their own
			Long effectiveStudentId = student_id;
			if ("STUDENT".equalsIgnoreCase(role)) {
				GetStudentDetails details =
					studentService.getStudentDetails(tokenUserId, null, lang);
				effectiveStudentId = details.getStudentId();
			}

			// 3) delegate to service / SP
			PageResult<Map<String,Object>> page =
				reportsService.getPaymentRequests(
					tokenUserId,
					school_id,
					effectiveStudentId,
					payment_request_id,
					pr_created_start,
					pr_created_end,
					pr_pay_by_start,
					pr_pay_by_end,
					payment_month,
					ps_pr_name,
					pt_name,
					payment_reference,
					student_full_name,
					sc_enabled,
					u_enabled,
					g_enabled,
					pr_payment_status_id,
					grade_group,
					lang,
					order_by,
					order_dir,
					offset,
					limit,
					exportAll
				);

			return ResponseEntity.ok(page);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
                }
        }

        @RequirePermission(module = "finance", action = "r")
        @GetMapping("/payment-request-recurrences")
        public ResponseEntity<?> getPaymentRecurrences(
                @RequestHeader("Authorization") String authHeader,
                @RequestParam(required = false) Long school_id,
                @RequestParam(required = false) Long student_id,
                @RequestParam(required = false) Long payment_request_id,
                @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                LocalDate payment_month,
                @RequestParam(required = false) String pt_name,
                @RequestParam(required = false) String payment_reference,
                @RequestParam(required = false) String student_full_name,
                @RequestParam(required = false) Boolean sc_enabled,
                @RequestParam(required = false) Boolean u_enabled,
                @RequestParam(required = false) Boolean g_enabled,
                @RequestParam(required = false) String grade_group,
                @RequestParam(defaultValue = "es") String lang,
                @RequestParam(required = false) String order_by,
                @RequestParam(required = false) String order_dir,
                @RequestParam(defaultValue = "0") Integer offset,
                @RequestParam(defaultValue = "10") Integer limit,
                @RequestParam(name = "export_all", defaultValue = "false") Boolean exportAll
        ) {
                try {
                        String token       = authHeader.replaceFirst("^Bearer\\s+", "");
                        Long   tokenUserId = jwtUtil.extractUserId(token);
                        String role        = jwtUtil.extractUserRole(token);

                        Long effectiveStudentId = student_id;
                        if ("STUDENT".equalsIgnoreCase(role)) {
                                GetStudentDetails details =
                                        studentService.getStudentDetails(tokenUserId, null, lang);
                                effectiveStudentId = details.getStudentId();
                        }

                        PageResult<Map<String,Object>> page = reportsService.getPaymentRecurrences(
                                tokenUserId,
                                school_id,
                                effectiveStudentId,
                                payment_request_id,
                                payment_month,
                                pt_name,
                                payment_reference,
                                student_full_name,
                                sc_enabled,
                                u_enabled,
                                g_enabled,
                                grade_group,
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


        @RequirePermission(module = "finance", action = "r")
        @GetMapping("/payment-request-schedule")
        public ResponseEntity<?> getPaymentRequestSchedule(
                @RequestHeader("Authorization") String authHeader,
                @RequestParam(required = false) String rule_name,
                @RequestParam(required = false) Boolean active,
                @RequestParam(required = false) Long school_id,
                @RequestParam(required = false) Long group_id,
                @RequestParam(required = false) Long student_id,
                @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                LocalDate due_start,
                @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                LocalDate due_end,
                @RequestParam(required = false) String global_search,
                @RequestParam(required = false) String order_by,
                @RequestParam(required = false) String order_dir,
                @RequestParam(defaultValue = "0") Integer offset,
                @RequestParam(defaultValue = "10") Integer limit,
                @RequestParam(name = "export_all", defaultValue = "false") Boolean exportAll,
                @RequestParam(defaultValue = "es") String lang
        ) {
                try {
                        String token       = authHeader.replaceFirst("^Bearer\\s+", "");
                        Long   tokenUserId = jwtUtil.extractUserId(token);
                        String role        = jwtUtil.extractUserRole(token);

                        Long effectiveStudentId = student_id;
                        if ("STUDENT".equalsIgnoreCase(role)) {
                                GetStudentDetails details =
                                        studentService.getStudentDetails(tokenUserId, null, lang);
                                effectiveStudentId = details.getStudentId();
                        }

                        PageResult<Map<String,Object>> page = reportsService.getPaymentRequestSchedule(
                                tokenUserId,
                                rule_name,
                                active,
                                school_id,
                                group_id,
                                effectiveStudentId,
                                due_start,
                                due_end,
                                global_search,
                                order_by,
                                order_dir,
                                offset,
                                limit,
                                exportAll,
                                lang
                        );

                        return ResponseEntity.ok(page);
                } catch (Exception e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                }
        }

        // Endpoint for retrieving the list of paymentDetails.
  @RequirePermission(module = "finance", action = "r")
  @GetMapping("/balance-recharges")
  public ResponseEntity<?> getBalanceRecharges(
    @RequestHeader("Authorization") String authHeader,
    @RequestParam(required = false) Long user_id,
    @RequestParam(required = false) Long school_id,
    @RequestParam(required = false) String full_name,
    @RequestParam(required = false) LocalDate created_at,
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
			String role        = jwtUtil.extractUserRole(token);

			// 2) if STUDENT, override student_id with their own
			Long effectiveuserId = user_id;
			if ("STUDENT".equalsIgnoreCase(role)) {
				effectiveuserId = token_user_id;
			}

      PageResult<Map<String,Object>> page = reportsService.getBalanceRecharges(
        token_user_id,
        effectiveuserId,
        school_id,
        full_name,
        created_at,
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

        @RequirePermission(module = "finance", action = "r")
        @GetMapping("/paymentrequest/details")
        public ResponseEntity<?> getPaymentRequestDetails(
		@RequestHeader("Authorization") String authHeader,
		@RequestParam Long payment_request_id,
		@RequestParam(defaultValue="es") String lang
	) {
		String token = authHeader.substring(7);
		Long schoolId = jwtUtil.extractSchoolId(token);
		Long tokenUserId = jwtUtil.extractUserId(token);
		try {
			var resp = reportsService.getPaymentRequestDetails(
				tokenUserId,
				schoolId,
				payment_request_id,
				lang
			);
			return ResponseEntity.ok(resp);
		} catch (Exception ex) {
			return ResponseEntity.status(403).body(ex.getMessage());
		}
	}

	// Endpoint to update an existing user.
        @RequirePermission(module = "finance", action = "u")
        @PutMapping("/payment-request/update/{id}")
        public ResponseEntity<?> updatePaymentRequest(
		@PathVariable("id") Long paymentRequestId,
		@RequestHeader("Authorization") String authHeader,
		@RequestBody UpdatePaymentRequest body,
		@RequestParam(defaultValue="es") String lang
	) {
                try {
                        String token = authHeader.substring(7);
                        // extract user_id from the token to pass as responsable_user_id
                        Long userId = Long.valueOf(jwtUtil.extractUserId(token));
                        Map<String,Object> jsonData = body.getData();
                        Object paymentMonth = jsonData.get("payment_month");
                        if (paymentMonth instanceof String paymentMonthStr) {
                                String normalizedPaymentMonth = paymentMonthStr.trim();
                                if (!normalizedPaymentMonth.isEmpty() && normalizedPaymentMonth.matches("\\d{4}-\\d{2}")) {
                                        jsonData.put("payment_month", normalizedPaymentMonth + "-01");
                                }
                        }
                        String jsonResult = reportsService.updatePaymentRequest(paymentRequestId, userId, jsonData, lang);
                        // the SP returns a tiny one‐row result: JSON_OBJECT AS result
                        // forward it verbatim
                        return ResponseEntity.ok()
                                                                .header("Content-Type","application/json")
								.body(jsonResult);
		} catch(Exception e) {
			return ResponseEntity.badRequest().body(Map.of(
			"success", false,
			"message", e.getMessage()
			));
		}
	}
}
