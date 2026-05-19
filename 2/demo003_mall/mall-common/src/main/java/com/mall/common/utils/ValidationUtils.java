package com.mall.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

/**
 * Validation utilities
 */
@Slf4j
public class ValidationUtils {

    public static String buildErrorMessage(BindingResult bindingResult) {
        if (bindingResult == null || !bindingResult.hasErrors()) {
            return "Validation failed";
        }
        StringBuilder sb = new StringBuilder();
        List<FieldError> errors = bindingResult.getFieldErrors();
        for (int i = 0; i < errors.size(); i++) {
            FieldError error = errors.get(i);
            sb.append(error.getField()).append(": ").append(error.getDefaultMessage());
            if (i < errors.size() - 1) {
                sb.append("; ");
            }
        }
        return sb.toString();
    }
}
