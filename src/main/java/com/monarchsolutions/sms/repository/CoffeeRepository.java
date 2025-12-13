package com.monarchsolutions.sms.repository;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monarchsolutions.sms.dto.coffee.CoffeeMenuDTO;
import com.monarchsolutions.sms.dto.coffee.CreateCoffeeMenuDTO;
import com.monarchsolutions.sms.dto.coffee.ProcessCoffeeSaleDTO;
import com.monarchsolutions.sms.dto.coffee.UpdateCoffeeMenuDTO;
import com.monarchsolutions.sms.dto.coffee.UserPurchasesDTO;
import com.monarchsolutions.sms.dto.common.PageResult;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;

@Repository
public class CoffeeRepository {
    
  @PersistenceContext
  private EntityManager em;

	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private ObjectMapper objectMapper;

  /**
   * Returns the last 7 purchase rows for the given user,
   * each row with coffee_sale_id, sale, item_name, created_at,
   * quantity, unit_price and line total.
   */
  public List<UserPurchasesDTO> getUserCoffeePurchases(Long userId, String lang) {
    String sql = """
      SELECT
        cs.coffee_sale_id,
        cs.sale,
        CASE WHEN :lang = 'es' THEN cm.name_es ELSE cm.name_en END AS item_name,
        cs.created_at,
        cs.quantity,
        cs.unit_price,
        cs.quantity * cs.unit_price AS total
      FROM coffee_sales cs
      JOIN coffee_menu  cm ON cs.menu_id = cm.menu_id
      WHERE cs.user_id = :userId
      ORDER BY cs.created_at DESC
      LIMIT 7
      """;

    Query q = em.createNativeQuery(sql);
    q.setParameter("lang",   lang);
    q.setParameter("userId", userId);

    @SuppressWarnings("unchecked")
    List<Object[]> rows = q.getResultList();

    List<UserPurchasesDTO> list = new ArrayList<>(rows.size());
    for (Object[] r : rows) {
      UserPurchasesDTO dto = new UserPurchasesDTO();
      dto.setCoffee_sale_id( ((Number) r[0]).longValue());
      dto.setSale((Number) r[1]);
      dto.setItem_name((String) r[2]);
      dto.setCreated_at(((java.sql.Timestamp) r[3]).toLocalDateTime());
      dto.setQuantity((Number) r[4]);
      dto.setUnit_price((BigDecimal) r[5]);
      dto.setTotal((BigDecimal) r[6]);
      list.add(dto);
    }
    return list;
  }

  public PageResult<Map<String,Object>> getCoffeeSales(
		Long token_user_id,
    Long user_id,
    Long school_id,
    String full_name,
    String item_name,
    LocalDate created_at,
    String lang,
    int page,
    int size,
    Boolean exportAll,
    String order_by,
    String order_dir
	) throws SQLException {
		String call = "{CALL getCoffeeSales(?,?,?,?,?,?,?,?,?,?,?,?)}";
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
			stmt.setString(idx++, item_name);
      // stmt.setDate(idx++, java.sql.Date.valueOf(created_at));
      if (created_at != null) {
          stmt.setDate(idx++, java.sql.Date.valueOf(created_at));
      } else {
          stmt.setNull(idx++, Types.DATE);
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

  public PageResult<Map<String,Object>> getCoffeeMenu(
		Long token_school_id,
    Long menu_id,
    String search_criteria,
    Boolean enabled,
    String lang,
    int page,
    int size,
    Boolean exportAll,
    String order_by,
    String order_dir
	) throws SQLException {
		String call = "{CALL getCoffeeMenu(?,?,?,?,?,?,?,?,?,?)}";
		List<Map<String,Object>> content = new ArrayList<>();
		long totalCount = 0;

		try (Connection conn = dataSource.getConnection();
			CallableStatement stmt = conn.prepareCall(call)) {

			int idx = 1;
			// 1) the four IDs
			if (token_school_id != null) { stmt.setInt(idx++, token_school_id.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }
			if (menu_id != null) { stmt.setInt(idx++, menu_id.intValue()); } else { stmt.setNull(idx++, Types.INTEGER); }

			// 2) the filters
			stmt.setString(idx++, search_criteria);
      
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
  
  public String updateCoffeeMenu(
    Long token_user_id, 
    Long menu_id, 
    String lang, 
    UpdateCoffeeMenuDTO request,
    boolean removeImage
	) throws Exception {
		// Convert the request DTO to a JSON string
		String dataJson = objectMapper.writeValueAsString(request);
		// System.out.println(dataJson);
		
		// Create the stored procedure query
		StoredProcedureQuery query = em.createStoredProcedureQuery("updateCoffeeMenu");
		
		// Register the stored procedure parameters
		query.registerStoredProcedureParameter("token_user_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("p_menu_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
    query.registerStoredProcedureParameter("removeImage", Boolean.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("menu_data", String.class, ParameterMode.IN);
		
		query.setParameter("token_user_id", token_user_id);
		query.setParameter("p_menu_id", menu_id);
		query.setParameter("lang", lang);
		query.setParameter("removeImage", removeImage);
		query.setParameter("menu_data", dataJson);

		query.execute();
		Object result = query.getSingleResult();
		return result != null ? result.toString() : null;
	}
	
	public String changeCoffeeMenuStatus(Long token_user_id, Long menu_id, String lang) throws Exception {
		StoredProcedureQuery query = em.createStoredProcedureQuery("changeCoffeeMenuStatus");
		query.registerStoredProcedureParameter("token_user_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("menu_id", Long.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("lang", String.class, ParameterMode.IN);
		
		query.setParameter("token_user_id", token_user_id);
		query.setParameter("menu_id", menu_id);
		query.setParameter("lang", lang);
		
		query.execute();
		Object result = query.getSingleResult();
		return result != null ? result.toString() : null;
	}

	@SuppressWarnings("unchecked")
  public CoffeeMenuDTO findMenuDetails(Long schoolId, Long menuId) {
    String sql = """
      SELECT
				cm.menu_id,
				cm.school_id,
				cm.code,
				cm.name_es,
				cm.name_en,
				cm.decription_es,
				cm.decription_en,
				cm.price,
				cm.image,
				cm.created_at,
				cm.enabled
			FROM coffee_menu cm
			JOIN schools sc
				ON cm.school_id = sc.school_id
			WHERE
				cm.menu_id = :menuId
				AND (
						:schoolId IS NULL
					OR sc.school_id = :schoolId
					OR sc.related_school_id = :schoolId
				)
			LIMIT 1
		""";

    Query q = em.createNativeQuery(sql)
                .setParameter("menuId",   menuId)
                .setParameter("schoolId", schoolId);

    List<Object[]> rows = q.getResultList();
    if (rows.isEmpty()) {
      return null;
    }

    Object[] r = rows.get(0);
    CoffeeMenuDTO dto = new CoffeeMenuDTO();
    dto.setMenuId(      ((Number)   r[0]).longValue());
    dto.setSchoolId(    ((Number)   r[1]).longValue());
    dto.setCode(        (String)    r[2]);
    dto.setNameEs(      (String)    r[3]);
    dto.setNameEn(      (String)    r[4]);
    dto.setDescriptionEs((String)   r[5]);
    dto.setDescriptionEn((String)   r[6]);
    dto.setPrice(       (java.math.BigDecimal) r[7]);
    dto.setImage(       (String)    r[8]);
    if (r[9] instanceof Timestamp ts) {
      dto.setCreatedAt(ts.toLocalDateTime());
    }
    dto.setEnabled(        (Boolean)    r[10]);
    return dto;
  }

	public String processCoffeeSale(
      Long tokenUserId,
      ProcessCoffeeSaleDTO sale,
      String lang
  ) throws Exception {
    // 1) serialize sale to JSON
    String saleJson = objectMapper.writeValueAsString(sale);

    // 2) prepare stored-proc call
    StoredProcedureQuery q = em
      .createStoredProcedureQuery("processCoffeeSale")
      .registerStoredProcedureParameter("token_user_id", Integer.class, ParameterMode.IN)
      .registerStoredProcedureParameter("sale_data",      String .class, ParameterMode.IN)
      .registerStoredProcedureParameter("lang",           String .class, ParameterMode.IN);

    // 3) bind params
    q.setParameter("token_user_id", tokenUserId.intValue());
    q.setParameter("sale_data",      saleJson);
    q.setParameter("lang",           lang);

    // 4) execute & unwrap
    q.execute();
    Object single = q.getSingleResult();
    if (single instanceof Object[]) {
      return (String) ((Object[]) single)[0];
    }
    return (String) single;
  }

  public String createCoffeeMenu(
    Long tokenUserId,
    CreateCoffeeMenuDTO payload,
    String lang
  ) throws Exception {
    String json = objectMapper.writeValueAsString(payload);

    StoredProcedureQuery q = em
      .createStoredProcedureQuery("createCoffeeMenu")
      .registerStoredProcedureParameter("token_user_id", Integer.class, ParameterMode.IN)
      .registerStoredProcedureParameter("menu_data",      String .class, ParameterMode.IN)
      .registerStoredProcedureParameter("lang",           String .class, ParameterMode.IN);

    q.setParameter("token_user_id", tokenUserId.intValue());
    q.setParameter("menu_data",      json);
    q.setParameter("lang",           lang);

    q.execute();
    Object single = q.getSingleResult();
    if (single instanceof Object[]) {
      return (String) ((Object[]) single)[0];
    }
    return (String) single;
  }


}
