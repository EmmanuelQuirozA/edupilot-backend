package com.monarchsolutions.sms.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.school.SchoolsList;
import com.monarchsolutions.sms.dto.school.CreateSchoolRequest;
import com.monarchsolutions.sms.dto.school.UpdateSchoolRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SchoolRepository {
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private ObjectMapper objectMapper;

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
