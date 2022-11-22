package it.multicoredev.client.exceptions;

public class GraphicException extends IllegalStateException {

    public GraphicException() {
        super();
    }

    public GraphicException(String s) {
        super(s);
    }

    public GraphicException(String message, Throwable cause) {
        super(message, cause);
    }

    public GraphicException(Throwable cause) {
        super(cause);
    }
}
