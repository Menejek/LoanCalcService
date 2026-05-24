package org.panama.loancalculatorservice.dto.response;

import java.math.BigDecimal;

public record LoanCalculationResponse (
    BigDecimal monthlyPayment,
    BigDecimal totalInterest,
    BigDecimal totalPayment,
    BigDecimal annualRatePercent
) {}