package Exceptions;

import java.io.IOException;

public class ManagerLoadException extends RuntimeException{
    Throwable throwable;
    public ManagerLoadException(final String message, Throwable throwable) {
        super(message,throwable);

    }


}
