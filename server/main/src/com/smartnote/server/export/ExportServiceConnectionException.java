package com.smartnote.server.export;

public class ExportServiceConnectionException extends ExportException {
    private static final long serialVersionUID = 1L;

    public ExportServiceConnectionException() {
        super();
    }

    public ExportServiceConnectionException(String message) {
        super(message);
    }

    public ExportServiceConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExportServiceConnectionException(Throwable cause) {
        super(cause);
    }
}