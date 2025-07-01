package com.monarchsolutions.sms.repository;

import java.sql.*;
import java.util.*;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.monarchsolutions.sms.dto.common.PageResult;


@Repository
public class ClassRepository {

	@Autowired
	private DataSource dataSource;

  public PageResult<Map<String,Object>> getClasses(
		Long token_user_id,
    Long group_id,
    Long school_id,
    String generation,
    String grade_group,
    String scholar_level_name,
    Boolean enabled,
    String lang,
    int page,
    int size,
    Boolean exportAll,
    String order_by,
    String order_dir
	) throws SQLException {
		String call = "{CALL getClasses(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
		List<Map<String,Object>> content = new ArrayList<>();
		long totalCount = 0;

		try (Connection conn = dataSource.getConnection();
			CallableStatement stmt = conn.prepareCall(call)) {

			int idx = 1;
			// 1) the four IDs
			if (token_user_id != null) { stmt.setInt(idx++, token_user_id.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }
			if (group_id != null) { stmt.setInt(idx++, group_id.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }
			if (school_id != null) { stmt.setInt(idx++, school_id.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }

			// 2) the filters
			stmt.setString(idx++, generation);
			stmt.setString(idx++, grade_group);
			stmt.setString(idx++, scholar_level_name);
      
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
  
}
