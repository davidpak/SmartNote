package com.smartnote.server.export;

public class ExportServiceTimeoutException extends ExportException {
    private static final long serialVersionUID = 1L;

    public ExportServiceTimeoutException() {
        super();
    }

    public ExportServiceTimeoutException(String message) {
        super(message);
    }

    public ExportServiceTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExportServiceTimeoutException(Throwable cause) {
        super(cause);
    }
}
