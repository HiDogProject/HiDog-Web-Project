package org.hidog.order.exceptions;

import org.hidog.global.exceptions.script.AlertBackException;
import org.springframework.http.HttpStatus;

public class BuyerSellerConflictError extends AlertBackException {

    public BuyerSellerConflictError() {
        super("BuyerSellerConflict.order", HttpStatus.BAD_REQUEST);
        setErrorCode(true);
    }
}
