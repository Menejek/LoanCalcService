package org.panama.loancalculatorservice.service;

import org.panama.loancalculatorservice.dto.request.LoanCalculationRequest;
import org.panama.loancalculatorservice.dto.response.LoanCalculationResponse;

public interface LoanCalculationService {

    LoanCalculationResponse calculate(LoanCalculationRequest request);
}
