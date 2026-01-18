package com.monarchsolutions.sms.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.student.UpdateStudentRequest;
import com.monarchsolutions.sms.dto.student.ValidateStudentExist;
import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.dto.student.CreateStudentRequest;
import com.monarchsolutions.sms.dto.student.GetStudent;
import com.monarchsolutions.sms.dto.student.GetStudentDetails;
import com.monarchsolutions.sms.dto.student.StudentCountsResponse;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class StudentRepository {

	@Autowired
	private DataSource dataSource;
    
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private ObjectMapper objectMapper;


	// Get Students List
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
	) throws SQLException {
		String call = "{CALL getStudentsList(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
		List<Map<String,Object>> content = new ArrayList<>();
		long totalCount = 0;

		try (Connection conn = dataSource.getConnection();
		CallableStatement stmt = conn.prepareCall(call)) {

			int idx = 1;
			// 1) the IDs
			if (tokenSchoolId != null) { stmt.setInt(idx++, tokenSchoolId.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }				
			if (student_id != null) { stmt.setInt(idx++, student_id.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }
			if (school_id != null) { stmt.setInt(idx++, school_id.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }

			// 2) the filters
			stmt.setString(idx++, register_id);
			stmt.setString(idx++, full_name);
			stmt.setString(idx++, payment_reference);
			stmt.setString(idx++, generation);
			stmt.setString(idx++, grade_group);
			if (enabled != null) {
					stmt.setBoolean(idx++, enabled);
			} else {
					stmt.setNull(idx++, Types.BOOLEAN);
			}

			stmt.setString(idx++, lang);

			int offsetParam = page;     // rename 'page' var to 'offsetParam'
			int limitParam  = size;     // rename 'size' var to 'limitParam'
			// 15. offset
			if (exportAll) {
				stmt.setNull(idx++, Types.INTEGER);
				stmt.setNull(idx++, Types.INTEGER);
			} else {
				stmt.setInt(idx++, offsetParam);
				stmt.setInt(idx++, limitParam);
			}
			// 17. export_all
			stmt.setBoolean(idx++, exportAll);
			stmt.setString(idx++, order_by);
			stmt.setString(idx++, order_dir);
			
			// -- execute & read page result --
			boolean hasRs = stmt.execute();
			if (hasRs) {
				try (ResultSet rs = stmt.getResultSet()) {
					ResultSetMetaData md = rs.getMetaData();
					int cols = md.getColumnCount();
					while (rs.next()) {
						Map<String,Object> row = new LinkedHashMap<>();
						for (int c = 1; c <= cols; c++) {
							row.put(md.getColumnLabel(c), rs.getObject(c));
						}
						content.add(row);
					}
				}
			}

			// -- advance to the second resultset: total count --
			if (stmt.getMoreResults()) {
				try (ResultSet rs2 = stmt.getResultSet()) {
					if (rs2.next()) {
						totalCount = rs2.getLong(1);
					}
				}
			}
		}

		return new PageResult<>(content, totalCount, page, size);
	}
	
	public String updateStudent(Long userSchoolId, Long user_id, String lang, Long responsible_user_id, UpdateStudentRequest request) throws Exception {
			// Convert the request DTO to a JSON string
			String studentDataJson = objectMapper.writeValueAsString(request);

			// Create the stored procedure query
			StoredProcedureQuery query = entityManager.createStoredProcedureQuery("updateStudent");

			// Register the stored procedure parameters
			query.registerStoredProcedureParameter("user_school_id", Integer.class, ParameterMode.IN);
			query.registerStoredProcedureParameter("p_user_id", Long.class, ParameterMode.IN);
			query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
			query.registerStoredProcedureParameter("responsible_user_id", Integer.class, ParameterMode.IN);
			query.registerStoredProcedureParameter("student_data", String.class, ParameterMode.IN);
			
			// Set the parameters. If userSchoolId is null, it will be passed as null.
			query.setParameter("user_school_id", userSchoolId != null ? userSchoolId.intValue() : null);
			query.setParameter("p_user_id", user_id);
			query.setParameter("lang", lang);
			query.setParameter("responsible_user_id", responsible_user_id);
			query.setParameter("student_data", studentDataJson);

			query.execute();
			Object result = query.getSingleResult();
			return result != null ? result.toString() : null;
	}

	// Create Student
	public String createStudent(CreateStudentRequest request, Long userSchoolId, String lang, Long responsible_user_id) throws Exception {
			// Convert the request DTO to a JSON string
			String studentDataJson = objectMapper.writeValueAsString(request);

			// Create the stored procedure query
			StoredProcedureQuery query = entityManager.createStoredProcedureQuery("createStudent");

			// Register the stored procedure parameters
			query.registerStoredProcedureParameter("user_school_id", Integer.class, ParameterMode.IN);
			query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
			query.registerStoredProcedureParameter("responsible_user_id", Integer.class, ParameterMode.IN);
			query.registerStoredProcedureParameter("user_data", String.class, ParameterMode.IN);

			// Set the parameters. If user_school_id is null, it will be passed as null.
			query.setParameter("user_school_id", userSchoolId != null ? userSchoolId.intValue() : null);
			query.setParameter("lang", lang);
			query.setParameter("responsible_user_id", responsible_user_id);
			query.setParameter("user_data", studentDataJson);

			// Execute the stored procedure
			query.execute();
			Object result = query.getSingleResult();
			return result != null ? result.toString() : null;
	}




	// Get Student Details List
	public List<GetStudent> getStudent(Long token_user_id, Long student_id, String lang){
		// Create the stored procedure query
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getStudent");

		// Register IN parameters
		query.registerStoredProcedureParameter("user_school_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("student_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);

		// Set the parameter values
		query.setParameter("user_school_id", token_user_id);
		query.setParameter("student_id", student_id);
		query.setParameter("lang", lang);

		// Execute the stored procedure
		query.execute();

		// Retrieve the results as a list of Object arrays
		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();
		List<GetStudent> student = new ArrayList<>();

		for (Object[] data : results) {
				student.add(mapStudentRequests(data));
		}
		return student;
	}

    private GetStudent mapStudentRequests(Object[] data) {
		MappingConfig[] config = new MappingConfig[] {
			new MappingConfig("student_id", Long.class),
			new MappingConfig("group_id", Long.class),
			new MappingConfig("register_id", String.class),
			new MappingConfig("payment_reference", String.class),
			new MappingConfig("user_id", Long.class),
			new MappingConfig("school_id", Long.class),
			new MappingConfig("email", String.class),
			new MappingConfig("username", String.class),
			new MappingConfig("role_name", String.class),
			new MappingConfig("full_name", String.class),
			new MappingConfig("address", String.class),
			new MappingConfig("commercial_name", String.class),
			new MappingConfig("business_name", String.class),
			new MappingConfig("group_name", String.class),
			new MappingConfig("generation", String.class),
			new MappingConfig("grade_group", String.class),
			new MappingConfig("grade", String.class),
			new MappingConfig("group", String.class),
			new MappingConfig("scholar_level_id", Long.class),
			new MappingConfig("scholar_level_name", String.class),
			new MappingConfig("first_name", String.class),
			new MappingConfig("last_name_father", String.class),
			new MappingConfig("last_name_mother", String.class),
			new MappingConfig("birth_date", Date.class),
			new MappingConfig("phone_number", String.class),
			new MappingConfig("tax_id", String.class),
			new MappingConfig("street", String.class),
			new MappingConfig("ext_number", String.class),
			new MappingConfig("int_number", String.class),
			new MappingConfig("suburb", String.class),
			new MappingConfig("locality", String.class),
			new MappingConfig("municipality", String.class),
			new MappingConfig("state", String.class),
			new MappingConfig("personal_email", String.class),
			new MappingConfig("user_enabled", Boolean.class),
			new MappingConfig("role_enabled", Boolean.class),
			new MappingConfig("school_enabled", Boolean.class),
			new MappingConfig("group_enabled", Boolean.class),
			new MappingConfig("user_status", String.class),
			new MappingConfig("role_status", String.class),
			new MappingConfig("school_status", String.class),
			new MappingConfig("group_status", String.class),
			new MappingConfig("balance", BigDecimal.class),
			new MappingConfig("joining_date", Date.class),
			new MappingConfig("tuition", BigDecimal.class),
			new MappingConfig("default_tuition", BigDecimal.class),
		};

		return MapperUtil.mapRow(data, config, GetStudent.class);
	}

	// Get Student Details List Read Only prepared
	public List<GetStudentDetails> getStudentDetails(Long token_user_id, Long student_id, String lang){
		// Create the stored procedure query
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getStudentDetails");

		// Register IN parameters
		query.registerStoredProcedureParameter("user_school_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("student_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);

		// Set the parameter values
		query.setParameter("user_school_id", token_user_id);
		query.setParameter("student_id", student_id);
		query.setParameter("lang", lang);

		// Execute the stored procedure
		query.execute();

		// Retrieve the results as a list of Object arrays
		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();
		List<GetStudentDetails> student = new ArrayList<>();

		for (Object[] data : results) {
				student.add(mapStudent(data));
		}
		return student;
	}

	private GetStudentDetails mapStudent(Object[] data) {
		MappingConfig[] config = new MappingConfig[] {
			new MappingConfig("studentId", Long.class),
			new MappingConfig("userId", Long.class),
			new MappingConfig("registerId", String.class),
			new MappingConfig("paymentReference", String.class),
			new MappingConfig("email", String.class),
			new MappingConfig("username", String.class),
			new MappingConfig("fullName", String.class),
			new MappingConfig("address", String.class),
			new MappingConfig("commercialName", String.class),
			new MappingConfig("groupName", String.class),
			new MappingConfig("generation", String.class),
			new MappingConfig("gradeGroup", String.class),
			new MappingConfig("scholarLevelName", String.class),
			new MappingConfig("birthDate", LocalDate.class),
			new MappingConfig("phoneNumber", String.class),
			new MappingConfig("taxId", String.class),
			new MappingConfig("personalEmail", String.class),
			new MappingConfig("userEnabled", Boolean.class),
			new MappingConfig("roleEnabled", Boolean.class),
			new MappingConfig("schoolEnabled", Boolean.class),
			new MappingConfig("groupEnabled", Boolean.class),
			new MappingConfig("userStatus", String.class),
			new MappingConfig("roleStatus", String.class),
			new MappingConfig("schoolStatus", String.class),
			new MappingConfig("groupStatus", String.class),
			new MappingConfig("joiningDate", LocalDate.class),
			new MappingConfig("tuition", BigDecimal.class),
			new MappingConfig("defaultTuition", BigDecimal.class),
			new MappingConfig("balance", BigDecimal.class)
		};

		return MapperUtil.mapRow(data, config, GetStudentDetails.class);
	}

	public StudentCountsResponse getStudentsCountByScholarLevel(Long tokenUserId, String lang) throws Exception {
		String sql = """
			/* =========================================================
			   COUNT de alumnos accesibles:
			   1) total_students
			   2) by_scholar_level (array)
			========================================================= */
			SELECT JSON_OBJECT(
			  'total_students', totals.total_students,
			  'by_scholar_level', IFNULL(levels.by_scholar_level, JSON_ARRAY())
			) AS result
			FROM
			(
			  /* ===== TOTAL ===== */
			  SELECT COUNT(*) AS total_students
			  FROM students st
			  JOIN users u_st   ON st.user_id = u_st.user_id
			  JOIN `groups` g   ON st.group_id = g.group_id
			  JOIN schools sc   ON u_st.school_id = sc.school_id
			  JOIN users u_call ON u_call.user_id = :token_user_id
			  WHERE u_st.enabled = 1
			    AND (
			      u_call.school_id IS NULL
			      OR sc.school_id = u_call.school_id
			      OR sc.related_school_id = u_call.school_id
			    )
			) totals
			CROSS JOIN
			(
			  /* ===== POR NIVEL ESCOLAR ===== */
			  SELECT JSON_ARRAYAGG(
			           JSON_OBJECT(
			             'scholar_level_id', x.scholar_level_id,
			             'scholar_level_name',
			                CASE WHEN :lang = 'en' THEN x.name_en ELSE x.name_es END,
			             'student_count', x.student_count
			           )
			         ) AS by_scholar_level
			  FROM (
			    SELECT
			      sl.scholar_level_id,
			      sl.name_en,
			      sl.name_es,
			      COUNT(*) AS student_count
			    FROM students st
			    JOIN users u_st   ON st.user_id = u_st.user_id
			    JOIN `groups` g   ON st.group_id = g.group_id
			    JOIN scholar_levels sl ON g.scholar_level_id = sl.scholar_level_id
			    JOIN schools sc   ON u_st.school_id = sc.school_id
			    JOIN users u_call ON u_call.user_id = :token_user_id
			    WHERE u_st.enabled = 1
			      AND (
			        u_call.school_id IS NULL
			        OR sc.school_id = u_call.school_id
			        OR sc.related_school_id = u_call.school_id
			      )
			    GROUP BY sl.scholar_level_id, sl.name_en, sl.name_es
			    ORDER BY sl.scholar_level_id ASC
			  ) x
			) levels;
		""";

		Query q = entityManager.createNativeQuery(sql);
		q.setParameter("token_user_id", tokenUserId);
		q.setParameter("lang", lang);

		Object result = q.getSingleResult();
		if (result == null) {
			return null;
		}
		return objectMapper.readValue(result.toString(), StudentCountsResponse.class);
	}

	public List<ValidateStudentExist>  validateStudentExists(Long token_user_id,String register_id,String payment_reference,String username) {
	String sql = """
		SELECT
			/* 1) Does any student exist with that register_id in the caller's school? */
			EXISTS(
				SELECT 1
					FROM students st
					JOIN users u ON st.user_id = u.user_id
					LEFT JOIN schools sc ON u.school_id = sc.school_id
				WHERE st.register_id = :register_id
					AND (
								sc.school_id         = uc.school_id
						OR sc.related_school_id = uc.school_id
					)
			) AS register_id,

			/* 2) Same, but for payment_reference */
			EXISTS(
				SELECT 1
					FROM students st
					JOIN users u ON st.user_id = u.user_id
					LEFT JOIN schools sc ON u.school_id = sc.school_id
				WHERE st.payment_reference = :payment_reference
					AND (
								sc.school_id         = uc.school_id
						OR sc.related_school_id = uc.school_id
					)
			) AS payment_reference,

			/* 3) And for username (users table only) */
			EXISTS(
				SELECT 1
					FROM users u
					LEFT JOIN schools sc ON u.school_id = sc.school_id
				WHERE u.username = :username
					AND (
								sc.school_id         = uc.school_id
						OR sc.related_school_id = uc.school_id
					)
			) AS username

		FROM users uc
		WHERE uc.user_id = :token_user_id
		;
	""";

	Query q = entityManager.createNativeQuery(sql);
	// bind parameters _without_ the leading colon:
	q.setParameter("token_user_id",	token_user_id);
	q.setParameter("register_id",	register_id);
	q.setParameter("payment_reference",	payment_reference);
	q.setParameter("username",	username);

	@SuppressWarnings("unchecked")
	List<Object[]> rows = q.getResultList();

	List<ValidateStudentExist> list = new ArrayList<>(rows.size());
	for (Object[] r : rows) {
		ValidateStudentExist dto = new ValidateStudentExist();
		dto.setRegister_id((Long) r[0]);
		dto.setPayment_reference((Long) r[1]);
		dto.setUsername((Long) r[2]);
		list.add(dto);
	}
	return list;
}

}
