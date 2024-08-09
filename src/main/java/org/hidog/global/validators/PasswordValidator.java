package org.hidog.global.validators;

public interface PasswordValidator {

    default boolean alphaCheck(String password) {
        return password.matches(".*[a-zA-Z]+.*");
    }

    default boolean numberCheck(String password) {
        return password.matches(".*\\d+.*");
    }

    default boolean specialCharsCheck(String password) {
        String pattern = ".*[^0-9a-zA-Zㄱ-ㅎ가-힣]+.*";
        return password.matches(pattern);
    }
}
