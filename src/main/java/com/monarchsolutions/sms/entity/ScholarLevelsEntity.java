package com.monarchsolutions.sms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "payment_through")
public class ScholarLevelsEntity {
  @Id
  @Column(name = "scholar_level_id")
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
