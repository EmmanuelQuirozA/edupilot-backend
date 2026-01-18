package com.monarchsolutions.sms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.dto.student.CreateStudentRequest;
import com.monarchsolutions.sms.dto.student.GetStudent;
import com.monarchsolutions.sms.dto.student.GetStudentDetails;
import com.monarchsolutions.sms.dto.student.UpdateStudentRequest;
import com.monarchsolutions.sms.dto.student.StudentCountsResponse;
import com.monarchsolutions.sms.dto.student.ValidateStudentExist;
import com.monarchsolutions.sms.repository.StudentRepository;

import java.util.List;
import java.util.Map;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public PageResult<Map<String,Object>> getStudentsList(
        Long tokenSchoolId,  
        Long student_id,
        Long school_id,
        String register_id,
        String full_name,
        String payment_reference,
        String generation,
        String grade_group,
        Boolean enabled,
        String lang,
        int page,
        int size,
        Boolean exportAll,
        String order_by,
        String order_dir
    ) throws Exception {
        // If tokenSchoolId is not null, the SP will filter students by school.
        return studentRepository.getStudentsList(
            tokenSchoolId,  
            student_id,
            school_id,
            register_id,
            full_name,
            payment_reference,
            generation,
            grade_group,
            enabled,
            lang,
            page,
            size,
            exportAll,
            order_by,
            order_dir
        );
    }

    public String createStudent(Long userSchoolId, String lang, Long responsible_user_id, CreateStudentRequest request) throws Exception {
        // Hash the password before storing it
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        request.setPassword(hashedPassword);
        
        // Call the repository method that converts the request to JSON and executes the stored procedure
        return studentRepository.createStudent(request, userSchoolId, lang, responsible_user_id);
    }

    public String updateStudent(Long userSchoolId, Long user_id, String lang, Long responsible_user_id, UpdateStudentRequest request) throws Exception {
        // Call the repository method that converts the request to JSON and executes the stored procedure
        return studentRepository.updateStudent(userSchoolId, user_id, lang, responsible_user_id, request);
    }

    public List<GetStudent> getStudent(Long token_user_id, Long user_id, String lang) {
        return studentRepository.getStudent(token_user_id, user_id, lang);
    }

    public GetStudentDetails getStudentDetails(Long token_user_id, Long student_id, String lang) {
        List<GetStudentDetails > rows = studentRepository.getStudentDetails(token_user_id, student_id, lang);
        return rows.stream().findFirst().orElse(null);
    }



    public List<ValidateStudentExist> validateStudentExists(Long token_user_id,String register_id,String payment_reference,String username) {
        // If tokenSchoolId is not null, the SP will filter users by school.
        return studentRepository.validateStudentExists(token_user_id, register_id, payment_reference, username);
    }

    public StudentCountsResponse getStudentsCountByScholarLevel(Long tokenUserId, String lang) throws Exception {
        return studentRepository.getStudentsCountByScholarLevel(tokenUserId, lang);
    }
}
