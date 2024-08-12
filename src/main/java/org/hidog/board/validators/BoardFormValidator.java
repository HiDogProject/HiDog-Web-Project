package org.hidog.board.validators;

import lombok.RequiredArgsConstructor;
import org.hidog.board.controllers.RequestBoard;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class BoardFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(RequestBoard.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        //RequestBoard form = (RequestBoard) target;

        if (errors.hasErrors()) {
            return;
        }
    }
}
