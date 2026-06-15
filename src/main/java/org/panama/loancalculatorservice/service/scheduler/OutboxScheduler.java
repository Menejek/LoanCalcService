package org.panama.loancalculatorservice.service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.panama.loancalculatorservice.model.OutboxEvent;
import org.panama.loancalculatorservice.repository.OutboxEventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxEventProcessor outboxEventService;

    @Scheduled(fixedDelay = 10000)
    public void run() {
            log.info("Началось вычитывание из таблицы outbox");
            outboxEventService.processAllOutboxEvents();
            log.info("Закончилось вычитывание из таблицы outbox");
    }
}