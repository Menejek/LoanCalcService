package org.panama.loancalculatorservice.utils;

import lombok.Data;
import org.panama.loancalculatorservice.constants.StatusService;
import org.panama.loancalculatorservice.model.LoanApplication;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UtilData {

    public static LoanApplication generateLoanApplication(String idempotencyKey) {
        return new LoanApplication(
                UUID.fromString(idempotencyKey),
                UUID.randomUUID(),
                new BigDecimal(10000),
                new BigDecimal(20),
                100,
                StatusService.UNDER_REVIEW,
                new BigDecimal(12000)
        );
    }
}
