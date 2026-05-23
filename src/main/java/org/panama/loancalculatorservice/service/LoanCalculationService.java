package org.panama.loancalculatorservice.service;

import org.panama.loancalculatorservice.dto.request.LoanCalculationRequest;
import org.panama.loancalculatorservice.dto.response.LoanCalculationResponse;
import org.panama.loancalculatorservice.model.User;

public interface LoanCalculationService {

    LoanCalculationResponse calculate(LoanCalculationRequest request);

    void saveUser(User user);
}
