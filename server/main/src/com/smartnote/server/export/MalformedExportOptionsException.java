package com.smartnote.server.export;

public class MalformedExportOptionsException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public MalformedExportOptionsException(String message) {
        super(message);
    }
    
    public MalformedExportOptionsException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public MalformedExportOptionsException(Throwable cause) {
        super(cause);
    }
    
    public MalformedExportOptionsException() {
        super();
    }
}
