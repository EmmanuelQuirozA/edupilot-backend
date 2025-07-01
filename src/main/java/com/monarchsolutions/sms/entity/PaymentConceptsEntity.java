package com.monarchsolutions.sms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "payment_concepts")
public class PaymentConceptsEntity {
  @Id
  @Column(name = "payment_concept_id")
  private Long id;

  @Column(name = "name_en")
  private String nameEn;

  @Column(name = "name_es")
  private String nameEs;

  @Column(name = "description_en")
  private String descEn;

  @Column(name = "description_es")
  private String descEs;

  public Long getId() {
    return id;
  }

  public String getNameEn() {
    return nameEn;
  }

  public String getNameEs() {
    return nameEs;
  }

  public String getDescEn() {
    return descEn;
  }

  public String getDescEs() {
    return descEs;
  }

  
}
