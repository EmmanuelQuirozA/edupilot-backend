package com.monarchsolutions.sms.controller;

import com.monarchsolutions.sms.service.BalanceService;
import com.monarchsolutions.sms.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.monarchsolutions.sms.dto.balance.CreateBalanceRechargeDTO;

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


  @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN','FINANCE','STUDENT')")
  @PostMapping("/recharge")
  public ResponseEntity<String> recharge(
      @RequestHeader("Authorization") String authHeader,
      @RequestParam(defaultValue = "en") String lang,
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
}
