package com.monarchsolutions.sms.dto.reports;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PaymentRequestDetailsResponseV2 {
  public static class PaymentRequestDetailsResponse {
    private StudentInfo student;
    private PaymentRequestInfo paymentRequest;
    private List<PaymentDetail> payments;
    private PaymentInfoSummary paymentInfo;
    private List<BreakdownEntry> breakdown;
    public StudentInfo getStudent() {
      return student;
    }
    public void setStudent(StudentInfo student) {
      this.student = student;
    }
    public PaymentRequestInfo getPaymentRequest() {
      return paymentRequest;
    }
    public void setPaymentRequest(PaymentRequestInfo paymentRequest) {
      this.paymentRequest = paymentRequest;
    }
    public List<PaymentDetail> getPayments() {
      return payments;
    }
    public void setPayments(List<PaymentDetail> payments) {
      this.payments = payments;
    }
    public PaymentInfoSummary getPaymentInfo() {
      return paymentInfo;
    }
    public void setPaymentInfo(PaymentInfoSummary paymentInfo) {
      this.paymentInfo = paymentInfo;
    }
    public List<BreakdownEntry> getBreakdown() {
      return breakdown;
    }
    public void setBreakdown(List<BreakdownEntry> breakdown) {
      this.breakdown = breakdown;
    }
    
  }
  // from row_type = "student"
  public static class StudentInfo {
    private Long user_id;
    private Long student_id;
    private String register_id;
    private String payment_reference;
    private String email;
    private String full_name;
    private String address;
    private String generation;
    private String grade_group;
    private String scholar_level_name;
    private String phone_number;
    public Long getUser_id() {
      return user_id;
    }
    public void setUser_id(Long user_id) {
      this.user_id = user_id;
    }
    public Long getStudent_id() {
      return student_id;
    }
    public void setStudent_id(Long student_id) {
      this.student_id = student_id;
    }
    public String getRegister_id() {
      return register_id;
    }
    public void setRegister_id(String register_id) {
      this.register_id = register_id;
    }
    public String getPayment_reference() {
      return payment_reference;
    }
    public void setPayment_reference(String payment_reference) {
      this.payment_reference = payment_reference;
    }
    public String getEmail() {
      return email;
    }
    public void setEmail(String email) {
      this.email = email;
    }
    public String getFull_name() {
      return full_name;
    }
    public void setFull_name(String full_name) {
      this.full_name = full_name;
    }
    public String getAddress() {
      return address;
    }
    public void setAddress(String address) {
      this.address = address;
    }
    public String getGeneration() {
      return generation;
    }
    public void setGeneration(String generation) {
      this.generation = generation;
    }
    public String getGrade_group() {
      return grade_group;
    }
    public void setGrade_group(String grade_group) {
      this.grade_group = grade_group;
    }
    public String getScholar_level_name() {
      return scholar_level_name;
    }
    public void setScholar_level_name(String scholar_level_name) {
      this.scholar_level_name = scholar_level_name;
    }
    public String getPhone_number() {
      return phone_number;
    }
    public void setPhone_number(String phone_number) {
      this.phone_number = phone_number;
    }
    
  }

  // from row_type = "request"
  public static class PaymentRequestInfo {
    private Long payment_request_id;
    private BigDecimal pr_amount;
    private LocalDateTime pr_created_at;
    private LocalDateTime pr_pay_by;
    private String pr_comments;
    private BigDecimal late_fee;
    private String fee_type;
    private int late_fee_frequency;
    private LocalDate payment_month;
    private Integer payment_status_id;
    private String partial_payment_transformed;
    private boolean partial_payment;
    private int mass_upload;
    private Integer pr_payment_status_id;
    private String ps_pr_name;
    private String pt_name;
    private LocalDateTime closed_at;
    public Long getPayment_request_id() {
      return payment_request_id;
    }
    public void setPayment_request_id(Long payment_request_id) {
      this.payment_request_id = payment_request_id;
    }
    public BigDecimal getPr_amount() {
      return pr_amount;
    }
    public void setPr_amount(BigDecimal pr_amount) {
      this.pr_amount = pr_amount;
    }
    public LocalDateTime getPr_created_at() {
      return pr_created_at;
    }
    public void setPr_created_at(LocalDateTime pr_created_at) {
      this.pr_created_at = pr_created_at;
    }
    public LocalDateTime getPr_pay_by() {
      return pr_pay_by;
    }
    public void setPr_pay_by(LocalDateTime pr_pay_by) {
      this.pr_pay_by = pr_pay_by;
    }
    public String getPr_comments() {
      return pr_comments;
    }
    public void setPr_comments(String pr_comments) {
      this.pr_comments = pr_comments;
    }
    public BigDecimal getLate_fee() {
      return late_fee;
    }
    public void setLate_fee(BigDecimal late_fee) {
      this.late_fee = late_fee;
    }
    public String getFee_type() {
      return fee_type;
    }
    public void setFee_type(String fee_type) {
      this.fee_type = fee_type;
    }
    public int getLate_fee_frequency() {
      return late_fee_frequency;
    }
    public void setLate_fee_frequency(int late_fee_frequency) {
      this.late_fee_frequency = late_fee_frequency;
    }
    public LocalDate getPayment_month() {
      return payment_month;
    }
    public void setPayment_month(LocalDate payment_month) {
      this.payment_month = payment_month;
    }
    public Integer getPayment_status_id() {
      return payment_status_id;
    }
    public void setPayment_status_id(Integer payment_status_id) {
      this.payment_status_id = payment_status_id;
    }
    public String getPartial_payment_transformed() {
      return partial_payment_transformed;
    }
    public void setPartial_payment_transformed(String partial_payment_transformed) {
      this.partial_payment_transformed = partial_payment_transformed;
    }
    public boolean isPartial_payment() {
      return partial_payment;
    }
    public void setPartial_payment(boolean partial_payment) {
      this.partial_payment = partial_payment;
    }
    public int getMass_upload() {
      return mass_upload;
    }
    public void setMass_upload(int mass_upload) {
      this.mass_upload = mass_upload;
    }
    public Integer getPr_payment_status_id() {
      return pr_payment_status_id;
    }
    public void setPr_payment_status_id(Integer pr_payment_status_id) {
      this.pr_payment_status_id = pr_payment_status_id;
    }
    public String getPs_pr_name() {
      return ps_pr_name;
    }
    public void setPs_pr_name(String ps_pr_name) {
      this.ps_pr_name = ps_pr_name;
    }
    public String getPt_name() {
      return pt_name;
    }
    public void setPt_name(String pt_name) {
      this.pt_name = pt_name;
    }
    public LocalDateTime getClosed_at() {
      return closed_at;
    }
    public void setClosed_at(LocalDateTime closed_at) {
      this.closed_at = closed_at;
    }

  }

  // each row_type="payment"
  public static class PaymentDetail {
    private Long payment_id;
    private LocalDate payment_month;      // might be null
    private BigDecimal amount;
    private Long payment_status_id;
    private LocalDateTime validated_at;
    private LocalDateTime pay_created_at;
    private LocalDateTime updated_at;
    private String comments;
    private String pt_name;
    private String payment_status_name;
    private String validator_full_name;
    private String validator_phone_number;
    private String validator_username;
    public Long getPayment_id() {
      return payment_id;
    }
    public void setPayment_id(Long payment_id) {
      this.payment_id = payment_id;
    }
    public LocalDate getPayment_month() {
      return payment_month;
    }
    public void setPayment_month(LocalDate payment_month) {
      this.payment_month = payment_month;
    }
    public BigDecimal getAmount() {
      return amount;
    }
    public void setAmount(BigDecimal amount) {
      this.amount = amount;
    }
    public Long getPayment_status_id() {
      return payment_status_id;
    }
    public void setPayment_status_id(Long payment_status_id) {
      this.payment_status_id = payment_status_id;
    }
    public LocalDateTime getValidated_at() {
      return validated_at;
    }
    public void setValidated_at(LocalDateTime validated_at) {
      this.validated_at = validated_at;
    }
    public LocalDateTime getPay_created_at() {
      return pay_created_at;
    }
    public void setPay_created_at(LocalDateTime pay_created_at) {
      this.pay_created_at = pay_created_at;
    }
    public LocalDateTime getUpdated_at() {
      return updated_at;
    }
    public void setUpdated_at(LocalDateTime updated_at) {
      this.updated_at = updated_at;
    }
    public String getComments() {
      return comments;
    }
    public void setComments(String comments) {
      this.comments = comments;
    }
    public String getPt_name() {
      return pt_name;
    }
    public void setPt_name(String pt_name) {
      this.pt_name = pt_name;
    }
    public String getPayment_status_name() {
      return payment_status_name;
    }
    public void setPayment_status_name(String payment_status_name) {
      this.payment_status_name = payment_status_name;
    }
    public String getValidator_full_name() {
      return validator_full_name;
    }
    public void setValidator_full_name(String validator_full_name) {
      this.validator_full_name = validator_full_name;
    }
    public String getValidator_phone_number() {
      return validator_phone_number;
    }
    public void setValidator_phone_number(String validator_phone_number) {
      this.validator_phone_number = validator_phone_number;
    }
    public String getValidator_username() {
      return validator_username;
    }
    public void setValidator_username(String validator_username) {
      this.validator_username = validator_username;
    }

  }

  public static class PaymentInfoSummary {
    private BigDecimal totalPaid;
    private long latePeriods;
    private BigDecimal lateFeeTotal;
    private BigDecimal accumulatedFees;
    private BigDecimal pendingPayment;

    public PaymentInfoSummary(
        BigDecimal totalPaid,
        long latePeriods,
        BigDecimal lateFeeTotal,
        BigDecimal accumulatedFees,
        BigDecimal pendingPayment
    ) {
      this.totalPaid = totalPaid;
      this.latePeriods = latePeriods;
      this.lateFeeTotal = lateFeeTotal;
      this.accumulatedFees = accumulatedFees;
      this.pendingPayment = pendingPayment;
    }
    public BigDecimal getTotalPaid() {
      return totalPaid;
    }
    public void setTotalPaid(BigDecimal totalPaid) {
      this.totalPaid = totalPaid;
    }
    public long getLatePeriods() {
      return latePeriods;
    }
    public void setLatePeriods(long latePeriods) {
      this.latePeriods = latePeriods;
    }
    public BigDecimal getLateFeeTotal() {
      return lateFeeTotal;
    }
    public void setLateFeeTotal(BigDecimal lateFeeTotal) {
      this.lateFeeTotal = lateFeeTotal;
    }
    public BigDecimal getAccumulatedFees() {
      return accumulatedFees;
    }
    public void setAccumulatedFees(BigDecimal accumulatedFees) {
      this.accumulatedFees = accumulatedFees;
    }
    public BigDecimal getPendingPayment() {
      return pendingPayment;
    }
    public void setPendingPayment(BigDecimal pendingPayment) {
      this.pendingPayment = pendingPayment;
    }
  }

  public static class BreakdownEntry {
    private Long payment_id;
    private String type;
    private Long payment_status_id;
    private String status_name;
    private LocalDateTime date;
    private BigDecimal amount;
    private BigDecimal balance;

    public BreakdownEntry(Long payment_id, String type, Long payment_status_id, String status_name, LocalDateTime date, BigDecimal amount, BigDecimal balance) {
      this.payment_id = payment_id;
      this.type = type;
      this.payment_status_id = payment_status_id;
      this.status_name = status_name;
      this.date = date;
      this.amount = amount;
      this.balance = balance;
    }

    public Long getPayment_id() {
      return payment_id;
    }
    public void setPayment_id(Long payment_id) {
      this.payment_id = payment_id;
    }
    public String getType() {
      return type;
    }
    public void setType(String type) {
      this.type = type;
    }
    public Long getPayment_status_id() {
      return payment_status_id;
    }
    public void setPayment_status_id(Long payment_status_id) {
      this.payment_status_id = payment_status_id;
    }
    public String getStatus_name() {
      return status_name;
    }
    public void setStatus_name(String status_name) {
      this.status_name = status_name;
    }
    public LocalDateTime getDate() {
      return date;
    }
    public void setDate(LocalDateTime date) {
      this.date = date;
    }
    public BigDecimal getAmount() {
      return amount;
    }
    public void setAmount(BigDecimal amount) {
      this.amount = amount;
    }
    public BigDecimal getBalance() {
      return balance;
    }
    public void setBalance(BigDecimal balance) {
      this.balance = balance;
    }
  }
}
