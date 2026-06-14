package org.panama.loancalculatorservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record LoanCreatedEvent (
        UUID applicationId,
        UUID idempotencyKey,
        BigDecimal totalCredit,
        BigDecimal annualRate,
        Integer monthCount,
        BigDecimal monthlyPayment,
        String status
){}
