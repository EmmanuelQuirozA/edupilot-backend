package com.monarchsolutions.sms.dto.userLogs.payments;

import java.time.LocalDateTime;
import java.util.List;

public class PaymentLogGroupDto {
  private Long payment_id;
    private Long responsable_user_id;
    private String responsable_full_name;
    private String role_name;
    private LocalDateTime updated_at;
    private String log_type_name;
    private List<ChangePaymentDto> changes;
    public Long getPayment_id() {
      return payment_id;
    }
    public void setPayment_id(Long payment_id) {
      this.payment_id = payment_id;
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
    public List<ChangePaymentDto> getChanges() {
      return changes;
    }
    public void setChanges(List<ChangePaymentDto> changes) {
      this.changes = changes;
    }
    public String getLog_type_name() {
      return log_type_name;
    }
    public void setLog_type_name(String log_type_name) {
      this.log_type_name = log_type_name;
    }
    
}
