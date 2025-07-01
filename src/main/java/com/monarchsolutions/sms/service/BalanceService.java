package com.monarchsolutions.sms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.monarchsolutions.sms.dto.balance.CreateBalanceRechargeDTO;
import com.monarchsolutions.sms.repository.BalanceRepository;

@Service
public class BalanceService {
  
  @Autowired
  private BalanceRepository balanceRepository;

  /**
   * Performs a balance recharge by calling the repository/SP.
   */
  public String rechargeBalance(
      Long tokenUserId,
      CreateBalanceRechargeDTO dto,
      String lang
  ) throws Exception {
    return balanceRepository.createBalanceRecharge(tokenUserId, dto, lang);
  }
  
}
