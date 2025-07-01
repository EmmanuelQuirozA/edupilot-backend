package com.monarchsolutions.sms.repository;

public class MappingConfig {
  private String field;
  private Class<?> type;

  public MappingConfig(String field, Class<?> type) {
      this.field = field;
      this.type = type;
  }

  public String getField() { return field; }
  public Class<?> getType() { return type; }
}