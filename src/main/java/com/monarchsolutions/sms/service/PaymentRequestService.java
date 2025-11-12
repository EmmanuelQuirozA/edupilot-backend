package com.monarchsolutions.sms.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.monarchsolutions.sms.dto.paymentRequests.CreatePaymentRequestDTO;
import com.monarchsolutions.sms.dto.paymentRequests.CreatePaymentRecurrenceDTO;
import com.monarchsolutions.sms.dto.paymentRequests.StudentPaymentRequestDTO;
import com.monarchsolutions.sms.dto.paymentRequests.ValidatePaymentRequestExistence;
import com.monarchsolutions.sms.repository.PaymentRequestRepository;

@Service
public class PaymentRequestService {
    
  @Autowired
  private PaymentRequestRepository paymentRequestRepository;

  public Map<String, Object> createPaymentRequest(Long token_user_id,
                                                  Long school_id,
                                                  Long group_id,
                                                  Long student_id,
                                                  CreatePaymentRequestDTO request,
                                                  String lang) throws Exception {
      // Call the repository method that converts the request to JSON and executes the stored procedure
      return paymentRequestRepository.createPaymentRequest(token_user_id,  school_id, group_id, student_id, request, lang);
  }

  public String createPaymentRecurrence(Long tokenUserId,
                                        CreatePaymentRecurrenceDTO dto,
                                        String lang) {
    int count = 0;
    if (dto.getSchool_id() != null) count++;
    if (dto.getGroup_id() != null) count++;
    if (dto.getStudent_id() != null) count++;
    if (count != 1) {
      throw new IllegalArgumentException("Provide exactly one of school_id, group_id or student_id");
    }
    return paymentRequestRepository.createPaymentRecurrence(tokenUserId, dto, lang);
  }

  public List<ValidatePaymentRequestExistence> validatePaymentRequests(Long token_user_id, Long school_id, Long group_id, Long payment_concept_id, Date payment_month) {
		// If tokenSchoolId is not null, the SP will filter students by school.
		return paymentRequestRepository.validatePaymentRequests(token_user_id,  school_id, group_id, payment_concept_id, payment_month);
	}


  public BigDecimal getPendingByStudent(Long token_user_id, Long studentId) {
    return paymentRequestRepository.getPendingByStudent(token_user_id, studentId);
  }

  public List<StudentPaymentRequestDTO> getStudentPaymentRequests(Long studentId, String lang) {
    return paymentRequestRepository.getStudentPaymentRequests(studentId,lang);
  }
}
