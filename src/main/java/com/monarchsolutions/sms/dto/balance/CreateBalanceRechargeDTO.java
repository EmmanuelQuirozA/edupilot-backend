package com.monarchsolutions.sms.dto.balance;

import java.math.BigDecimal;

public class CreateBalanceRechargeDTO {
    private Long      userId;
    private Boolean   ticket;
    private BigDecimal amount;

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Boolean getTicket() {
        return ticket;
    }
    public void setTicket(Boolean ticket) {
        this.ticket = ticket;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

