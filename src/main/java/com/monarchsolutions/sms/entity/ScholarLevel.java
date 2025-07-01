package com.monarchsolutions.sms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "scholar_levels")
public class ScholarLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scholar_level_id")
    private Long scholarLevelId;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "name_es")
    private String nameEs;

    // Getters and setters
    public Long getScholarLevelId() {
        return scholarLevelId;
    }

    public void setScholarLevelId(Long scholarLevelId) {
        this.scholarLevelId = scholarLevelId;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameEs() {
        return nameEs;
    }

    public void setNameEs(String nameEs) {
        this.nameEs = nameEs;
    }
}
