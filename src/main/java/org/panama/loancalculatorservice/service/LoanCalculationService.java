package org.panama.loancalculatorservice.service;

import org.panama.loancalculatorservice.dto.request.LoanCalculationRequest;
import org.panama.loancalculatorservice.model.LoanApplication;

public interface LoanCalculationService {

    LoanApplication calculate(LoanCalculationRequest request);
}
