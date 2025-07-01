package com.monarchsolutions.sms.repository;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.monarchsolutions.sms.dto.balance.CreateBalanceRechargeDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

@Repository
public class BalanceRepository {
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
      Long tokenUserId,
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

    q.setParameter("token_user_id", tokenUserId.intValue());
    q.setParameter("recharge_data",  payload);
    q.setParameter("lang",           lang);

    q.execute();
    Object single = q.getSingleResult();
    if (single instanceof Object[]) {
      return (String) ((Object[]) single)[0];
    }
    return (String) single;
  }
  
}
