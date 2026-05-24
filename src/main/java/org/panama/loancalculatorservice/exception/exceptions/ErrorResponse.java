package org.panama.loancalculatorservice.exception.exceptions;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public final class ErrorResponse {
    private final LocalDateTime time;
    private final int status;
    private final String message;
    private final String path;
    private final List<String> details;

    public List<String> getDetails() {
        return details;
    }

    public String getPath() {
        return path;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
