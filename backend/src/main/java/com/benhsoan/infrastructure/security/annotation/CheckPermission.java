package com.benhsoan.infrastructure.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.benhsoan.domain.auth.enums.Permission;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckPermission {

    Permission[] value() default {};

    Operator operator() default Operator.ANY;

    enum Operator {
        ANY,
        ALL
    }
}
