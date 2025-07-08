package com.monarchsolutions.sms.dto.school;

import java.math.BigDecimal;

public class SchoolsList {
    private Long school_id;
    private Long related_school_id;
    private String description;
    private String description_en;
    private String description_es;
    private String commercial_name;
    private String business_name;
    private String tax_id;
    private String address;
    private String street;
    private String ext_number;
    private String int_number;
    private String suburb;
    private String locality;
    private String municipality;
    private String state;
    private String phone_number;
    private String email;
    private Boolean enabled;
    private BigDecimal default_tuition;
    private String school_status;
    private String image;

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public Long getSchool_id() {
        return school_id;
    }
    public void setSchool_id(Long school_id) {
        this.school_id = school_id;
    }
    public Long getRelated_school_id() {
        return related_school_id;
    }
    public void setRelated_school_id(Long related_school_id) {
        this.related_school_id = related_school_id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription_en() {
        return description_en;
    }
    public void setDescription_en(String description_en) {
        this.description_en = description_en;
    }
    public String getDescription_es() {
        return description_es;
    }
    public void setDescription_es(String description_es) {
        this.description_es = description_es;
    }
    public String getCommercial_name() {
        return commercial_name;
    }
    public void setCommercial_name(String commercial_name) {
        this.commercial_name = commercial_name;
    }
    public String getBusiness_name() {
        return business_name;
    }
    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }
    public String getTax_id() {
        return tax_id;
    }
    public void setTax_id(String tax_id) {
        this.tax_id = tax_id;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }
    public String getExt_number() {
        return ext_number;
    }
    public void setExt_number(String ext_number) {
        this.ext_number = ext_number;
    }
    public String getInt_number() {
        return int_number;
    }
    public void setInt_number(String int_number) {
        this.int_number = int_number;
    }
    public String getSuburb() {
        return suburb;
    }
    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }
    public String getLocality() {
        return locality;
    }
    public void setLocality(String locality) {
        this.locality = locality;
    }
    public String getMunicipality() {
        return municipality;
    }
    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getPhone_number() {
        return phone_number;
    }
    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    public BigDecimal getDefault_tuition() {
        return default_tuition;
    }
    public void setDefault_tuition(BigDecimal default_tuition) {
        this.default_tuition = default_tuition;
    }
    public String getSchool_status() {
        return school_status;
    }
    public void setSchool_status(String school_status) {
        this.school_status = school_status;
    }
    
}
