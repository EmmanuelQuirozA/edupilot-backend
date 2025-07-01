package com.monarchsolutions.sms.service;

import com.monarchsolutions.sms.dto.common.ScholarLevelsDTO;
import com.monarchsolutions.sms.entity.ScholarLevel;
import com.monarchsolutions.sms.repository.ScholarLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScholarLevelService {

    @Autowired
    private ScholarLevelRepository scholarLevelRepository;

    public List<ScholarLevelsDTO> getScholarLevels(String lang) {
        List<ScholarLevel> levels = scholarLevelRepository.findAll();
        return levels.stream().map((ScholarLevel level) -> {
            ScholarLevelsDTO response = new ScholarLevelsDTO();
            response.setScholarLevelId(level.getScholarLevelId());
            response.setName("es".equalsIgnoreCase(lang) ? level.getNameEs() : level.getNameEn());
            return response;
        }).collect(Collectors.toList());
    }
}
