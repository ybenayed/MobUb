package com.smartcampus.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Contrainte de validation pour un mot de passe "robuste" :
 * - au moins 8 caracteres
 * - au moins une lettre majuscule
 * - au moins un chiffre
 * - au moins un caractere special
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
@Documented
public @interface ValidPassword {

    String message() default "Le mot de passe doit contenir au moins 8 caracteres, "
            + "une majuscule, un chiffre et un caractere special";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}