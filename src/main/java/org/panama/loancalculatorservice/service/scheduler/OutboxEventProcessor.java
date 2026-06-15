package org.panama.loancalculatorservice.service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.panama.loancalculatorservice.model.OutboxEvent;
import org.panama.loancalculatorservice.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxEventProcessor {

    private final OutboxEventRepository repository;
    private final TransactionTemplate transactionTemplate;

    private final static String PENDING_STATUS = "PENDING";
    private final static String SENT_STATUS = "SENT";
    private final static String FAILED_STATUS = "FAILED";
    private final static Integer MAX_RETRY_COUNT = 5;

    public void processAllOutboxEvents() {
        List<OutboxEvent> events = repository.findByStatusOrderByCreatedAtAsc(PENDING_STATUS);
        for (OutboxEvent event : events) {
            transactionTemplate.execute(status -> {
                processOutboxEvent(event);
                return null;
            });
        }
    }

    public void processOutboxEvent(OutboxEvent event) {
        try {
            log.info("Отправка в Kafka события с aggregateId={}", event.getAggregateId());
            event.setStatus(SENT_STATUS);
            event.setRetryCount(0);
            event.setErrorMessage(null);
        } catch (Exception e) {
            int newRetryCount = event.getRetryCount() + 1;
            event.setRetryCount(newRetryCount);
            event.setErrorMessage(e.getMessage());

            if (newRetryCount >= MAX_RETRY_COUNT) {
                event.setStatus(FAILED_STATUS);
                log.error("Превышено количество retry для события с aggregateId={}", event.getAggregateId());
            } else {
                log.error("Отправка в Kafka прошла неудачно для события с aggregateId={}, осталось попыток={}", event.getAggregateId(), MAX_RETRY_COUNT - newRetryCount, e);
            }
        }
        repository.save(event);
    }
}
