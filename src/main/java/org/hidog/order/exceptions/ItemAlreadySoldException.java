package org.hidog.order.exceptions;

import org.hidog.global.exceptions.script.AlertBackException;
import org.springframework.http.HttpStatus;

public class ItemAlreadySoldException extends AlertBackException {
    public ItemAlreadySoldException() {
        super("AlreadySold.order", HttpStatus.NOT_FOUND);
        setErrorCode(true);
    }
}
