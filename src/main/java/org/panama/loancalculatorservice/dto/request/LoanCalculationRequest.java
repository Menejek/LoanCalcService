package org.panama.loancalculatorservice.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.UUID;

import java.math.BigDecimal;

public record LoanCalculationRequest(

        @UUID(message = "Ключ идемпотентности должен быть действительным UUID")
        String idempotencyKey,

        @NotNull
        @DecimalMin(value = "1000.00", message = "Сумма кредите не может быть меньше 10000")
        BigDecimal totalCredit,

        @NotNull(message = "Ставка не может быть нулевой")
        @DecimalMin("0.1")
        @DecimalMax("30.0")
        BigDecimal annualRate,

        @NotNull
        @Range(min = 1, max = 360, message = "Кредит можно взять на срок не более 30 лет")
        Integer monthCount
) {
}
