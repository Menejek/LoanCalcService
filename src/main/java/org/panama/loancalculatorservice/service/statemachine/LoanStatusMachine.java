package org.panama.loancalculatorservice.service.statemachine;

import org.panama.loancalculatorservice.constants.LoanTrigger;
import org.panama.loancalculatorservice.constants.StatusService;
import org.springframework.stereotype.Component;

@Component
public class LoanStatusMachine {

    public StatusService transition(StatusService current, LoanTrigger trigger) {
        switch (current) {
            case null -> {
                switch (trigger) {
                    case CREATE -> {
                        return StatusService.UNDER_REVIEW;
                    }
                    default -> throw new IllegalArgumentException("Невозможно создать заявку с триггером: " + trigger);
                }
            }
            case UNDER_REVIEW -> {
                switch (trigger) {
                    case SCORE_APPROVED -> {
                        return StatusService.APPROVED;
                    }
                    case SCORE_REJECTED -> {
                        return StatusService.REJECTED;
                    }
                    case DOCS_REQUESTED ->  {
                        return StatusService.DOCUMENTS_PENDING;
                    }
                    case ESCALATE_MANUAL -> {
                        return StatusService.NEEDS_MANUAL_REVIEW;
                    }
                    default -> throw new IllegalArgumentException("Триггер: " + trigger + " не обработан для статуса UNDER_REVIEW");
                }
            }
            case DOCUMENTS_PENDING -> {
                switch (trigger) {
                    case DOCS_RECEIVED ->  {
                        return StatusService.UNDER_REVIEW;
                    }
                    default -> throw new IllegalArgumentException("Триггер: " + trigger + " не обработан для статуса DOCUMENTS_PENDING");
                }
            }
            case NEEDS_MANUAL_REVIEW -> {
                switch (trigger) {
                    case MANUAL_APPROVE ->  {
                        return StatusService.APPROVED;
                    }
                    case MANUAL_REJECT -> {
                        return StatusService.REJECTED;
                    }
                    default -> throw new IllegalArgumentException("Триггер: " + trigger + " не обработан для статуса NEEDS_MANUAL_REVIEW");
                }
            }
            case APPROVED -> throw new IllegalArgumentException("Заявка одобрена, переходы запрещены");
            case REJECTED -> throw new IllegalArgumentException("Заявка отклонена, переходы запрещены");
            default -> throw new IllegalArgumentException("Неизвестное состояние: " + current);
        }
    }
}
