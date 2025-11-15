package com.monarchsolutions.sms.dto.groups;

public class GetClassesCatalog {
  private Long group_id;  
  private String generation;
  private String scholar_level_name;
  public String getScholar_level_name() {
    return scholar_level_name;
  }
  public void setScholar_level_name(String scholar_level_name) {
    this.scholar_level_name = scholar_level_name;
  }
  private String grade_group;
  public Long getGroup_id() {
    return group_id;
  }
  public void setGroup_id(Long group_id) {
    this.group_id = group_id;
  }
  public String getGeneration() {
    return generation;
  }
  public void setGeneration(String generation) {
    this.generation = generation;
  }
  public String getGrade_group() {
    return grade_group;
  }
  public void setGrade_group(String grade_group) {
    this.grade_group = grade_group;
  }  
}
