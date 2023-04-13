package exceptions;

public class ManagerLoadException extends RuntimeException{
    Throwable throwable;
    public ManagerLoadException(final String message, Throwable throwable) {
        super(message,throwable);

    }


}
