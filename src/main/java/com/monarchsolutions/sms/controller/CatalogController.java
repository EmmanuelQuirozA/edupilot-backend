package com.monarchsolutions.sms.controller;

import com.monarchsolutions.sms.annotation.RequirePermission;
import com.monarchsolutions.sms.dto.catalogs.PaymentConceptsDto;
import com.monarchsolutions.sms.dto.catalogs.PaymentStatusesDto;
import com.monarchsolutions.sms.dto.catalogs.PaymentThroughDto;
import com.monarchsolutions.sms.dto.catalogs.PeriodOfTimeDto;
import com.monarchsolutions.sms.dto.catalogs.ScholarLevelsDto;
import com.monarchsolutions.sms.service.CatalogsService;
import com.monarchsolutions.sms.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
@RequirePermission(module = "catalogs", action = "r")
public class CatalogController {
  @Autowired
  private CatalogsService CatalogsService;

  @Autowired
  private JwtUtil jwtUtil;

  @GetMapping("/payment-concepts")
  public ResponseEntity<List<PaymentConceptsDto>> paymentConcepts(
          @RequestParam(defaultValue = "es") String lang) {
      return ResponseEntity.ok(CatalogsService.getPaymentConcepts(lang));
  }

  @GetMapping("/payment-statuses")
  public ResponseEntity<List<PaymentStatusesDto>> paymentStatuses(
          @RequestParam(defaultValue = "es") String lang) {
      return ResponseEntity.ok(CatalogsService.getPaymentStatuses(lang));
  }

  @GetMapping("/payment-through")
  public ResponseEntity<List<PaymentThroughDto>> paymentThrough(
          @RequestParam(defaultValue = "es") String lang) {
      return ResponseEntity.ok(CatalogsService.getPaymentThrough(lang));
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
}
