package com.monarchsolutions.sms.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monarchsolutions.sms.dto.balance.AccountActivityRecord;
import com.monarchsolutions.sms.dto.balance.CreateBalanceRechargeDTO;
import com.monarchsolutions.sms.dto.balance.DailyActivityDto;
import com.monarchsolutions.sms.dto.balance.MonthlyActivityDto;
import com.monarchsolutions.sms.dto.balance.YearlyActivityDto;
import com.monarchsolutions.sms.dto.common.PageResult;
import com.monarchsolutions.sms.repository.BalanceRepository;

@Service
public class BalanceService {
  
  @Autowired
  private BalanceRepository balanceRepository;

  /**
   * Performs a balance recharge by calling the repository/SP.
   */
  public String rechargeBalance(
      Long token_user_id,
      CreateBalanceRechargeDTO dto,
      String lang
  ) throws Exception {
    return balanceRepository.createBalanceRecharge(token_user_id, dto, lang);
  }

  @Transactional(readOnly = true)
  public PageResult<Map<String,Object>> getAccountActivity(
      Long token_school_id,
      Long user_id,
      String lang,
      int page,
      int size,
      Boolean exportAll,
      String order_by,
      String order_dir
  ) throws Exception {
      return balanceRepository.getAccountActivity(
      token_school_id,
      user_id,
      lang,
      page,
      size,
      exportAll,
      order_by,
      order_dir
      );
  }

  public List<YearlyActivityDto> getAccountActivityGroup(
    Long token_user_id,
    Long user_id,
    String  lang
  ) {
    List<AccountActivityRecord> flat = balanceRepository.getAccountActivityGroup(token_user_id, user_id, lang);

    // group by year → month → day
    Map<Integer, Map<Integer, Map<Integer, List<AccountActivityRecord>>>> byYMD =
      flat.stream().collect(
        Collectors.groupingBy(
          rec -> rec.getCreatedAt().getYear(),
          LinkedHashMap::new,
          Collectors.groupingBy(
            rec -> rec.getCreatedAt().getMonthValue(),
            LinkedHashMap::new,
            Collectors.groupingBy(
              rec -> rec.getCreatedAt().getDayOfMonth(),
              LinkedHashMap::new,
              Collectors.toList()
            )
          )
        )
      );

    List<YearlyActivityDto> result = new ArrayList<>();
    for (var yearEntry : byYMD.entrySet()) {
      int year = yearEntry.getKey();
      Map<Integer, Map<Integer, List<AccountActivityRecord>>> monthsMap = yearEntry.getValue();

      List<MonthlyActivityDto> months = new ArrayList<>();
      for (var monthEntry : monthsMap.entrySet()) {
        int month = monthEntry.getKey();
        Map<Integer, List<AccountActivityRecord>> daysMap = monthEntry.getValue();

        List<DailyActivityDto> days = new ArrayList<>();
        for (var dayEntry : daysMap.entrySet()) {
          int day = dayEntry.getKey();
          List<AccountActivityRecord> items = dayEntry.getValue();
          days.add(new DailyActivityDto(day, items));
        }

        months.add(new MonthlyActivityDto(month, days));
      }

      result.add(new YearlyActivityDto(year, months));
    }

    return result;
  }
  
}
