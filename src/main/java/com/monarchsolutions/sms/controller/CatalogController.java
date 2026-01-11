package com.monarchsolutions.sms.controller;

import com.monarchsolutions.sms.annotation.RequirePermission;
import com.monarchsolutions.sms.dto.catalogs.PaymentConceptsDto;
import com.monarchsolutions.sms.dto.catalogs.PaymentStatusesDto;
import com.monarchsolutions.sms.dto.catalogs.PaymentThroughDto;
import com.monarchsolutions.sms.dto.catalogs.PeriodOfTimeDto;
import com.monarchsolutions.sms.dto.catalogs.PlanModuleDto;
import com.monarchsolutions.sms.dto.catalogs.ScholarLevelsDto;
import com.monarchsolutions.sms.service.CatalogsService;
import com.monarchsolutions.sms.util.JwtUtil;
import java.util.Map;
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

  @RequirePermission(module = "payments", action = "c")
  @PostMapping("/payment-concepts")
  public ResponseEntity<Map<String, Object>> createPaymentConcept(
          @RequestHeader("Authorization") String authHeader,
          @RequestBody Map<String, Object> payload,
          @RequestParam(defaultValue = "es") String lang) throws Exception {
      String token = authHeader.replaceFirst("^Bearer\\s+", "");
      Long tokenUserId = jwtUtil.extractUserId(token);
      Map<String, Object> response = CatalogsService.createPaymentConcept(tokenUserId, payload, lang);
      return ResponseEntity.ok(response);
  }

  @RequirePermission(module = "payments", action = "u")
  @PutMapping("/payment-concepts")
  public ResponseEntity<Map<String, Object>> updatePaymentConcept(
          @RequestHeader("Authorization") String authHeader,
          @RequestParam("payment_concept_id") Long paymentConceptId,
          @RequestBody Map<String, Object> payload,
          @RequestParam(defaultValue = "es") String lang) throws Exception {
      String token = authHeader.replaceFirst("^Bearer\\s+", "");
      Long tokenUserId = jwtUtil.extractUserId(token);
      Map<String, Object> response =
              CatalogsService.updatePaymentConcept(tokenUserId, paymentConceptId, payload, lang);
      return ResponseEntity.ok(response);
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

  @RequirePermission(module = "payments", action = "c")
  @PostMapping("/payment-through")
  public ResponseEntity<Map<String, Object>> createPaymentThrough(
          @RequestHeader("Authorization") String authHeader,
          @RequestBody Map<String, Object> payload,
          @RequestParam(defaultValue = "es") String lang) throws Exception {
      String token = authHeader.replaceFirst("^Bearer\\s+", "");
      Long tokenUserId = jwtUtil.extractUserId(token);
      Map<String, Object> response = CatalogsService.createPaymentThrough(tokenUserId, payload, lang);
      return ResponseEntity.ok(response);
  }

  @RequirePermission(module = "payments", action = "u")
  @PutMapping("/payment-through")
  public ResponseEntity<Map<String, Object>> updatePaymentThrough(
          @RequestHeader("Authorization") String authHeader,
          @RequestParam("payment_through_id") Long paymentThroughId,
          @RequestBody Map<String, Object> payload,
          @RequestParam(defaultValue = "es") String lang) throws Exception {
      String token = authHeader.replaceFirst("^Bearer\\s+", "");
      Long tokenUserId = jwtUtil.extractUserId(token);
      Map<String, Object> response =
              CatalogsService.updatePaymentThrough(tokenUserId, paymentThroughId, payload, lang);
      return ResponseEntity.ok(response);
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
