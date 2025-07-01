package com.monarchsolutions.sms.dto.coffee;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UserPurchasesDTO {
  private Long coffee_sale_id;
  private Number sale;
  private String item_name;
  private LocalDateTime created_at;
  private Number quantity;
  private BigDecimal unit_price;
  private BigDecimal total;
  
  public Long getCoffee_sale_id() {
    return coffee_sale_id;
  }
  public void setCoffee_sale_id(Long coffee_sale_id) {
    this.coffee_sale_id = coffee_sale_id;
  }
  public Number getSale() {
    return sale;
  }
  public void setSale(Number sale) {
    this.sale = sale;
  }
  public String getItem_name() {
    return item_name;
  }
  public void setItem_name(String item_name) {
    this.item_name = item_name;
  }
  public LocalDateTime getCreated_at() {
    return created_at;
  }
  public void setCreated_at(LocalDateTime created_at) {
    this.created_at = created_at;
  }
  public Number getQuantity() {
    return quantity;
  }
  public void setQuantity(Number quantity) {
    this.quantity = quantity;
  }
  public BigDecimal getUnit_price() {
    return unit_price;
  }
  public void setUnit_price(BigDecimal unit_price) {
    this.unit_price = unit_price;
  }
  public BigDecimal getTotal() {
    return total;
  }
  public void setTotal(BigDecimal total) {
    this.total = total;
  }
  
}
