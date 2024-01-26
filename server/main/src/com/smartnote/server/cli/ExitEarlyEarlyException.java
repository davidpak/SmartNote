package com.smartnote.server.cli;

public class ExitEarlyEarlyException extends RuntimeException {
    private int code;

    public ExitEarlyEarlyException(int code) {
        super();
        this.code = code;    
    }

    public ExitEarlyEarlyException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
