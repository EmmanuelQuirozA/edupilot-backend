package com.monarchsolutions.sms.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.monarchsolutions.sms.dto.coffee.CoffeeMenuDTO;
import com.monarchsolutions.sms.dto.coffee.CreateCoffeeMenuDTO;
import com.monarchsolutions.sms.dto.coffee.ProcessCoffeeSaleDTO;
import com.monarchsolutions.sms.dto.coffee.SaleGroupDTO;
import com.monarchsolutions.sms.dto.coffee.UpdateCoffeeMenuDTO;
import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.service.CoffeeService;
import com.monarchsolutions.sms.util.JwtUtil;
import org.springframework.util.StringUtils;

@RestController
@RequestMapping("/api/coffee")
public class CoffeeController {
  private final CoffeeService service;
  private final JwtUtil jwtUtil;

  public CoffeeController(
      CoffeeService service,
      JwtUtil jwtUtil
  ) {
    this.service = service;
    this.jwtUtil = jwtUtil;
  }

  @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN','STUDENT')")
  @GetMapping("/my-purchases")
  public ResponseEntity<List<SaleGroupDTO>> getMyPurchases(
      @RequestHeader("Authorization") String authHeader,
      @RequestParam(defaultValue = "en") String lang
  ) {
    String token = authHeader.replaceFirst("^Bearer\\s+", "");
    Long   userId= jwtUtil.extractUserId(token);

    List<SaleGroupDTO> grouped = service.getGroupedUserCoffeePurchases(userId, lang);
    return ResponseEntity.ok(grouped);
  }

  // Endpoint for retrieving the list of paymentDetails.
  @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
  @GetMapping("/menu")
  public ResponseEntity<?> getCoffeeMenu(
    @RequestHeader("Authorization") String authHeader,
    @RequestParam(required = false) Long menu_id,
    @RequestParam(required = false) String search_criteria,
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
      Long   token_school_id = jwtUtil.extractSchoolId(token);

      PageResult<Map<String,Object>> page = service.getCoffeeMenu(
        token_school_id,
        menu_id,
        search_criteria,
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

  // Endpoint for retrieving the list of paymentDetails.
  @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN','FINANCE','STUDENT')")
  @GetMapping("/sales")
  public ResponseEntity<?> getCoffeeSales(
    @RequestHeader("Authorization") String authHeader,
    @RequestParam(required = false) Long user_id,
    @RequestParam(required = false) Long school_id,
    @RequestParam(required = false) String full_name,
    @RequestParam(required = false) String item_name,
    @RequestParam(required = false) LocalDate created_at,
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
			String role        = jwtUtil.extractUserRole(token);

			// 2) if STUDENT, override student_id with their own
			Long effectiveuserId = user_id;
			if ("STUDENT".equalsIgnoreCase(role)) {
				effectiveuserId = token_user_id;
			}

      PageResult<Map<String,Object>> page = service.getCoffeeSales(
        token_user_id,
        effectiveuserId,
        school_id,
        full_name,
        item_name,
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

  // Endpoint to update an existing group.
  @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
  @PutMapping(
    path     = "/update/{menu_id}",
    produces = MediaType.APPLICATION_JSON_VALUE
    // ← removed `consumes` so it will accept both JSON PUTs and multipart PUTs
  )
  public ResponseEntity<String> updateCoffeeMenu( 
    @RequestHeader("Authorization") String authHeader,
    @PathVariable("menu_id") Long menu_id,
    @RequestParam(defaultValue = "en") String lang,
    @RequestPart(value = "request", required = false)
        UpdateCoffeeMenuDTO request,                  // now optional
    @RequestParam(value = "removeImage", defaultValue = "false")
        boolean removeImage,
    
    @RequestPart(value = "image", required = false)
        MultipartFile image
  ) {
    try {
      // ── 0) if an image was uploaded, reject non-JPEGs ──
      if (image != null && !image.isEmpty()) {
        // check MIME type
        if (!MediaType.IMAGE_JPEG_VALUE.equals(image.getContentType())) {
          return ResponseEntity
            .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
            .body("{\"title\":\"Invalid File\",\"message\":\"Only JPEG images are allowed\",\"type\":\"error\"}");
        }
        // optional: also check file extension
        String ext = StringUtils.getFilenameExtension(image.getOriginalFilename());
        if (ext == null || !ext.equalsIgnoreCase("jpg")) {
          return ResponseEntity
            .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
            .body("{\"title\":\"Invalid File\",\"message\":\"Only .jpg extension is allowed\",\"type\":\"error\"}");
        }
      }

      // 1) ensure dto exists and has its ID
      if (request == null) {
        request = new UpdateCoffeeMenuDTO();
      }
      request.setMenuId(menu_id);

      // Extract the token (remove "Bearer " prefix)
      String token = authHeader.substring(7);
      // Extract schoolId from the token (if available)
      Long token_user_id = jwtUtil.extractUserId(token);
      // Call the service method (which will pass the JSON data to the SP)
      String jsonResponse = service.updateCoffeeMenu(token_user_id, menu_id, lang, request, removeImage, image);
      return ResponseEntity.ok(jsonResponse);
    } catch (Exception e) {
      return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("{\"title\":\"Update Failed\",\"message\":\""
              + e.getMessage().replace("\"","'")
              + "\",\"type\":\"error\"}");
    }
  }

  // Endpoint to toggle or change the group's status.
  @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
  @PostMapping("/update/{menu_id}/status")
  public ResponseEntity<String> changeMenuStatus( @RequestHeader("Authorization") String authHeader,
                                                  @PathVariable("menu_id") Long menu_id,
                                                  @RequestParam(defaultValue = "en") String lang) {
    try {
      String token = authHeader.substring(7);
      Long token_user_id = jwtUtil.extractUserId(token);
      String jsonResponse = service.changeCoffeeMenuStatus(token_user_id, menu_id, lang);
      return ResponseEntity.ok(jsonResponse);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN','STUDENT')")
  @GetMapping("/{menuId}")
  public ResponseEntity<CoffeeMenuDTO> getMenu(
      @RequestHeader("Authorization") String authHeader,
      @PathVariable Long menuId
  ) {
    String token    = authHeader.replaceFirst("^Bearer\\s+", "");
    Long   schoolId = jwtUtil.extractSchoolId(token);

    CoffeeMenuDTO dto = service.getMenuDetails(schoolId, menuId);
    if (dto == null ) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(dto);
  }

  /**
   * POST /api/coffee-sales/process?lang=es
   * Body = { userId:123, items:[{menuId:1,quantity:2},…] }
   */
  @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
  @PostMapping(
    path = "/process",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<String> processSale(
      @RequestHeader("Authorization") String auth,
      @RequestParam(defaultValue = "en") String lang,
      @RequestBody ProcessCoffeeSaleDTO sale
  ) {
    try {
      // extract seller’s user_id from token
      String token = auth.replaceFirst("^Bearer\\s+", "");
      Long   sellerId = jwtUtil.extractUserId(token);

      // call service
      String resultJson = service.processSale(sellerId, sale, lang);
      return ResponseEntity.ok(resultJson);

    } catch (Exception e) {
      // unexpected error
      String err = String.format(
        "{\"success\":false,\"message\":\"%s\"}",
        e.getMessage().replace("\"","'")
      );
      return ResponseEntity
        .status(500)
        .body(err);
    }
  }

  @PreAuthorize("hasAnyRole('ADMIN','SCHOOL_ADMIN')")
  @PostMapping(
    path     = "/create",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<String> createCoffeeMenu(
      @RequestHeader("Authorization") String authHeader,
      @RequestParam(defaultValue = "en") String lang,
      @RequestPart("request") CreateCoffeeMenuDTO dto,
      @RequestPart(value="image", required=false) MultipartFile image
  ) {
    try {
      String token   = authHeader.replaceFirst("^Bearer\\s+", "");
      Long   userId  = jwtUtil.extractUserId(token);

      String result  = service.createCoffeeMenu(userId, dto, image, lang);
      return ResponseEntity.ok(result);
    } catch (IllegalArgumentException iae) {
      return ResponseEntity
        .badRequest()
        .body("{\"success\":false,\"message\":\"" 
              + iae.getMessage().replace("\"","'") + "\"}");
    } catch (Exception e) {
      return ResponseEntity
        .status(500)
        .body("{\"success\":false,\"message\":\""
              + e.getMessage().replace("\"","'") + "\"}");
    }
  }
}
