package com.monarchsolutions.sms.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.monarchsolutions.sms.annotation.RequirePermission;
import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.service.ClassService;
import com.monarchsolutions.sms.util.JwtUtil;

@RestController
@RequestMapping("/api/classes")
public class ClassController {

  @Autowired
  private ClassService classService;

  @Autowired
  private JwtUtil jwtUtil;

  // Endpoint for retrieving the list of paymentDetails.
  @RequirePermission(module = "classes", action = "r")
  @GetMapping("")
  public ResponseEntity<?> getClasses(
    @RequestHeader("Authorization") String authHeader,
    @RequestParam(required = false) Long group_id,
    @RequestParam(required = false) Long school_id,
    @RequestParam(required = false) String generation,
    @RequestParam(required = false) String grade_group,
    @RequestParam(required = false) String scholar_level_name,
    @RequestParam(required = false) Boolean enabled,
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

      PageResult<Map<String,Object>> page = classService.getClasses(
        token_user_id,
        group_id,
        school_id,
        generation,
        grade_group,
        scholar_level_name,
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
  
}
