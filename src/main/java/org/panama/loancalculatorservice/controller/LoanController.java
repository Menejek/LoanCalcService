package org.panama.loancalculatorservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.panama.loancalculatorservice.dto.request.LoanCalculationRequest;
import org.panama.loancalculatorservice.dto.response.LoanCalculationResponse;
import org.panama.loancalculatorservice.service.LoanCalculationService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.panama.loancalculatorservice.constants.ApiConstant.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(LOAN_API_CONTROLLER)
public class LoanController {

    private final LoanCalculationService loanCalculationService;

    @PostMapping(value = LOAN_POST_CALCULATION,
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public LoanCalculationResponse calculate(@RequestBody @Valid LoanCalculationRequest request){
        log.debug("Запрос с idempotencyKey={} получен", request.idempotencyKey());
        return loanCalculationService.calculate(request);
    }
}
