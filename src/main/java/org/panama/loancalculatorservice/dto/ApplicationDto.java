package org.panama.loancalculatorservice.dto;

import org.panama.loancalculatorservice.dto.response.LoanCalculationResponse;
import org.panama.loancalculatorservice.model.LoanApplication;
import org.springframework.stereotype.Component;

@Component
public class ApplicationDto {

    public LoanCalculationResponse toResponse(LoanApplication application, String message) {
        return new LoanCalculationResponse(
                application.getMonthlyPayment(),
                application.getApplicationId().toString(),
                application.getStatus().name(),
                message
        );
    }
}