package com.monarchsolutions.sms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "payment_status")
public class PaymentStatusesEntity {
  @Id
  @Column(name = "payment_status_id")
  private Long id;

  @Column(name = "name_en")
  private String nameEn;

  @Column(name = "name_es")
  private String nameEs;

  public Long getId() {
    return id;
  }

  public String getNameEn() {
    return nameEn;
  }

  public String getNameEs() {
    return nameEs;
  }
  
}
