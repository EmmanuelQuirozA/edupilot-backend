package com.monarchsolutions.sms.dto.balance;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountActivityRecord {
  private String     concept;
  private String     sale;
  private String     fullName;
  private LocalDateTime createdAt;
  private int        quantity;
  private BigDecimal unitPrice;
  private BigDecimal amount;
  
  public String getConcept() {
    return concept;
  }
  public void setConcept(String concept) {
    this.concept = concept;
  }
  public String getSale() {
    return sale;
  }
  public void setSale(String sale) {
    this.sale = sale;
  }
  public String getFullName() {
    return fullName;
  }
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
  public int getQuantity() {
    return quantity;
  }
  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }
  public BigDecimal getUnitPrice() {
    return unitPrice;
  }
  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }
  public BigDecimal getAmount() {
    return amount;
  }
  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }
  
}
