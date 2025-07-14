package com.monarchsolutions.sms.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.dto.common.UserLoginDTO;
import com.monarchsolutions.sms.dto.user.UserDetails;
import com.monarchsolutions.sms.dto.user.UserDetailsCache;
import com.monarchsolutions.sms.dto.user.UserListDTO;
import com.monarchsolutions.sms.dto.user.UsersBalanceDTO;
import com.monarchsolutions.sms.dto.user.CreateUserRequest;
import com.monarchsolutions.sms.dto.user.UpdateUserRequest;
import com.monarchsolutions.sms.dto.user.UserBalanceDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

	@Autowired
	private DataSource dataSource;

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private ObjectMapper objectMapper;

	// Get User by username for login
	public UserLoginDTO getUserByUsernameOrEmail(String usernameOrEmail, String lang) {
		// Create the stored procedure query (the procedure name must match exactly)
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getUserByUsernameOrEmail");
		
		// Register the IN parameters
		query.registerStoredProcedureParameter("p_username_or_email", String.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
		
		// Set the parameters
		query.setParameter("p_username_or_email", usernameOrEmail);
		query.setParameter("lang", lang);

		// Execute the stored procedure
		query.execute();
		
		// Get the single result.
		Object result = query.getSingleResult();
		if (result == null) {
			return null;
		}
		
		// The SP returns a row with multiple columns.
		// Cast the result to Object[]
		Object[] data = (Object[]) result;
		UserLoginDTO user = new UserLoginDTO();
		// Mapping

		user.setUserId(data[0] != null ? ((Number) data[0]).longValue() : null);
		user.setSchoolId(data[1] != null ? ((Number) data[1]).longValue() : null);
		user.setRoleId(data[2] != null ? ((Number) data[2]).longValue() : null);
		user.setEmail(data[3] != null ? (String) data[3] : null);
		user.setUsername(data[4] != null ? (String) data[4] : null);
		user.setPassword(data[5] != null ? (String) data[5] : null);
		user.setRoleName(data[6] != null ? (String) data[6] : null);
		user.setFullName(data[7] != null ? (String) data[7] : null);
		user.setAddress(data[8] != null ? (String) data[8] : null);
		user.setCommercialName(data[9] != null ? (String) data[9] : null);
		user.setBusinessName(data[10] != null ? (String) data[10] : null);
		user.setPersonalEmail(data[11] != null ? (String) data[11] : null);
		user.setEnabledUser(data[12] != null ? (Boolean) data[12] : null);
		user.setEnabledRole(data[13] != null ? (Boolean) data[13] : null);
		user.setEnabledSchool(data[14] != null ? (Boolean) data[14] : null);
		user.setUsernameOrEmail(data[15] != null ? (String) data[15] : null);
		
		return user;
	}
	
	// Get Users List
	public PageResult<Map<String,Object>> getUsersList(
		Long token_user_id,
		Long user_id,
		Long school_id,
		Long role_id,
		String full_name,
		Boolean enabled,
		String lang,
		int page,
		int size,
		Boolean exportAll,
		String order_by,
		String order_dir
	) throws SQLException {
		String call = "{CALL getUsersList(?,?,?,?,?,?,?,?,?,?,?,?)}";
		List<Map<String,Object>> content = new ArrayList<>();
		long totalCount = 0;

		try (Connection conn = dataSource.getConnection();
			CallableStatement stmt = conn.prepareCall(call)) {

			int idx = 1;
			// 1) the four IDs
			if (token_user_id != null) { stmt.setInt(idx++, token_user_id.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }
			if (user_id != null) { stmt.setInt(idx++, user_id.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }
			if (school_id != null) { stmt.setInt(idx++, school_id.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }
			if (role_id != null) { stmt.setInt(idx++, role_id.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }

			// 2) the filters
			stmt.setString(idx++, full_name);
			
			if (enabled != null) {
				stmt.setBoolean(idx++, enabled);
			} else {
				// pass a proper SQL NULL
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
			Boolean hasRs = stmt.execute();
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
	
	// Create User
	public String createUser(CreateUserRequest request, Long userSchoolId, String lang, Long responsible_user_id) throws Exception {
		// Convert the request DTO to a JSON string
		String userDataJson = objectMapper.writeValueAsString(request);

		// Create the stored procedure query
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("createUser");

		// Register the stored procedure parameters
		query.registerStoredProcedureParameter("user_school_id", Integer.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("responsible_user_id", Integer.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("user_data", String.class, ParameterMode.IN);

		// Set the parameters. If user_school_id is null, it will be passed as null.
		query.setParameter("user_school_id", userSchoolId != null ? userSchoolId.intValue() : null);
		query.setParameter("lang", lang);
		query.setParameter("responsible_user_id", responsible_user_id);
		query.setParameter("user_data", userDataJson);

		// Execute the stored procedure
		query.execute();
		Object result = query.getSingleResult();
		return result != null ? result.toString() : null;
	}
	
	public String updateUser(UpdateUserRequest request, Long userSchoolId, Long user_id, String lang, Long responsible_user_id) throws Exception {
		// Convert the request DTO to a JSON string
		String userDataJson = objectMapper.writeValueAsString(request);
		
		// Create the stored procedure query
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("updateUser");
		
		// Register the stored procedure parameters
		query.registerStoredProcedureParameter("user_school_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("p_user_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("responsible_user_id", Integer.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("user_data", String.class, ParameterMode.IN);
		
		// Set the parameters. If userSchoolId is null, it will be passed as null.
		query.setParameter("user_school_id", userSchoolId != null ? userSchoolId.intValue() : null);
		query.setParameter("p_user_id", user_id);
		query.setParameter("lang", lang);
		query.setParameter("responsible_user_id", responsible_user_id);
		query.setParameter("user_data", userDataJson);

		query.execute();
		Object result = query.getSingleResult();
		return result != null ? result.toString() : null;
	}
	
	public String changeUserStatus(Integer userId, String lang, Long tokenSchoolId, Long responsible_user_id) throws Exception {
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("changeUserStatus");
		query.registerStoredProcedureParameter("user_school_id", Integer.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("p_user_id", Integer.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("responsible_user_id", Integer.class, ParameterMode.IN);
		
		query.setParameter("user_school_id", tokenSchoolId != null ? tokenSchoolId.intValue() : null);
		query.setParameter("p_user_id", userId);
		query.setParameter("lang", lang);
		query.setParameter("responsible_user_id", responsible_user_id);
		
		query.execute();
		Object result = query.getSingleResult();
		return result != null ? result.toString() : null;
	}

	public List<UserDetails> getUser(Long token_user_id, Long userId, String lang){
		// Create the stored procedure query
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getUser");

		// Register IN parameters
		query.registerStoredProcedureParameter("token_user_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("userId", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);

		// Set the parameter values
		query.setParameter("token_user_id", token_user_id);
		query.setParameter("userId", userId);
		query.setParameter("lang", lang);

		// Execute the stored procedure
		query.execute();

		// Retrieve the results as a list of Object arrays
		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();
		List<UserDetails> student = new ArrayList<>();

		for (Object[] data : results) {
				student.add(mapUser(data));
		}
		return student;
	}

	private UserDetails mapUser(Object[] data) {
		MappingConfig[] config = new MappingConfig[] {
			new MappingConfig("user_id", Long.class),
			new MappingConfig("person_id", Long.class),
			new MappingConfig("school_id", Long.class),
			new MappingConfig("role_id", Long.class),
			new MappingConfig("email", String.class),
			new MappingConfig("username", String.class),
			new MappingConfig("role_name", String.class),
			new MappingConfig("full_name", String.class),
			new MappingConfig("address", String.class),
			new MappingConfig("commercial_name", String.class),
			new MappingConfig("business_name", String.class),
			new MappingConfig("first_name", String.class),
			new MappingConfig("last_name_father", String.class),
			new MappingConfig("last_name_mother", String.class),
			new MappingConfig("birth_date", LocalDate.class),
			new MappingConfig("phone_number", String.class),
			new MappingConfig("tax_id", String.class),
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
			new MappingConfig("user_status", String.class),
			new MappingConfig("role_status", String.class),
			new MappingConfig("school_status", String.class),
			new MappingConfig("balance", BigDecimal.class),
		};
		return MapperUtil.mapRow(data, config, UserDetails.class);
	}

	public List<UserDetailsCache> getUserBasic(Long token_user_id, Long userId, String lang) {
    StoredProcedureQuery query = entityManager
      .createStoredProcedureQuery("getUser");
    query.registerStoredProcedureParameter("token_user_id", Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("userId",         Long.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("lang",           String.class, ParameterMode.IN);

    query.setParameter("token_user_id", token_user_id);
    query.setParameter("userId",         userId);
    query.setParameter("lang",           lang);

    query.execute();

    @SuppressWarnings("unchecked")
    List<Object[]> rows = query.getResultList();
    List<UserDetailsCache> out = new ArrayList<>(rows.size());

    for (Object[] r : rows) {
      UserDetailsCache u = new UserDetailsCache();
      // [0] user_id
      u.setUser_id(((Number) r[0]).longValue());
      // [2] school_id
      u.setSchool_id(((Number) r[2]).longValue());
      // [4] email
      u.setEmail((String) r[4]);
      // [5] username
      u.setUsername((String) r[5]);
      // [6] role_name
			u.setRole_name((String) r[6]);
      // [7] full_name
      u.setFull_name((String) r[7]);
      // [11] first_name
			u.setFirst_name((String) r[11]);
      // [14] birthday
			Object raw = r[14];
			if (raw instanceof java.sql.Date sqlDate) {
					u.setBirth_date(sqlDate.toLocalDate());
			} else if (raw instanceof java.sql.Timestamp ts) {
					u.setBirth_date(ts.toLocalDateTime().toLocalDate());
			} else {
					u.setBirth_date(null);
			}

			// u.setUser_id(((Number) r[0]).longValue());
			// u.setSchool_id(((Number) r[1]).longValue());
			// u.setEmail((String) r[2]);
			// u.setUsername((String) r[3]);
			// u.setRole_name((String) r[4]);
			// u.setFull_name((String) r[5]);
      out.add(u);
    }
    return out;
	}

	public List<UserBalanceDTO> findActiveUserBalancesBySchoolId(Long schoolId, String search_criteria) {
    String sql = """
      SELECT 
        u.user_id,
        CONCAT(p.first_name,' ',p.last_name_father,' ',p.last_name_mother) AS full_name,
        p.balance
      FROM users u
      JOIN persons p ON u.person_id = p.person_id
      WHERE u.school_id = :schoolId
        AND u.enabled = 1
      	AND CONCAT(p.first_name,' ',p.last_name_father,' ',p.last_name_mother)
          LIKE :search
			LIMIT 6
      """;

    Query q = entityManager.createNativeQuery(sql)
      .setParameter("schoolId", schoolId)
      .setParameter("search",
          "%" + (search_criteria == null ? "" : search_criteria.trim()) + "%");

    @SuppressWarnings("unchecked")
    List<Object[]> rows = q.getResultList();
    List<UserBalanceDTO> out = new ArrayList<>(rows.size());
    for (Object[] r : rows) {
      Long       userId   = ((Number) r[0]).longValue();
      String     fullName = (String)   r[1];
      BigDecimal balance  = (BigDecimal) r[2];
      out.add(new UserBalanceDTO(userId, fullName, balance));
    }
    return out;
  }

	public List<UsersBalanceDTO>  getUsersBalance(Long token_user_id, String full_name, String lang) {
		String sql = """
			SELECT
				u.user_id,
				CONCAT(p.first_name, ' ', p.last_name_father, ' ', p.last_name_mother) AS full_name,
				CASE WHEN :lang = 'en' THEN r.name_en ELSE r.name_es END AS role_name,
				g.generation,
				CASE WHEN :lang = 'en' THEN sl.name_en ELSE sl.name_es END AS scholar_level_name,
				CONCAT(g.grade,'-',g.`group`) AS grade_group,
				p.balance
			FROM users u

				/* 1) bring in the caller so we know their school */
				JOIN users uc
					ON uc.user_id = :token_user_id

				JOIN persons        p  ON u.person_id = p.person_id
				JOIN roles          r  ON u.role_id   = r.role_id
				LEFT JOIN students       s  ON u.user_id   = s.user_id
				LEFT JOIN schools        sc ON u.school_id = sc.school_id
				LEFT JOIN `groups`       g  ON s.group_id  = g.group_id
				LEFT JOIN scholar_levels sl ON g.scholar_level_id = sl.scholar_level_id

			WHERE u.enabled = 1
				AND CONCAT(p.first_name, ' ', p.last_name_father, ' ', p.last_name_mother)
						LIKE CONCAT('%', :full_name, '%')

				/* 2) only users in the same or a related school as the caller */
				AND (
						sc.school_id         = uc.school_id
					OR sc.related_school_id = uc.school_id
				);
		""";

		Query q = entityManager.createNativeQuery(sql);
		// bind parameters _without_ the leading colon:
		q.setParameter("token_user_id",	token_user_id);
		q.setParameter("lang",      			lang);
		q.setParameter("full_name", 			full_name == null ? "" : full_name.trim());

    @SuppressWarnings("unchecked")
    List<Object[]> rows = q.getResultList();

    List<UsersBalanceDTO> list = new ArrayList<>(rows.size());
    for (Object[] r : rows) {
      UsersBalanceDTO dto = new UsersBalanceDTO();
      dto.setUser_id( ((Number) r[0]).longValue());
      dto.setFull_name((String) r[1]);
      dto.setRole_name((String) r[2]);
      dto.setGeneration(((String) r[3]));
      dto.setScholar_level_name((String) r[4]);
      dto.setGrade_group((String) r[5]);
      dto.setBalance((BigDecimal) r[6]);
      list.add(dto);
    }
    return list;
	}
}
