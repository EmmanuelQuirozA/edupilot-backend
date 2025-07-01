package com.monarchsolutions.sms.dto.coffee;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SaleGroupDTO {
  private Number           sale;
  private LocalDateTime    created_at;
  private BigDecimal       total;   // sum of item-totals in this sale
  private List<UserPurchasesDTO> items;

  // constructors, getters & setters
  public SaleGroupDTO(Number sale,
                      LocalDateTime created_at,
                      BigDecimal total,
                      List<UserPurchasesDTO> items) {
    this.sale       = sale;
    this.created_at = created_at;
    this.total      = total;
    this.items      = items;
  }
  public Number getSale() {
    return sale;
  }
  public void setSale(Number sale) {
    this.sale = sale;
  }
  public LocalDateTime getCreated_at() {
    return created_at;
  }
  public void setCreated_at(LocalDateTime created_at) {
    this.created_at = created_at;
  }
  public BigDecimal getTotal() {
    return total;
  }
  public void setTotal(BigDecimal total) {
    this.total = total;
  }
  public List<UserPurchasesDTO> getItems() {
    return items;
  }
  public void setItems(List<UserPurchasesDTO> items) {
    this.items = items;
  }
}