package org.panama.loancalculatorservice.exception;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.panama.loancalculatorservice.exception.exceptions.ErrorResponse;
import org.panama.loancalculatorservice.exception.exceptions.LoanApplicationNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Ошибка в клиентском запросе. Вызвана некорректным JSON, нарушением типов данных или не прохождением границ валидации"),
        @ApiResponse(responseCode = "401", description = "Аутентификация не пройдена. ТОкен отсутствует, просрочен или имеет невалидную подпись"),
        @ApiResponse(responseCode = "403", description = "Аутентификация пройдена, но текущая роль пользователя не предоставляет прав не выполнение операции"),
        @ApiResponse(responseCode = "405", description = "HTTP-метод не поддерживается ждя указанного эндпоинта. Разрешенные методы указаны в заголовке Allow"),
        @ApiResponse(responseCode = "415", description = "Тип содержимого запроса не поддерживается. Ожидается формат application/JSON"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера. Инцидент зафиксирован в логах приложения. Рекомендуется повторить запрос позже")
})
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream().
                map(e -> e.getField() + ": " + (e.getDefaultMessage() != null ? e.getDefaultMessage() : "This field is not validated")).toList();
        ErrorResponse errorResponse = ErrorResponse.builder().
                time(LocalDateTime.now()).
                status(HttpStatus.BAD_REQUEST.value()).
                message("Validation failed").
                path(request.getRequestURI()).
                details(errors).
                build();
        log.warn("Клиент прислал невалидные данные. Метод={}, Путь={}, Поля={}",
                request.getMethod(),
                request.getRequestURI(),
                errors,
                ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder().
                time(LocalDateTime.now()).
                status(HttpStatus.BAD_REQUEST.value()).
                message("Invalid request body").
                path(request.getRequestURI()).
                build();
        log.warn("Сервер не смог прочитать или распознать тело запроса. Метод={}, Путь={}",
                request.getMethod(),
                request.getRequestURI(),
                ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder().
                time(LocalDateTime.now()).
                status(HttpStatus.BAD_REQUEST.value()).
                message(ex.getMessage()).
                path(request.getRequestURI()).
                build();
        log.warn("Клиент прислал некорректные данные в запросе. Метод={}, Путь={}",
                request.getMethod(),
                request.getRequestURI(),
                ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder().
                time(LocalDateTime.now()).
                status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .message("Unsupported media type").
                path(request.getRequestURI()).
                build();
        log.warn("Несовпадение типов данных в запросе и ожидаемых сервером. Метод={}, Путь={}",
                request.getMethod(),
                request.getRequestURI(),
                ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder().
                time(LocalDateTime.now()).
                status(HttpStatus.METHOD_NOT_ALLOWED.value()).
                message("Method not allowed").
                path(request.getRequestURI()).
                build();
        log.warn("Выбран неподходящий HTTP-метод для выполнения текущего запроса. Метод={}, Путь={}",
                request.getMethod(),
                request.getRequestURI(),
                ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder().
                time(LocalDateTime.now()).
                status(HttpStatus.INTERNAL_SERVER_ERROR.value()).
                message("Internal server error").
                path(request.getRequestURI()).
                build();
        log.error("Uncaught exception",
                request.getMethod(),
                request.getRequestURI(),
                ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(LoanApplicationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLoanApplicationNotFoundException(LoanApplicationNotFoundException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder().
                time(LocalDateTime.now()).
                status(HttpStatus.NOT_FOUND.value()).
                message("Application not found").
                path(request.getRequestURI()).
                build();
        log.warn("Заявка на кредит не найдена в базе данных. Метод={}, Путь={}",
                request.getMethod(),
                request.getRequestURI(),
                ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
