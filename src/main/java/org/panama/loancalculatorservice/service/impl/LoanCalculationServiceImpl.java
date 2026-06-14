package org.panama.loancalculatorservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.panama.loancalculatorservice.constants.LoanTrigger;
import org.panama.loancalculatorservice.dto.LoanCreatedEvent;
import org.panama.loancalculatorservice.dto.request.LoanCalculationRequest;
import org.panama.loancalculatorservice.model.LoanApplication;
import org.panama.loancalculatorservice.model.OutboxEvent;
import org.panama.loancalculatorservice.repository.LoanApplicationRepository;
import org.panama.loancalculatorservice.repository.OutboxEventRepository;
import org.panama.loancalculatorservice.service.LoanCalculationService;
import org.panama.loancalculatorservice.service.statemachine.LoanStatusMachine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanCalculationServiceImpl implements LoanCalculationService {

    private final LoanApplicationRepository repository;
    private final LoanStatusMachine statusMachine;
    private final ObjectMapper objectMapper;
    private final OutboxEventRepository outboxRepository;

    private static final int FINAL_SCALE = 2;
    private static final int LOAN_SCALE = 10;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    @Override
    @Transactional
    public LoanApplication calculate(LoanCalculationRequest request) {
        UUID idempotencyKey = UUID.fromString(request.idempotencyKey());

        UUID applicationId = UUID.randomUUID();

        BigDecimal totalCredit = request.totalCredit();
        BigDecimal annualRate = request.annualRate();
        int monthCount = request.monthCount();

        BigDecimal monthlyPayment = calculateLoan(totalCredit, monthCount, annualRate);
        log.info("Расчет по аннуитетной формуле успешно завершен для заявки с id={}", idempotencyKey);

        LoanApplication applicationNew = generateLoanApplication(request, monthlyPayment, idempotencyKey, applicationId);

        repository.save(applicationNew);
        log.info("Запрос с idempotencyKey={}, applicationId={}, status={} успешно сохранен в базу данных", applicationNew.getIdempotencyKey(), applicationNew.getApplicationId(), applicationNew.getStatus());

        LoanCreatedEvent eventPayload = new LoanCreatedEvent(
                applicationNew.getApplicationId(),
                applicationNew.getIdempotencyKey(),
                applicationNew.getTotalCredit(),
                applicationNew.getYearlyInterestRate(),
                applicationNew.getMonthlyCount(),
                applicationNew.getMonthlyPayment(),
                applicationNew.getStatus().name()
        );
        try {
            String payloadJson = objectMapper.writeValueAsString(eventPayload);
            outboxRepository.save(new OutboxEvent(applicationNew.getApplicationId(),
                    "LOAN_CREATED",
                    payloadJson,
                    "PENDING",
                    LocalDateTime.now()));
        } catch (JsonProcessingException e) {
            log.error("Событие с idempotencyKey={}, applicationId={}, status={} не преобразовано в JSON", applicationNew.getApplicationId(), applicationNew.getIdempotencyKey(), applicationNew.getStatus(), e.getMessage());
            throw new RuntimeException(e);
        }

        return applicationNew;
    }

    private BigDecimal calculateLoan(BigDecimal totalCredit, Integer monthCount, BigDecimal annualRate) {
        BigDecimal rawPayment;

        BigDecimal monthlyRate = annualRate.divide(new BigDecimal("100"), LOAN_SCALE, ROUNDING_MODE).divide(new BigDecimal("12"), LOAN_SCALE, ROUNDING_MODE);

        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            log.debug("Ежемесячная ставка по кредиту равна 0");
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
                statusMachine.transition(null, LoanTrigger.CREATE),
                rawPayment);
    }
}