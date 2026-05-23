package org.panama.loancalculatorservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoanCalculationResponse {

    BigDecimal monthlyPayment;
    BigDecimal totalInterest;
    BigDecimal totalPayment;
    BigDecimal annualRatePercent;
}