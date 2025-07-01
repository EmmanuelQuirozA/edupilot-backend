package com.monarchsolutions.sms.dto.coffee;

import java.util.List;

public class ProcessCoffeeSaleDTO {
  private Long userId;
  private List<SaleItemDTO> items;

  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }

  public List<SaleItemDTO> getItems() { return items; }
  public void setItems(List<SaleItemDTO> items) { this.items = items; }
}
