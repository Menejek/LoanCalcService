package org.panama.loancalculatorservice.exception.exceptions;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public class ErrorResponse {
    private LocalDateTime time;
    private int status;
    private String message;
    private String path;
    private List<String> details;

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
