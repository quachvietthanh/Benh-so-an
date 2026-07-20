// package com.benhsoan.infrastructure.security.annotation;

// import com.benhsoan.domain.auth.Permission;

// import java.lang.annotation.ElementType;
// import java.lang.annotation.Retention;
// import java.lang.annotation.RetentionPolicy;
// import java.lang.annotation.Target;

// @Target({ElementType.METHOD, ElementType.TYPE})
// @Retention(RetentionPolicy.RUNTIME)
// public @interface CheckPermission {

//     Permission[] value() default {};

//     Operator operator() default Operator.ANY;

//     enum Operator {
//         ANY,
//         ALL
//     }
// }
