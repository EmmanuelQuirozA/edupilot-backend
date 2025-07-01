package com.monarchsolutions.sms.controller;

import com.monarchsolutions.sms.dto.catalogs.PaymentConceptsDto;
import com.monarchsolutions.sms.dto.catalogs.PaymentStatusesDto;
import com.monarchsolutions.sms.dto.catalogs.PaymentThroughDto;
import com.monarchsolutions.sms.dto.catalogs.ScholarLevelsDto;
import com.monarchsolutions.sms.service.CatalogsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {
  @Autowired
  private CatalogsService CatalogsService;

  @GetMapping("/payment-concepts")
  public ResponseEntity<List<PaymentConceptsDto>> paymentConcepts(
          @RequestParam(defaultValue = "en") String lang) {
      return ResponseEntity.ok(CatalogsService.getPaymentConcepts(lang));
  }

  @GetMapping("/payment-statuses")
  public ResponseEntity<List<PaymentStatusesDto>> paymentStatuses(
          @RequestParam(defaultValue = "en") String lang) {
      return ResponseEntity.ok(CatalogsService.getPaymentStatuses(lang));
  }

  @GetMapping("/payment-through")
  public ResponseEntity<List<PaymentThroughDto>> paymentThrough(
          @RequestParam(defaultValue = "en") String lang) {
      return ResponseEntity.ok(CatalogsService.getPaymentThrough(lang));
  }

  @GetMapping("/scholar-levels")
  public ResponseEntity<List<ScholarLevelsDto>> scholarLevels(
          @RequestParam(defaultValue = "en") String lang) {
      return ResponseEntity.ok(CatalogsService.getScholarLevels(lang));
  }
}
