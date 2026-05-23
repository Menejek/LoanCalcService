package org.panama.loancalculatorservice.service.impl;

import org.panama.loancalculatorservice.dto.request.LoanCalculationRequest;
import org.panama.loancalculatorservice.dto.response.LoanCalculationResponse;
import org.panama.loancalculatorservice.model.User;
import org.panama.loancalculatorservice.service.LoanCalculationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Service
public class LoanCalculationServiceImpl implements LoanCalculationService {

    private final int FINAL_SCALE = 2;
    private final int LOAN_SCALE = 10;
    private final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    @Override
    public LoanCalculationResponse calculate(LoanCalculationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null.");
        }

        validateRequestParameters(request);

        BigDecimal totalCredit = request.getTotalCredit();
        BigDecimal annualRate = request.getAnnualRate();
        int monthCount = request.getMonthCount();

        return calculateLoan(totalCredit, monthCount, annualRate);
    }

    @Override
    public void saveUser(User user) {
            // TODO
    }

    private void validateRequestParameters(LoanCalculationRequest request) {
        if (request.getTotalCredit().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Total credit can't be less than 0.");
        }
        if (request.getAnnualRate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Annual rate can't be less than 0.");
        }
        if (request.getMonthCount() < 1 || request.getMonthCount() > 360) {
            throw new IllegalArgumentException("Monthly count can't be less than 1 or more than 360.");
        }
    }

    private LoanCalculationResponse calculateLoan(BigDecimal totalCredit, Integer monthCount, BigDecimal annualRate) {
        BigDecimal rawPayment;

        BigDecimal monthlyRate = annualRate.divide(new BigDecimal("100"), LOAN_SCALE, ROUNDING_MODE).divide(new BigDecimal("12"), LOAN_SCALE, ROUNDING_MODE);

        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            rawPayment = totalCredit.divide(BigDecimal.valueOf(monthCount), LOAN_SCALE, ROUNDING_MODE);
        } else {
            BigDecimal multiplier = (BigDecimal.ONE.add(monthlyRate)).pow(monthCount, new MathContext(LOAN_SCALE, ROUNDING_MODE));

            BigDecimal numerator = monthlyRate.multiply(multiplier);

            BigDecimal denominator = multiplier.subtract(BigDecimal.ONE);

            BigDecimal annuityCoefficient = numerator.divide(denominator, LOAN_SCALE, ROUNDING_MODE);

            rawPayment = totalCredit.multiply(annuityCoefficient).setScale(FINAL_SCALE, ROUNDING_MODE);
        }
        BigDecimal totalAmountWillRefund = rawPayment.multiply(BigDecimal.valueOf(monthCount));

        BigDecimal percentages = totalAmountWillRefund.subtract(totalCredit);

        return new LoanCalculationResponse(rawPayment.setScale(FINAL_SCALE, ROUNDING_MODE), percentages.setScale(FINAL_SCALE, ROUNDING_MODE), totalAmountWillRefund.setScale(FINAL_SCALE, ROUNDING_MODE), annualRate.setScale(FINAL_SCALE, ROUNDING_MODE));
    }
}
