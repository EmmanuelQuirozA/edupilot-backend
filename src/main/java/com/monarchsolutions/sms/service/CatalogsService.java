package com.monarchsolutions.sms.service;

import com.monarchsolutions.sms.dto.catalogs.PaymentConceptsDto;
import com.monarchsolutions.sms.dto.catalogs.PaymentStatusesDto;
import com.monarchsolutions.sms.dto.catalogs.PaymentThroughDto;
import com.monarchsolutions.sms.dto.catalogs.ScholarLevelsDto;
import com.monarchsolutions.sms.dto.catalogs.PeriodOfTimeDto;
import com.monarchsolutions.sms.dto.catalogs.PlanModuleDto;

import com.monarchsolutions.sms.repository.catalogs.PaymentConceptsRepository;
import com.monarchsolutions.sms.repository.catalogs.PaymentStatusesRepository;
import com.monarchsolutions.sms.repository.catalogs.PaymentThroughRepository;
import com.monarchsolutions.sms.repository.catalogs.ScholarLevelsRepository;
import com.monarchsolutions.sms.repository.catalogs.PeriodsOfTimeRepository;
import com.monarchsolutions.sms.repository.catalogs.PlanModulesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogsService {

  @Autowired
  private PaymentConceptsRepository paymentConceptsRepository;
  @Autowired
  private PaymentStatusesRepository paymentStatusesRepository;
  @Autowired
  private PaymentThroughRepository paymentThroughRepository;
  @Autowired
  private ScholarLevelsRepository scholarLevelsRepository;
  @Autowired
  private PeriodsOfTimeRepository periodsOfTimeRepository;
  @Autowired
  private PlanModulesRepository planModulesRepository;

  public List<PaymentConceptsDto> getPaymentConcepts(String lang) {
      return paymentConceptsRepository.findAllByLang(lang);
  }

  public List<PaymentStatusesDto> getPaymentStatuses(String lang) {
      return paymentStatusesRepository.findAllByLang(lang);
  }

  public List<PaymentThroughDto> getPaymentThrough(String lang) {
      return paymentThroughRepository.findAllByLang(lang);
  }

  public List<ScholarLevelsDto> getScholarLevels(String lang) {
      return scholarLevelsRepository.findAllByLang(lang);
  }
  
  public List<PeriodOfTimeDto> getPeriodOfTime(String lang, Long tokenUserId) {
      return periodsOfTimeRepository.findAllByLang(lang, tokenUserId);
  }

  public List<PlanModuleDto> getPlanModules(String lang, Long tokenUserId, Long schoolId) {
      return planModulesRepository.findPlanModules(lang, tokenUserId, schoolId);
  }
  
}
