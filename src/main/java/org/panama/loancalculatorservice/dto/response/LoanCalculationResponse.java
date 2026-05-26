package org.panama.loancalculatorservice.dto.response;

import java.math.BigDecimal;

public record LoanCalculationResponse (
    BigDecimal estimatedMonthlyPayment,
    String applicationId,
    String status,
    String message
) {}