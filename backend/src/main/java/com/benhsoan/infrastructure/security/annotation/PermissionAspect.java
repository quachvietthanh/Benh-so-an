package com.benhsoan.infrastructure.security.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.benhsoan.domain.auth.enums.Permission;
import com.benhsoan.infrastructure.security.service.PermissionEvaluator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final PermissionEvaluator permissionEvaluator;

    @Around("@annotation(checkPermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, CheckPermission checkPermission) throws Throwable {
        Permission[] requiredPermissions = checkPermission.value();
        CheckPermission.Operator operator = checkPermission.operator();

        boolean hasAccess;
        if (operator == CheckPermission.Operator.ALL) {
            hasAccess = permissionEvaluator.hasAllPermissions(requiredPermissions);
        } else {
            hasAccess = permissionEvaluator.hasAnyPermission(requiredPermissions);
        }

        if (!hasAccess) {
            log.warn("Access denied: user lacks required permissions {} (operator={})",
                     requiredPermissions, operator);
            throw new AccessDeniedException("Bạn không có quyền thực hiện thao tác này");
        }

        return joinPoint.proceed();
    }
}
