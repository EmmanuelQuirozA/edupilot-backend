package com.monarchsolutions.sms.controller;

import com.monarchsolutions.sms.dto.catalogs.PaymentConceptsDto;
import com.monarchsolutions.sms.dto.catalogs.PaymentStatusesDto;
import com.monarchsolutions.sms.dto.catalogs.PaymentThroughDto;
import com.monarchsolutions.sms.dto.catalogs.PeriodOfTimeDto;
import com.monarchsolutions.sms.dto.catalogs.PlanModuleDto;
import com.monarchsolutions.sms.dto.catalogs.ScholarLevelsDto;
import com.monarchsolutions.sms.service.CatalogsService;
import com.monarchsolutions.sms.util.JwtUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {
  @Autowired
  private CatalogsService CatalogsService;

  @Autowired
  private JwtUtil jwtUtil;

  @GetMapping("/payment-concepts")
  public ResponseEntity<List<PaymentConceptsDto>> paymentConcepts(
          @RequestHeader("Authorization") String authHeader,
          @RequestParam(name = "school_id", required = false) Long schoolId,
          @RequestParam(defaultValue = "es") String lang) {
      String token = authHeader.replaceFirst("^Bearer\\s+", "");
      Long tokenUserId = jwtUtil.extractUserId(token);
      return ResponseEntity.ok(CatalogsService.getPaymentConcepts(lang, tokenUserId, schoolId));
  }

  @GetMapping("/payment-statuses")
  public ResponseEntity<List<PaymentStatusesDto>> paymentStatuses(
          @RequestParam(defaultValue = "es") String lang) {
      return ResponseEntity.ok(CatalogsService.getPaymentStatuses(lang));
  }

  @GetMapping("/payment-through")
  public ResponseEntity<List<PaymentThroughDto>> paymentThrough(
          @RequestHeader("Authorization") String authHeader,
          @RequestParam(name = "school_id", required = false) Long schoolId,
          @RequestParam(defaultValue = "es") String lang) {
      String token = authHeader.replaceFirst("^Bearer\\s+", "");
      Long tokenUserId = jwtUtil.extractUserId(token);
      return ResponseEntity.ok(CatalogsService.getPaymentThrough(lang, tokenUserId, schoolId));
  }

  @GetMapping("/scholar-levels")
  public ResponseEntity<List<ScholarLevelsDto>> scholarLevels(
          @RequestParam(defaultValue = "es") String lang) {
      return ResponseEntity.ok(CatalogsService.getScholarLevels(lang));
  }

  @GetMapping("/period-of-time")
  public ResponseEntity<List<PeriodOfTimeDto>> periodOfTime(
          @RequestHeader("Authorization") String authHeader,
          @RequestParam(defaultValue = "es") String lang) {
      // strip off "Bearer "
      String token    = authHeader.replaceFirst("^Bearer\\s+", "");
      Long   token_user_id = jwtUtil.extractUserId(token);
      return ResponseEntity.ok(CatalogsService.getPeriodOfTime(lang, token_user_id));
  }

  @GetMapping("/plan-modules")
  public ResponseEntity<List<PlanModuleDto>> planModules(
          @RequestHeader("Authorization") String authHeader,
          @RequestParam(name = "school_id", required = false) Long schoolId,
          @RequestParam(defaultValue = "es") String lang) {
      String token = authHeader.replaceFirst("^Bearer\\s+", "");
      Long tokenUserId = jwtUtil.extractUserId(token);
      return ResponseEntity.ok(CatalogsService.getPlanModules(lang, tokenUserId, schoolId));
  }
}
