package com.skipper.exception;

public enum ErrorCodes {
    /*
     * Common Errors
     */
    COM_001("1001", "An unknown error has occurred in the system."),
    COM_002("1002", "STB API error"),
    COM_003("1003", "Google API error");


    private final String code;
    private final String description;

    private ErrorCodes(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }

    public int getIntCode() {
        return Integer.parseInt(code);
    }

    @Override
    public String toString() {
        return code + ": " + description;
    }
}