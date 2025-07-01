package com.monarchsolutions.sms.dto.reports;

import java.util.List;

import com.monarchsolutions.sms.dto.student.StudentListResponse;

public class PaymentRequestDetailsResponse {
  private StudentListResponse student;
  private PaymentRequestListResponse request;
  private List<PaymentsResponse> payments;
  private List<PaymentRequestBreakdownResponse> paymentsBreakdown;
  public StudentListResponse getStudent() {
    return student;
  }
  public void setStudent(StudentListResponse student) {
    this.student = student;
  }
  public PaymentRequestListResponse getRequest() {
    return request;
  }
  public void setRequest(PaymentRequestListResponse request) {
    this.request = request;
  }
  public List<PaymentsResponse> getPayments() {
    return payments;
  }
  public void setPayments(List<PaymentsResponse> payments) {
    this.payments = payments;
  }
  public List<PaymentRequestBreakdownResponse> getPaymentsBreakdown() {
    return paymentsBreakdown;
  }
  public void setPaymentsBreakdown(List<PaymentRequestBreakdownResponse> paymentsBreakdown) {
    this.paymentsBreakdown = paymentsBreakdown;
  }
  
}
