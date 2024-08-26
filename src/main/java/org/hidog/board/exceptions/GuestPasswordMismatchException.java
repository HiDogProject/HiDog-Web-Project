package org.hidog.board.exceptions;

import org.hidog.global.exceptions.script.AlertException;
import org.springframework.http.HttpStatus;

public class GuestPasswordMismatchException extends AlertException {
    public GuestPasswordMismatchException() {
        super("Mismatch.password", HttpStatus.BAD_REQUEST);
        setErrorCode(true);
    }
}
