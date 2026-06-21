package org.panama.loancalculatorservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.panama.loancalculatorservice.model.LoanApplication;
import org.panama.loancalculatorservice.utils.UtilData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RepositoryUnitTests {

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    private final String randomUUID1 = "9fd9c8f3-f447-4bbd-99d7-7d69b5d30ea4";
    private final String randomUUID2 = "b1c19e8d-8931-4a2d-83ba-55275206fd18";

    @BeforeEach
    public void setUp() {
        loanApplicationRepository.deleteAll();
    }

    @Test
    @DisplayName("Тестируем сохранение в БД")
    public void givenLoanApplicationWhenSaveThenApplicationCreated() {
        // given
        LoanApplication loanApplication = UtilData.generateLoanApplication(randomUUID1);
        //when
        LoanApplication savedApplication = loanApplicationRepository.save(loanApplication);
        // then
        assertThat(savedApplication).isNotNull();
        assertThat(savedApplication.getId()).isNotNull();
    }

    @Test
    @DisplayName("Тестируем сохранение в бд с тем же ключом который уже есть в базе")
    public void givenLoanApplicationDuplicateWhenSaveApplicationThenIsExceptionThrown() {
        //given
        LoanApplication loanApplication = UtilData.generateLoanApplication(randomUUID1);
        LoanApplication duplicateApplication = UtilData.generateLoanApplication(randomUUID1);
        //when
        loanApplicationRepository.saveAndFlush(loanApplication);

        //than
        assertThatThrownBy(() ->
                loanApplicationRepository.saveAndFlush(duplicateApplication))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Тестируем успешное получение сущности по idempotencyKey")
    public void givenLoanApplicationIdempotencyKeyWhenSaveApplicationThenReturnedApplication() {
        //given
        LoanApplication loanApplication = UtilData.generateLoanApplication(randomUUID1);
        //when
        loanApplicationRepository.saveAndFlush(loanApplication);
        LoanApplication applicationFindByIdempotencyKey = loanApplicationRepository.findByIdempotencyKey(loanApplication.getIdempotencyKey()).orElse(null);
        //then
        assertThat(applicationFindByIdempotencyKey).isNotNull();
    }


}
