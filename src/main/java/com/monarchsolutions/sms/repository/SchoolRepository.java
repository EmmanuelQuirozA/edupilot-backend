package com.monarchsolutions.sms.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.school.SchoolsList;
import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.dto.school.CreateSchoolRequest;
import com.monarchsolutions.sms.dto.school.UpdateSchoolRequest;
import com.monarchsolutions.sms.dto.school.GetSchoolsResponse;
import com.monarchsolutions.sms.dto.school.SchoolSummary;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.hibernate.Session;

@Repository
public class SchoolRepository {
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private DataSource dataSource;
    
	public PageResult<Map<String,Object>> getSchools(
		Long tokenSchoolId,
		Long schoolId,
		String lang,
		Integer statusFilter,
		Integer page,
		Integer size,
		boolean exportAll,
		String order_by,
		String order_dir
	) throws SQLException {	
		String call = "{CALL getSchools(?,?,?,?,?,?,?,?,?)}";
		List<Map<String,Object>> content = new ArrayList<>();
		long totalCount = 0;

		try (Connection conn = dataSource.getConnection();
		CallableStatement stmt = conn.prepareCall(call)) {

			int idx = 1;
			// 1) the IDs
			if (tokenSchoolId != null) { stmt.setInt(idx++, tokenSchoolId.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }				
			if (schoolId != null) { stmt.setInt(idx++, schoolId.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }

			// 2) the filters
			stmt.setString(idx++, lang);

			if (statusFilter != null) {
					stmt.setInt(idx++, statusFilter);
			} else {
					stmt.setNull(idx++, Types.BOOLEAN);
			}

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

		// var response = new GetSchoolsResponse();
		// var schools  = new ArrayList<SchoolSummary>();
		// var total    = new AtomicInteger(0);

		// 	entityManager.unwrap(Session.class).doWork(connection -> {
		// 	try (CallableStatement stmt = connection.prepareCall("{CALL getSchools(?,?,?,?,?,?,?,?,?)}")) {
		// 		stmt.setObject(1, p_token_user_id, Types.INTEGER);
		// 		stmt.setObject(2, p_school_id, Types.INTEGER);
		// 		stmt.setString(3, lang);
		// 		stmt.setObject(4, p_status_filter, Types.INTEGER);
		// 		stmt.setObject(5, p_offset, Types.INTEGER);
		// 		stmt.setObject(6, p_limit, Types.INTEGER);
		// 		stmt.setBoolean(7, p_export_all);
		// 		stmt.setString(8, p_order_by);
		// 		stmt.setString(9, p_order_dir);

		// 		boolean hasResults = stmt.execute();

		// 		if (hasResults) {
		// 			try (ResultSet rs = stmt.getResultSet()) {
		// 				while (rs.next()) {
		// 					var school = new SchoolSummary();
		// 					school.setSchool_id(rs.getObject("school_id", Long.class));
		// 					school.setRelated_school_id(rs.getObject("related_school_id", Long.class));
		// 					school.setDescription(rs.getString("description"));
		// 					school.setCommercial_name(rs.getString("commercial_name"));
		// 					var enabled = rs.getObject("enabled");
		// 					school.setEnabled(enabled != null ? rs.getBoolean("enabled") : null);
		// 					school.setSchool_status(rs.getString("school_status"));
		// 					school.setImage(rs.getString("image"));
		// 					school.setPlan_name(rs.getString("plan_name"));
		// 					school.setIs_parent_school(rs.getInt("is_parent_school") == 1);
		// 					schools.add(school);
		// 				}
		// 			}
		// 		}

		// 		if (stmt.getMoreResults()) {
		// 			try (ResultSet countRs = stmt.getResultSet()) {
		// 				if (countRs.next()) {
		// 					total.set(countRs.getInt("total_count"));
		// 				}
		// 			}
		// 		}
		// 	}
		// });

		// response.setSchools(schools);
		// response.setTotal_count(total.get());
		// return response;
	}

	// Get Schools List
	public List<SchoolsList> getSchoolsList(Long token_user_id, Long school_id, String lang, int statusFilter) {
		// Create the stored procedure query
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getSchoolsList");

		// Register IN parameters
		query.registerStoredProcedureParameter("token_user_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("school_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("status_filter", Integer.class, ParameterMode.IN);

		// Set the parameter values
		query.setParameter("token_user_id", token_user_id);
		query.setParameter("school_id", school_id);
		query.setParameter("lang", lang);
		query.setParameter("status_filter", statusFilter);

		// Execute the stored procedure
		query.execute();

		// Retrieve the results as a list of Object arrays
		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();
		List<SchoolsList> schools = new ArrayList<>();

		for (Object[] data : results) {
			schools.add(mapSchool(data));
		}
		return schools;
	}
	//Map the School List
	private SchoolsList mapSchool(Object[] data) {
		SchoolsList school = new SchoolsList();
		
		school.setSchool_id(data[0] != null ? ((Number) data[0]).longValue() : null);
		school.setRelated_school_id(data[1] != null ? ((Number) data[1]).longValue() : null);
		school.setDescription(data[2] != null ? (String) data[2] : null);
		school.setDescription_en(data[3] != null ? (String) data[3] : null);
		school.setDescription_es(data[4] != null ? (String) data[4] : null);
		school.setCommercial_name(data[5] != null ? (String) data[5] : null);
		school.setBusiness_name(data[6] != null ? (String) data[6] : null);
		school.setTax_id(data[7] != null ? (String) data[7] : null);
		school.setAddress(data[8] != null ? (String) data[8] : null);
		school.setStreet(data[9] != null ? (String) data[9] : null);
		school.setExt_number(data[10] != null ? (String) data[10] : null);
		school.setInt_number(data[11] != null ? (String) data[11] : null);
		school.setSuburb(data[12] != null ? (String) data[12] : null);
		school.setLocality(data[13] != null ? (String) data[13] : null);
		school.setMunicipality(data[14] != null ? (String) data[14] : null);
		school.setState(data[15] != null ? (String) data[15] : null);
		school.setPhone_number(data[16] != null ? (String) data[16] : null);
		school.setEmail(data[17] != null ? (String) data[17] : null);
		school.setEnabled(data[18] != null ? (Boolean) data[18] : null);
		school.setDefault_tuition(data[19] != null ? (BigDecimal) data[19] : null);
		school.setSchool_status(data[20] != null ? (String) data[20] : null);
		school.setImage(data[21] != null ? (String) data[21] : null);
		
		
		return school;
	}

	// Get the Related Schools List
	public List<SchoolsList> getRelatedSchoolList(Long user_school_id, Long school_id, String lang) {
		// Create the stored procedure query
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getRelatedSchoolList");

		// Register IN parameters
		query.registerStoredProcedureParameter("user_school_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("school_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);

		// Set the parameter values
		query.setParameter("user_school_id", user_school_id);
		query.setParameter("school_id", school_id);
		query.setParameter("lang", lang);

		// Execute the stored procedure
		query.execute();

		// Retrieve the results as a list of Object arrays
		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();
		List<SchoolsList> schools = new ArrayList<>();

		for (Object[] data : results) {
			schools.add(mapRelatedSchool(data));
		}
		return schools;
	}
	// Map the Related School List
	private SchoolsList mapRelatedSchool(Object[] data) {
		SchoolsList school = new SchoolsList();
		
		school.setSchool_id(data[0] != null ? ((Number) data[0]).longValue() : null);
		school.setRelated_school_id(data[1] != null ? ((Number) data[1]).longValue() : null);
		school.setDescription(data[2] != null ? (String) data[2] : null);
		school.setCommercial_name(data[3] != null ? (String) data[3] : null);
		school.setBusiness_name(data[4] != null ? (String) data[4] : null);
		school.setTax_id(data[5] != null ? (String) data[5] : null);
		school.setAddress(data[6] != null ? (String) data[6] : null);
		school.setStreet(data[7] != null ? (String) data[7] : null);
		school.setExt_number(data[8] != null ? (String) data[8] : null);
		school.setInt_number(data[9] != null ? (String) data[9] : null);
		school.setSuburb(data[10] != null ? (String) data[10] : null);
		school.setLocality(data[11] != null ? (String) data[11] : null);
		school.setMunicipality(data[12] != null ? (String) data[12] : null);
		school.setState(data[13] != null ? (String) data[13] : null);
		school.setPhone_number(data[14] != null ? (String) data[14] : null);
		school.setEmail(data[15] != null ? (String) data[15] : null);
		school.setEnabled(data[16] != null ? (Boolean) data[16] : null);
		school.setSchool_status(data[17] != null ? (String) data[17] : null);
		
		return school;
	}
	
	// Create School
	public String createSchool(CreateSchoolRequest request) throws Exception {
		// Convert the request DTO to a JSON string
		String schoolDataJson = objectMapper.writeValueAsString(request);

		// Create the stored procedure query
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("createSchool");

		// Register the stored procedure parameters
		query.registerStoredProcedureParameter("school_data", String.class, ParameterMode.IN);

		// Set the parameters.
		query.setParameter("school_data", schoolDataJson);

		// Execute the stored procedure
		query.execute();

		// Retrieve the JSON result returned by the SP.
		Object result = query.getSingleResult();
		return result != null ? result.toString() : null;
	}

	// Update School
	public String updateSchool(Long p_token_user_id, Long schoolId, UpdateSchoolRequest request, String lang) throws Exception {
		// Convert the request DTO to a JSON string.
		String schoolDataJson = objectMapper.writeValueAsString(request);
		
		// Create the stored procedure query.
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("updateSchool");
		
		// Register the parameters as defined in your SP.
		query.registerStoredProcedureParameter("p_token_user_id", Integer.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("p_school_id", Integer.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("school_data", String.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
		
		// Set the parameters.
		query.setParameter("p_token_user_id", p_token_user_id);
		query.setParameter("p_school_id", schoolId.intValue());
		query.setParameter("school_data", schoolDataJson);
		query.setParameter("lang", lang);
		
		// Execute the stored procedure.
		query.execute();
		
		// Retrieve the JSON result returned by the SP.
		Object result = query.getSingleResult();
		return result != null ? result.toString() : null;
	}
	
	//Change School Status
	public String changeSchoolStatus(Long tokenSchoolId, Long school_id, String lang) {
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("changeSchoolStatus");
		query.registerStoredProcedureParameter("p_school_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);

		query.setParameter("p_school_id", school_id);
		query.setParameter("lang", lang);

		// If the user does not exist, the stored procedure will signal an error.
		query.execute();
		
		// Retrieve the JSON result returned by the SP.
		Object result = query.getSingleResult();
		return result != null ? result.toString() : null;
	}

	public String getSchoolImage(Long token_user_id, Long school_id) {
		var sql = """
			SELECT s.image
			FROM schools s
			LEFT JOIN users u
				ON u.user_id = :token_user_id
			WHERE 
				-- if the caller has no school, return all
				(u.school_id IS NULL
				-- otherwise only their school or any child schools
				OR s.school_id         = u.school_id
				OR s.related_school_id = u.school_id)
				AND s.school_id=:school_id;
		""";

		Object single = entityManager
		.createNativeQuery(sql)
		.setParameter("token_user_id", token_user_id)
		.setParameter("school_id", school_id)
		.getSingleResult();

		if (single == null) {
		return null;
		}
		// MySQL may return String
		if (single instanceof String n) {
		return n;
		}
		throw new IllegalStateException("Unexpected type for image: " + single.getClass());
	}

}
