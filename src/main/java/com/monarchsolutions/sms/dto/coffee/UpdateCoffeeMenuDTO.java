package com.monarchsolutions.sms.dto.coffee;

import java.math.BigDecimal;

public class UpdateCoffeeMenuDTO {
    private Long     menuId;         // required
    private Long     schoolId;
    private String      code;
    private String      nameEs;
    private String      nameEn;
    private String      descriptionEs;
    private String      descriptionEn;
    private BigDecimal  price;
    private Boolean     enabled;
    private String      image;
    public Long getMenuId() {
        return menuId;
    }
    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }
    public Long getSchoolId() {
        return schoolId;
    }
    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getNameEs() {
        return nameEs;
    }
    public void setNameEs(String nameEs) {
        this.nameEs = nameEs;
    }
    public String getNameEn() {
        return nameEn;
    }
    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }
    public String getDescriptionEs() {
        return descriptionEs;
    }
    public void setDescriptionEs(String descriptionEs) {
        this.descriptionEs = descriptionEs;
    }
    public String getDescriptionEn() {
        return descriptionEn;
    }
    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public Boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

}