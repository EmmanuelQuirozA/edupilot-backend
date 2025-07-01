package com.monarchsolutions.sms.service;

import com.monarchsolutions.sms.dto.school.SchoolsList;
import com.monarchsolutions.sms.dto.school.UpdateSchoolRequest;
import com.monarchsolutions.sms.dto.school.CreateSchoolRequest;
import com.monarchsolutions.sms.repository.SchoolRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SchoolService {
    
    @Autowired
    private SchoolRepository schoolRepository;

    public List<SchoolsList> getSchoolsList(Long user_school_id, Long school_id, String lang, int statusFilter) {
        return schoolRepository.getSchoolsList(user_school_id, school_id, lang, statusFilter);
    }

    public List<SchoolsList> getRelatedSchoolList(Long user_school_id, Long school_id, String lang) {
        return schoolRepository.getRelatedSchoolList(user_school_id, school_id, lang);
    }

    public String createSchool(CreateSchoolRequest request) throws Exception {
        // Call the repository method that converts the request to JSON and executes the stored procedure
        return schoolRepository.createSchool(request);
    }

    public String updateSchool(UpdateSchoolRequest request, Long userSchoolId, Long schoolId, String lang) throws Exception {
        return schoolRepository.updateSchool(userSchoolId, schoolId, request, lang);
    }

    public String changeSchoolStatus(Long tokenSchoolId, Long school_id, String lang) {
        return schoolRepository.changeSchoolStatus(tokenSchoolId, school_id, lang);
    }

    public String getSchoolImage(Long school_id) {
        return schoolRepository.getSchoolImage(school_id);
    }

}
