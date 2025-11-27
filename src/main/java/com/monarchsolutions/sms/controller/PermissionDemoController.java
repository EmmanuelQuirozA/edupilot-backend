package com.monarchsolutions.sms.controller;

import com.monarchsolutions.sms.annotation.RequirePermission;
import com.monarchsolutions.sms.dto.permission.StudentDTO;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/permission-demo")
public class PermissionDemoController {

    @RequirePermission(module = "students", action = "r")
    @GetMapping("/students/list")
    public ResponseEntity<List<StudentDTO>> getStudents() {
        List<StudentDTO> sampleStudents = List.of(
                new StudentDTO(1L, "Ada Lovelace", "ada@example.com"),
                new StudentDTO(2L, "Alan Turing", "alan@example.com")
        );
        return ResponseEntity.ok(sampleStudents);
    }
}
