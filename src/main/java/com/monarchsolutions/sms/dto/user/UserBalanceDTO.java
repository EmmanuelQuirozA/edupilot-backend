package com.monarchsolutions.sms.dto.user;

import java.math.BigDecimal;

public class UserBalanceDTO {
    private Long       userId;
    private String     fullName;
    private BigDecimal balance;

    public UserBalanceDTO(Long userId, String fullName, BigDecimal balance) {
        this.userId   = userId;
        this.fullName = fullName;
        this.balance  = balance;
    }

    // getters & setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}
