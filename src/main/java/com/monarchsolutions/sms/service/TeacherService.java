package com.monarchsolutions.sms.service;

import org.springframework.transaction.annotation.Transactional;

import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.dto.teachers.CreateTeacherRequest;
import com.monarchsolutions.sms.repository.TeacherRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class TeacherService {

@Autowired
private TeacherRepository teacherRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

@Transactional(readOnly = true)
public PageResult<Map<String,Object>> getTeacherList(
	Long token_user_id,
	Long user_id,
	Long school_id,
	String full_name,
	Boolean enabled,
	String lang,
	int page,
	int size,
	Boolean exportAll,
	String order_by,
	String order_dir) throws Exception {
		// If tokenSchoolId is not null, the SP will filter users by school.
		return teacherRepository.getTeacherList(
			token_user_id,
			user_id,
			school_id,
			full_name,
			enabled,
			lang,
			page,
			size,
			exportAll,
			order_by,
			order_dir
		);
	}
	public String createTeacher(Long userSchoolId, String lang, Long responsible_user_id, CreateTeacherRequest request) throws Exception {
		// Hash the password before storing it
		String hashedPassword = passwordEncoder.encode(request.getPassword());
		request.setPassword(hashedPassword);
		String jsonResponse = teacherRepository.createTeacher(request, userSchoolId, lang, responsible_user_id);

		// Call the repository method that converts the request to JSON and executes the stored procedure
		return jsonResponse;
	}
}
