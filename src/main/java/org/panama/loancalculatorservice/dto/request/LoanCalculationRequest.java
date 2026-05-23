package org.panama.loancalculatorservice.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoanCalculationRequest {

    @NotNull
    @DecimalMin("1000.00")
    BigDecimal totalCredit;

    @NotNull
    @DecimalMin("0.1")
    @DecimalMax("30.0")
    BigDecimal annualRate;

    @NotNull
    @Range(min = 1, max = 360, message = "Кредит можно взять на срок не более 30 лет")
    Integer monthCount;
}
