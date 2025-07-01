package com.monarchsolutions.sms.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.dto.teachers.CreateTeacherRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import java.sql.*;
import java.util.*;

import javax.sql.DataSource;

@Repository
public class TeacherRepository {

  @Autowired
  private DataSource dataSource;

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private ObjectMapper objectMapper;
  
  // Get Users List
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
    String order_dir
  ) throws SQLException {
    String call = "{CALL getTeachersList(?,?,?,?,?,?,?,?,?,?,?)}";
    List<Map<String,Object>> content = new ArrayList<>();
    long totalCount = 0;

    try (Connection conn = dataSource.getConnection();
      CallableStatement stmt = conn.prepareCall(call)) {

      int idx = 1;
      // 1) the four IDs
      if (token_user_id != null) { stmt.setInt(idx++, token_user_id.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }
      if (user_id != null) { stmt.setInt(idx++, user_id.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }
      if (school_id != null) { stmt.setInt(idx++, school_id.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }

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
	public String createTeacher(CreateTeacherRequest request, Long userSchoolId, String lang, Long responsible_user_id) throws Exception {
		// Convert the request DTO to a JSON string
		String userDataJson = objectMapper.writeValueAsString(request);

		// Create the stored procedure query
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("createTeacher");

		// Register the stored procedure parameters
		query.registerStoredProcedureParameter("user_school_id", Integer.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("responsible_user_id", Integer.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("teacher_data", String.class, ParameterMode.IN);

		// Set the parameters. If user_school_id is null, it will be passed as null.
		query.setParameter("user_school_id", userSchoolId != null ? userSchoolId.intValue() : null);
		query.setParameter("lang", lang);
		query.setParameter("responsible_user_id", responsible_user_id);
		query.setParameter("teacher_data", userDataJson);

		// Execute the stored procedure
		query.execute();
		Object result = query.getSingleResult();
		return result != null ? result.toString() : null;
	}
}
