package com.skipper.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("serial")
public class InternalException extends RuntimeException {
    private ErrorCodes code;
    private String message;
    private Throwable cause;

    public InternalException(String message, Throwable cause, ErrorCodes code) {
        this.message = message;
        this.cause = cause;
        this.code = code;
    }

    public InternalException(String message, ErrorCodes code) {
        this.message = message;
        this.code = code;
    }

    public InternalException(Throwable cause, ErrorCodes code) {
        this.cause = cause;
        this.code = code;
    }

    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(code.getCode());
        sb.append(": ");
        sb.append(this.message);
        return sb.toString();
    }

    public int getIntCode() {
        return Integer.parseInt(this.code.getCode());
    }
}
