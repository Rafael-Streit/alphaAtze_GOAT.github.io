package org.systeminfo.systeminfoapi.exception;

public class InvalidProcessRequestException extends RuntimeException {
    public InvalidProcessRequestException(String message) {
        super(message);
    }

    public InvalidProcessRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
