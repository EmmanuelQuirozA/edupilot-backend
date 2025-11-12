package com.monarchsolutions.sms.controller;

import com.monarchsolutions.sms.dto.common.ScholarLevelsDTO;
import com.monarchsolutions.sms.service.ScholarLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scholar-levels")
public class ScholarLevelController {

    @Autowired
    private ScholarLevelService scholarLevelService;

    @GetMapping("/list")
    public ResponseEntity<List<ScholarLevelsDTO>> getScholarLevels(@RequestParam(defaultValue = "es") String lang) {
        List<ScholarLevelsDTO> responses = scholarLevelService.getScholarLevels(lang);
        return ResponseEntity.ok(responses);
    }
}
