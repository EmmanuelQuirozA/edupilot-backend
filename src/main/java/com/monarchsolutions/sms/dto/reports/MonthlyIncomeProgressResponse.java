package com.monarchsolutions.sms.dto.reports;

import java.math.BigDecimal;

public class MonthlyIncomeProgressResponse {

    private BigDecimal monthIncomeTotal;
    private BigDecimal monthGoalTotal;
    private BigDecimal monthProgressPct;

    public BigDecimal getMonthIncomeTotal() {
        return monthIncomeTotal;
    }

    public void setMonthIncomeTotal(BigDecimal monthIncomeTotal) {
        this.monthIncomeTotal = monthIncomeTotal;
    }

    public BigDecimal getMonthGoalTotal() {
        return monthGoalTotal;
    }

    public void setMonthGoalTotal(BigDecimal monthGoalTotal) {
        this.monthGoalTotal = monthGoalTotal;
    }

    public BigDecimal getMonthProgressPct() {
        return monthProgressPct;
    }

    public void setMonthProgressPct(BigDecimal monthProgressPct) {
        this.monthProgressPct = monthProgressPct;
    }
}
