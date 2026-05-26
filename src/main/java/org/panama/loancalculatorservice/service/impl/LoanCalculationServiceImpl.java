package org.panama.loancalculatorservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.panama.loancalculatorservice.constants.StatusService;
import org.panama.loancalculatorservice.dto.request.LoanCalculationRequest;
import org.panama.loancalculatorservice.dto.response.LoanCalculationResponse;
import org.panama.loancalculatorservice.model.LoanApplication;
import org.panama.loancalculatorservice.repository.LoanApplicationRepository;
import org.panama.loancalculatorservice.service.LoanCalculationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanCalculationServiceImpl implements LoanCalculationService {

    private final LoanApplicationRepository repository;

    private static final int FINAL_SCALE = 2;
    private static final int LOAN_SCALE = 10;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    @Override
    @Transactional
    public LoanCalculationResponse calculate(LoanCalculationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null.");
        }

        UUID idempotencyKey = UUID.fromString(request.idempotencyKey());
        Optional<LoanApplication> application = repository.findByIdempotencyKey(idempotencyKey);
        if (application.isPresent()){
            return new LoanCalculationResponse(
                    application.get().getMonthlyPayment(),
                    application.get().getApplicationId().toString(),
                    application.get().getStatus().name(),
                    "Повторный запрос"
            );
        }

        UUID applicationId = UUID.randomUUID();

        BigDecimal totalCredit = request.totalCredit();
        BigDecimal annualRate = request.annualRate();
        int monthCount = request.monthCount();

        BigDecimal monthlyPayment = calculateLoan(totalCredit, monthCount, annualRate);

        repository.save(generateLoanApplication(request, monthlyPayment,idempotencyKey, applicationId));

        return new LoanCalculationResponse(monthlyPayment, applicationId.toString(), StatusService.UNDER_REVIEW.name(), "Заявка принята в обработку");
    }

    private BigDecimal calculateLoan(BigDecimal totalCredit, Integer monthCount, BigDecimal annualRate) {
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

        return rawPayment.setScale(FINAL_SCALE, ROUNDING_MODE);
    }

    private LoanApplication generateLoanApplication(LoanCalculationRequest request,
                                                    BigDecimal rawPayment,
                                                    UUID idempotencyKey,
                                                    UUID applicationId) {
        return new LoanApplication(idempotencyKey,
                applicationId,
                request.totalCredit(),
                request.annualRate(),
                request.monthCount(),
                StatusService.UNDER_REVIEW,
                rawPayment);
    }
}
