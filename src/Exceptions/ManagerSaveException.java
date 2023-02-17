package Exceptions;

import java.io.IOException;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(final String message, Throwable throwable) {
        super(message, throwable);

    }


}
