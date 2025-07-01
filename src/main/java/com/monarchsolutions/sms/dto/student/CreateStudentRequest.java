package com.monarchsolutions.sms.dto.student;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.monarchsolutions.sms.validation.AdminGroup;
import com.monarchsolutions.sms.validation.SchoolAdminGroup;

public class CreateStudentRequest {
    @NotNull(message = "School is required", groups = {SchoolAdminGroup.class, SchoolAdminGroup.class})
    @Min(value = 1, message = "School ID must be greater than 0", groups = {SchoolAdminGroup.class, SchoolAdminGroup.class})
    private Long school_id;

    private Long group_id;

    @NotNull(message = "Register ID is required", groups = {AdminGroup.class, SchoolAdminGroup.class})
    @NotBlank(message = "Register ID is required", groups = {AdminGroup.class, SchoolAdminGroup.class})
    private String register_id;
    private String payment_reference;

    @NotNull(message = "Name is required", groups = {AdminGroup.class, SchoolAdminGroup.class})
    @NotBlank(message = "Name is required", groups = {AdminGroup.class, SchoolAdminGroup.class})
    private String first_name;
    
    @NotNull(message = "Father's last name is required", groups = {AdminGroup.class, SchoolAdminGroup.class})
    @NotBlank(message = "Father's last name is required", groups = {AdminGroup.class, SchoolAdminGroup.class})
    private String last_name_father;
    private String last_name_mother;
    private LocalDate birth_date;
    private String phone_number;
    private String tax_id;
    private String curp;
    private String street;
    private String ext_number;
    private String int_number;
    private String suburb;
    private String locality;
    private String municipality;
    private String state;
    private String personal_email;
    private String image;
    @NotNull(message = "Email is required", groups = {AdminGroup.class, SchoolAdminGroup.class})
    @NotBlank(message = "Email is required", groups = {AdminGroup.class, SchoolAdminGroup.class})
    private String email;
    @NotNull(message = "Username is required", groups = {AdminGroup.class, SchoolAdminGroup.class})
    @NotBlank(message = "Username is required", groups = {AdminGroup.class, SchoolAdminGroup.class})
    private String username;
    @NotNull(message = "Password is required", groups = {AdminGroup.class, SchoolAdminGroup.class})
    @NotBlank(message = "Password is required", groups = {AdminGroup.class, SchoolAdminGroup.class})
    private String password;
    private BigDecimal tuition;

    public Long getSchool_id() {
        return school_id;
    }
    public void setSchool_id(Long school_id) {
        this.school_id = school_id;
    }
    public Long getGroup_id() {
        return group_id;
    }
    public void setGroup_id(Long group_id) {
        this.group_id = group_id;
    }
    public String getFirst_name() {
        return first_name;
    }
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }
    public String getLast_name_father() {
        return last_name_father;
    }
    public void setLast_name_father(String last_name_father) {
        this.last_name_father = last_name_father;
    }
    public String getLast_name_mother() {
        return last_name_mother;
    }
    public void setLast_name_mother(String last_name_mother) {
        this.last_name_mother = last_name_mother;
    }
    public LocalDate getBirth_date() {
        return birth_date;
    }
    public void setBirth_date(LocalDate birth_date) {
        this.birth_date = birth_date;
    }
    public String getPhone_number() {
        return phone_number;
    }
    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
    public String getTax_id() {
        return tax_id;
    }
    public void setTax_id(String tax_id) {
        this.tax_id = tax_id;
    }
    public String getCurp() {
        return curp;
    }
    public void setCurp(String curp) {
        this.curp = curp;
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
    public String getPersonal_email() {
        return personal_email;
    }
    public void setPersonal_email(String personal_email) {
        this.personal_email = personal_email;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRegister_id() {
        return register_id;
    }
    public void setRegister_id(String register_id) {
        this.register_id = register_id;
    }
    public String getPayment_reference() {
        return payment_reference;
    }
    public void setPayment_reference(String payment_reference) {
        this.payment_reference = payment_reference;
    }
    public BigDecimal getTuition() {
        return tuition;
    }
    public void setTuition(BigDecimal tuition) {
        this.tuition = tuition;
    }
    
}
