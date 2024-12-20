package com.beaconfire.auth_service.exception;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthServiceExceptionAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceExceptionAspect.class);

    @Pointcut("within(com.beaconfire.auth_service..*)")
    public void authServicePointcut() {}

    @Around("authServicePointcut()")
    public Object handleAuthServiceExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception ex) {
            logger.error("Auth-Service Exception in {}.{}: {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    ex.getMessage(), ex);
            throw new RuntimeException("Authentication or permission error occurred.");
        }
    }
}
