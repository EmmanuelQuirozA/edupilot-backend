package com.monarchsolutions.sms.dto.userLogs.paymentRequest;

import java.time.LocalDateTime;
import java.util.List;

public class PaymentRequestLogGroupDto {
  private Long payment_request_id;
    private Long responsable_user_id;
    private String responsable_full_name;
    private String role_name;
    private LocalDateTime updated_at;
    private String log_type_name;
    private List<ChangeDto> changes;
    public Long getPayment_request_id() {
      return payment_request_id;
    }
    public void setPayment_request_id(Long payment_request_id) {
      this.payment_request_id = payment_request_id;
    }
    public Long getResponsable_user_id() {
      return responsable_user_id;
    }
    public void setResponsable_user_id(Long responsable_user_id) {
      this.responsable_user_id = responsable_user_id;
    }
    public String getResponsable_full_name() {
      return responsable_full_name;
    }
    public void setResponsable_full_name(String responsable_full_name) {
      this.responsable_full_name = responsable_full_name;
    }
    public String getRole_name() {
      return role_name;
    }
    public void setRole_name(String role_name) {
      this.role_name = role_name;
    }
    public LocalDateTime getUpdated_at() {
      return updated_at;
    }
    public void setUpdated_at(LocalDateTime updated_at) {
      this.updated_at = updated_at;
    }
    public List<ChangeDto> getChanges() {
      return changes;
    }
    public void setChanges(List<ChangeDto> changes) {
      this.changes = changes;
    }
    public String getLog_type_name() {
      return log_type_name;
    }
    public void setLog_type_name(String log_type_name) {
      this.log_type_name = log_type_name;
    }
    
}
