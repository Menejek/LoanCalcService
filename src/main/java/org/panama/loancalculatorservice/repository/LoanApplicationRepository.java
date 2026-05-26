package org.panama.loancalculatorservice.repository;

import org.panama.loancalculatorservice.model.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LoanApplicationRepository extends JpaRepository {
    Optional<LoanApplication> findByIdempotencyKey(UUID idempotencyKey);
}
