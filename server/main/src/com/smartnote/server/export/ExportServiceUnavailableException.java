package com.smartnote.server.export;

public class ExportServiceUnavailableException extends ExportException {
    private static final long serialVersionUID = 1L;

    public ExportServiceUnavailableException() {
        super();
    }

    public ExportServiceUnavailableException(String message) {
        super(message);
    }

    public ExportServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExportServiceUnavailableException(Throwable cause) {
        super(cause);
    }
}
