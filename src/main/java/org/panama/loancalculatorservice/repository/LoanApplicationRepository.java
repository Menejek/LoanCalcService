package org.panama.loancalculatorservice.repository;

import org.panama.loancalculatorservice.model.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, UUID> {

    Optional<LoanApplication> findByIdempotencyKey(UUID idempotencyKey);

    Optional<LoanApplication> findByApplicationId(UUID applicationId);
}
