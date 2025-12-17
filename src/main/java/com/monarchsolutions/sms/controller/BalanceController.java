package com.monarchsolutions.sms.controller;

import com.monarchsolutions.sms.annotation.RequirePermission;
import com.monarchsolutions.sms.dto.balance.CreateBalanceRechargeDTO;
import com.monarchsolutions.sms.dto.balance.YearlyActivityDto;
import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.service.BalanceService;
import com.monarchsolutions.sms.util.JwtUtil;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/balances")
public class BalanceController {

  private final BalanceService balanceService;
  private final JwtUtil        jwtUtil;

  public BalanceController(
      BalanceService balanceService,
      JwtUtil jwtUtil
  ) {
    this.balanceService = balanceService;
    this.jwtUtil        = jwtUtil;
  }


  @RequirePermission(module = "balance", action = "c")
  @PostMapping("/recharge")
  public ResponseEntity<String> recharge(
      @RequestHeader("Authorization") String authHeader,
      @RequestParam(defaultValue = "es") String lang,
      @RequestBody CreateBalanceRechargeDTO dto
  ) {
    try {
      String token       = authHeader.replaceFirst("^Bearer\\s+", "");
      Long   tokenUserId = jwtUtil.extractUserId(token);

      String result = balanceService.rechargeBalance(tokenUserId, dto, lang);
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      String err = String.format(
        "{\"success\":false,\"message\":\"%s\"}",
        e.getMessage().replace("\"","'")
      );
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
  }

  // Endpoint for retrieving the list of paymentDetails.
  @RequirePermission(module = "balance", action = "r")
  @GetMapping("/account-activity")
  public ResponseEntity<?> getBalanceRecharges(
    @RequestHeader("Authorization") String authHeader,
    @RequestParam(required = false) Long user_id,
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

      PageResult<Map<String,Object>> page = balanceService.getAccountActivity(
        token_user_id,
        effectiveuserId,
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

  // @RequirePermission(module = "balance", action = "r")
  @GetMapping("/account-activity-grouped")
  public ResponseEntity<List<YearlyActivityDto>> getAccountActivityGroup(
      @RequestHeader("Authorization") String authHeader,
      @RequestParam(required = false) Long user_id,
      @RequestParam(defaultValue = "es") String lang
  ) {
    
    // strip off "Bearer "
    String token    = authHeader.replaceFirst("^Bearer\\s+", "");
    Long   token_user_id = jwtUtil.extractUserId(token);
    String role        = jwtUtil.extractUserRole(token);

    // 2) if STUDENT, override student_id with their own
    Long effectiveuserId = user_id;
    if ("STUDENT".equalsIgnoreCase(role)) {
      effectiveuserId = token_user_id;
    }

    List<YearlyActivityDto> grouped =
      balanceService.getAccountActivityGroup(token_user_id, effectiveuserId, lang
      );
    return ResponseEntity.ok(grouped);
  }

}
