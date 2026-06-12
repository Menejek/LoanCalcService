package org.panama.loancalculatorservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.panama.loancalculatorservice.constants.StatusService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false,
    unique = true,
    columnDefinition = "uuid",
    name = "idempotency_key")
    private UUID idempotencyKey;

    @Column(nullable = false,
    columnDefinition = "uuid",
    unique = true,
    updatable = false,
    name = "application_id")
    private UUID applicationId;

    @Column(nullable = false,
    name = "total_credit")
    private BigDecimal totalCredit;

    @Column(nullable = false,
    name = "yearly_interest_rate")
    private BigDecimal yearlyInterestRate;

    @Column(nullable = false,
    name = "monthly_count")
    private Integer monthlyCount;

    @Enumerated(EnumType.STRING)
    private StatusService status;

    @CreationTimestamp
    @Column(nullable = false,
    name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updateAt;

    @Column(nullable = false,
    name = "monthly_payment")
    private BigDecimal monthlyPayment;

    public LoanApplication (UUID idempotencyKey,
                            UUID applicationId,
                            BigDecimal totalCredit,
                            BigDecimal yearlyInterestRate,
                            Integer monthlyCount,
                            StatusService status,
                            BigDecimal monthlyPayment) {
        this.idempotencyKey = idempotencyKey;
        this.applicationId = applicationId;
        this.totalCredit = totalCredit;
        this.yearlyInterestRate = yearlyInterestRate;
        this.monthlyCount = monthlyCount;
        this.status = status;
        this.monthlyPayment = monthlyPayment;
    }
}
