package org.panama.loancalculatorservice.constants;

/***
 * create - заявка впервые валидно принята
 * score_approved - скоринговая модель выдала положительное решение
 * score_rejected - скоринг отклонил по правилам и лимитам
 * docs_requested - не хватает файлов или они не прошли проверку
 * docs_received - все обязательные документы загружены и валидны
 * escalate_manual - автоматика не может принять решение
 * manual_approve - оператор вручную одобрил заявку
 * manual_reject - оператор вручную отклонил заявку
 */
public enum LoanTrigger {
    CREATE,
    SCORE_APPROVED,
    SCORE_REJECTED,
    DOCS_REQUESTED,
    DOCS_RECEIVED,
    ESCALATE_MANUAL,
    MANUAL_APPROVE,
    MANUAL_REJECT
}
