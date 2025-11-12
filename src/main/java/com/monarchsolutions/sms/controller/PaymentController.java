package com.monarchsolutions.sms.controller;

import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.monarchsolutions.sms.dto.payments.CreatePayment;
import com.monarchsolutions.sms.dto.payments.UpdatePaymentDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.payments.ByYearPaymentsDTO;
import com.monarchsolutions.sms.service.PaymentService;
import com.monarchsolutions.sms.util.JwtUtil;
import com.monarchsolutions.sms.validation.AdminGroup;
import com.monarchsolutions.sms.validation.SchoolAdminGroup;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintViolation;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
  private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
  
  // ← make this a String
  @Value("${app.upload-dir}")
  private String uploadDir;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private PaymentService paymentService;

  @Autowired
  private JwtUtil jwtUtil;
  
  @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
  @PostMapping(
    path = "/create",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<String> createPayment(
      @RequestHeader("Authorization") String authHeader,
      @RequestPart("request") CreatePayment request,   // JSON part
      @RequestPart(value = "receipt", required = false) MultipartFile receipt,
      @RequestParam(defaultValue = "es") String lang
  ) throws IOException {
    System.out.println(request);

    // 1) Normalize “YYYY-MM” → “YYYY-MM-01”
    String pm = request.getPayment_month();
    if (pm != null && pm.matches("\\d{4}-\\d{2}")) {
      request.setPayment_month(pm + "-01");
    }

    // 2) If there’s a receipt, save it to your protected area
    if (receipt != null && !receipt.isEmpty()) {
      try {
          // a) Clean the original filename
          String original = StringUtils.cleanPath(receipt.getOriginalFilename());
          
          // b) Build a prefix using today’s date (yyyy-MM-dd_HH-mm-ss) and the student ID
          // String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);  // e.g. "2025-05-19"
          DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
          String timestamp = LocalDateTime.now().format(fmt); // e.g. "2025-05-19_14-23-05"
          String prefix = timestamp + "-" + request.getStudent_id();  // e.g. "2025-05-19_14-23-05-317"
          
          // c) Prepare protected directory
          Path uploadPath = Paths.get(uploadDir);
          Files.createDirectories(uploadPath);
          
          // d) Create the stored filename: prefix + "_" + original
          String storedName = prefix + "_" + original;

          // Ensure the directory exists
          Path dir = Paths.get(uploadDir);
          if (!Files.exists(dir)) {
            Files.createDirectories(dir);
          }

          // Resolve target and copy the file
          Path target = dir.resolve(storedName);
          try (InputStream in = receipt.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
          }

          // e) Save the file
          receipt.transferTo(target.toFile());
          
          // f) Store back into your DTO
          request.setReceipt_file_name(original);
          // receipt_file_path is the URL path your clients will use:
          request.setReceipt_path(storedName);
      } catch (IOException e) {
          // Log the real error
          log.error("Error saving receipt file to {}", uploadDir, e);
          String err = String.format(
            "{\"title\":\"Upload Failed\",\"message\":\"%s\",\"type\":\"error\"}",
            e.getMessage().replace("\"","'")
          );
          return ResponseEntity
              .status(HttpStatus.INTERNAL_SERVER_ERROR)
              .contentType(MediaType.APPLICATION_JSON)
              .body(err);
      }
    }

    // 3) Auth extraction
    String token = authHeader.substring(7);
    Long userId = jwtUtil.extractUserId(token);

    // 4) Delegate to your service / stored procedure
    String jsonResult = paymentService.createPayment(userId, request, lang);

    return ResponseEntity
        .ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(jsonResult);
  };

  @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
  @PostMapping(
    path     = "/create/bulk",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<String> createPaymentsBulk(
      @RequestHeader("Authorization") String authHeader,
      @RequestParam(defaultValue="es")  String lang,
      @RequestBody Object payload        // could be a single object or list
  ) {
    try {
      // 1) Strip off "Bearer " and extract user
      String token      = authHeader.replaceFirst("^Bearer\\s+", "");
      Long   userId     = jwtUtil.extractUserId(token);

      // 2) Convert incoming payload to List<CreatePayment>
      List<CreatePayment> requests;
      if (payload instanceof List) {
        // direct cast won't work—use Jackson conversion:
        requests = objectMapper.convertValue(
          payload,
          new TypeReference<List<CreatePayment>>() {}
        );
      } else {
        CreatePayment single = objectMapper.convertValue(
          payload,
          CreatePayment.class
        );
        requests = List.of(single);
      }

      // (Optional) validate each request here with your Validator…

      String lastResponseJson = null;
      for (CreatePayment req : requests) {
        // ―― normalize payment_month as before ――
        String pm = req.getPayment_month();
        if (pm != null && pm.matches("\\d{4}-\\d{2}")) {
          req.setPayment_month(pm + "-01");
        }
        // 3) Delegate to your existing service, passing `receipt=null`
        lastResponseJson = paymentService.createPayment(
          userId,
          req,
          lang
        );
      }

      // return the *last* SP response as JSON
      return ResponseEntity.ok(lastResponseJson);

    } catch (Exception e) {
      // you might want to aggregate errors instead of just returning the last
      return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body("{\"error\":\"" + e.getMessage().replace("\"","'") + "\"}");
    }
  }

  // Endpoint to update an existing user.
  @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
  @PutMapping(
    path     = "/update/{payment_id}",
    produces = MediaType.APPLICATION_JSON_VALUE
    // ← removed `consumes` so it will accept both JSON PUTs and multipart PUTs
  )
  public ResponseEntity<String> updatePayment(
      @RequestHeader("Authorization") String authHeader,
      @PathVariable("payment_id")  Long paymentId,
      @RequestParam(value = "removeReceipt", defaultValue = "false")
          boolean removeReceipt,
      @RequestPart(value = "request", required = false)
          UpdatePaymentDTO dto,                  // now optional
      @RequestPart(value = "receipt", required = false)
          MultipartFile receipt,
      @RequestParam(defaultValue = "es") String lang
  ) {
    try {
      // 1) ensure dto exists and has its ID
      if (dto == null) {
        dto = new UpdatePaymentDTO();
      }
      dto.setPayment_id(paymentId);

      Long responsibleUserId = jwtUtil.extractUserId(authHeader.substring(7));
      String json = paymentService.updatePayment(
        responsibleUserId, paymentId, dto, removeReceipt, receipt, lang
      );
      return ResponseEntity.ok(json);

    } catch (Exception e) {
      log.error("Error updating payment", e);
      return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("{\"title\":\"Update Failed\",\"message\":\""
              + e.getMessage().replace("\"","'")
              + "\",\"type\":\"error\"}");
    }
  }


  @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN','STUDENT')")
  @GetMapping("/grouped")
  public ResponseEntity<List<ByYearPaymentsDTO>> getGroupedPayments(
      @RequestHeader("Authorization") String authHeader,
      @RequestParam(required = false) Integer paymentId,
      @RequestParam(required = false) Integer paymentRequestId,
      @RequestParam(required = false) String  ptName,
      @RequestParam(required = false) LocalDate paymentMonth,
      @RequestParam(required = false) LocalDate paymentCreatedAt,
      @RequestParam(required = false, defaultValue = "false") Boolean tuitions,
      @RequestParam(defaultValue = "es")     String lang
  ) {
    String token = authHeader.replaceFirst("^Bearer\\s+", "");
    Long   tokenUser = jwtUtil.extractUserId(token);

    List<ByYearPaymentsDTO> grouped =
      paymentService.getGroupedPayments(
        tokenUser, paymentId, paymentRequestId,
        ptName, paymentMonth, paymentCreatedAt,
        tuitions, lang
      );
    return ResponseEntity.ok(grouped);
  }
}