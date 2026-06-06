package org.panama.loancalculatorservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.panama.loancalculatorservice.dto.ApplicationDto;
import org.panama.loancalculatorservice.dto.request.LoanCalculationRequest;
import org.panama.loancalculatorservice.dto.response.LoanCalculationResponse;
import org.panama.loancalculatorservice.model.LoanApplication;
import org.panama.loancalculatorservice.repository.LoanApplicationRepository;
import org.panama.loancalculatorservice.service.LoanCalculationService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CacheLoanCalcService {

    private final LoanApplicationRepository repository;
    private final LoanCalculationService loanCalculationService;
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final ApplicationDto applicationDto;

    private final static String CACHE_KEY_PREFIX = "user:application:";
    private static final Duration CACHE_TTL = Duration.ofHours(24);

    public LoanCalculationResponse calculate(LoanCalculationRequest request) {
        if (request == null) {
            log.error("Получен невалидный запрос: {}", request);
            throw new IllegalArgumentException("Request cannot be null.");
        }

        UUID idempotencyKey = UUID.fromString(request.idempotencyKey());
        log.debug("Начало обработки заявки с ключом: {}", idempotencyKey);

        String applicationJSON = getFromCache(idempotencyKey);
        if (applicationJSON != null) {
            try {
                LoanApplication applicationCache = objectMapper.readValue(applicationJSON, LoanApplication.class);
                log.info("Заявка с idempotencyKey={} найдена в Redis",  idempotencyKey);
                return applicationDto.toResponse(applicationCache, "Заявка уже есть в базе данных");
            } catch (JsonProcessingException e) {
                log.warn("Не удалось десериализовать кэш для idempotencyKey={}", idempotencyKey);
            }
        }

        LoanApplication application = repository.findByIdempotencyKey(idempotencyKey).orElse(null);
        if (application != null) {
            log.info("Запрос с idempotencyKey={}, applicationId={}, status={} уже есть в базе", application.getIdempotencyKey(), application.getApplicationId(), application.getStatus());
            saveToCache(idempotencyKey, application);
            return applicationDto.toResponse(application,
                    "Повторный запрос"
            );
        }

        LoanApplication applicationNew = loanCalculationService.calculate(request);
        saveToCache(idempotencyKey, applicationNew);

        return applicationDto.toResponse(applicationNew,
                "Заявка принята в обработку");
    }

    private String getFromCache(UUID idempotencyKey) {
        try {
            return redis.opsForValue().get(CACHE_KEY_PREFIX + idempotencyKey);
        } catch (Exception e) {
            log.warn("Ошибка чтения из Redis, продолжаем без кеша", e);
            return null;
        }
    }

    private void saveToCache(UUID idempotencyKey, LoanApplication application) {
        try {
            String json = objectMapper.writeValueAsString(application);
            redis.opsForValue().set(CACHE_KEY_PREFIX + idempotencyKey, json, CACHE_TTL);
            log.debug("Заявка с idempotencyKey={} сохранена в Redis c TTL={}", idempotencyKey, CACHE_TTL);
        } catch(JsonProcessingException e) {
            log.error("Не удалось сериализовать заявку для кеша", e);
        } catch(Exception e) {
            log.warn("Ошибка записи в Redis, заявка сохранена только в БД", e);
        }
    }
}
