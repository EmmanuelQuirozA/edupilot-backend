package com.monarchsolutions.sms.repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.monarchsolutions.sms.dto.balance.CreateBalanceRechargeDTO;
import com.monarchsolutions.sms.dto.balance.AccountActivityRecord;
import com.monarchsolutions.sms.dto.common.PageResult;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

@Repository
public class BalanceRepository {

	@Autowired
	private DataSource dataSource;

  @PersistenceContext
  private EntityManager em;

  private final ObjectMapper objectMapper;
  public BalanceRepository(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * Calls CREATEBALANCERECHARGE(token_user_id, recharge_data JSON, lang)
   * and returns the JSON result string from the SP.
   */
  public String createBalanceRecharge(
      Long token_user_id,
      CreateBalanceRechargeDTO dto,
      String lang
  ) throws Exception {
    // Turn DTO into a JSON object node
    ObjectNode node = objectMapper.valueToTree(dto);

    // Overwrite the "ticket" field with 0/1 instead of true/false
    int ticketInt = Boolean.TRUE.equals(dto.getTicket()) ? 1 : 0;
    node.put("ticket", ticketInt);

    // Serialize that node to JSON
    String payload = objectMapper.writeValueAsString(node);

    StoredProcedureQuery q = em
      .createStoredProcedureQuery("createBalanceRecharge")
      .registerStoredProcedureParameter("token_user_id", Integer.class,    ParameterMode.IN)
      .registerStoredProcedureParameter("recharge_data",  String.class,     ParameterMode.IN)
      .registerStoredProcedureParameter("lang",           String.class,     ParameterMode.IN);

    q.setParameter("token_user_id", token_user_id.intValue());
    q.setParameter("recharge_data",  payload);
    q.setParameter("lang",           lang);

    q.execute();
    Object single = q.getSingleResult();
    if (single instanceof Object[]) {
      return (String) ((Object[]) single)[0];
    }
    return (String) single;
  }
  

  public PageResult<Map<String,Object>> getAccountActivity(
		Long token_user_id,
    Long user_id,
    String lang,
    int page,
    int size,
    Boolean exportAll,
    String order_by,
    String order_dir
	) throws SQLException {
		String call = "{CALL getAccountActivity(?,?,?,?,?,?,?,?)}";
		List<Map<String,Object>> content = new ArrayList<>();
		long totalCount = 0;

		try (Connection conn = dataSource.getConnection();
			CallableStatement stmt = conn.prepareCall(call)) {

			int idx = 1;
			// 1) the four IDs
			if (token_user_id != null) { stmt.setInt(idx++, token_user_id.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }
			if (user_id != null) { stmt.setInt(idx++, user_id.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }

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




  public List<AccountActivityRecord> getAccountActivityGroup(
		Long token_user_id,
		Long user_id,
		String lang
	) {
    // Create the stored procedure query
    StoredProcedureQuery q = em.createStoredProcedureQuery("getAccountActivity");
    // token, user, lang, offset=0, limit=0, exportAll=1, orderBy=null, orderDir=null

    // 1) register IN params
    q.registerStoredProcedureParameter("p_token_user_id",       Integer.class, ParameterMode.IN);
    q.registerStoredProcedureParameter("p_user_id",             Integer.class, ParameterMode.IN);
    q.registerStoredProcedureParameter("lang",                  String .class, ParameterMode.IN);
    q.registerStoredProcedureParameter("offset",                int    .class, ParameterMode.IN);
    q.registerStoredProcedureParameter("limit",                 int    .class, ParameterMode.IN);
    q.registerStoredProcedureParameter("exportAll",             Boolean.class, ParameterMode.IN);
    q.registerStoredProcedureParameter("orderBy",               String .class, ParameterMode.IN);
    q.registerStoredProcedureParameter("orderDir",              String .class, ParameterMode.IN);

    // 2) set parameter values
    q.setParameter("p_token_user_id",      token_user_id.intValue());
    q.setParameter("p_user_id",            user_id);
    q.setParameter("lang",                 lang);
    q.setParameter("offset",         0);
    q.setParameter("limit",          0);
    q.setParameter("exportAll",      true);
    q.setParameter("orderBy",        null);
    q.setParameter("orderDir",       null);

    // 3) execute and grab the result set
    q.execute();
    @SuppressWarnings("unchecked")
		List<Object[]> rows = q.getResultList();

    // 4) map each row to your DTO
    List<AccountActivityRecord> out = new ArrayList<>(rows.size());
    for (Object[] r : rows) {
      AccountActivityRecord dto = new AccountActivityRecord();
			dto.setConcept( r[0] != null ? r[0].toString() : null);
			dto.setSale( r[1] != null ? r[1].toString() : null);
			dto.setFullName( r[2] != null ? r[2].toString() : null);
			Object pcObj = r[3];
			if (pcObj instanceof java.sql.Timestamp) {
				dto.setCreatedAt(
					((java.sql.Timestamp) pcObj).toLocalDateTime()
				);
			} else {
				dto.setCreatedAt(null);
			}
			dto.setQuantity( r[4] != null ? ((Number) r[4]).intValue() : null);
			dto.setUnitPrice( r[5] != null ? (BigDecimal) r[5] : null);
			dto.setAmount( r[6] != null ? (BigDecimal) r[6] : null);
      out.add(dto);
    }
    return out;
  }
  
}
