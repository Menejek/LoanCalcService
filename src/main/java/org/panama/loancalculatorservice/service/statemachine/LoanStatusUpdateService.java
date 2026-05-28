package org.panama.loancalculatorservice.service.statemachine;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.panama.loancalculatorservice.constants.LoanTrigger;
import org.panama.loancalculatorservice.constants.StatusService;
import org.panama.loancalculatorservice.exception.exceptions.LoanApplicationNotFoundException;
import org.panama.loancalculatorservice.model.LoanApplication;
import org.panama.loancalculatorservice.repository.LoanApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanStatusUpdateService {

    private final LoanApplicationRepository repository;
    private final LoanStatusMachine statusMachine;

    @Transactional
    public StatusService applyTrigger(UUID applicationId, LoanTrigger trigger) {
        LoanApplication application = repository.findByApplicationId(applicationId).orElseThrow(() ->
        new LoanApplicationNotFoundException("Заявка с applicationId = " + applicationId + " не найдена"));

        StatusService newStat = statusMachine.transition(application.getStatus(), trigger);

        application.setStatus(newStat);
        return newStat;
    }
}
