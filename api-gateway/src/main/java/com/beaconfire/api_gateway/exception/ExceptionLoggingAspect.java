package com.beaconfire.api_gateway.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.ProceedingJoinPoint;


@Aspect
@Component
public class ExceptionLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionLoggingAspect.class);

    @Pointcut("within(com.beaconfire.api_gateway..*)")
    public void gatewayPackagePointcut() {}

    @Around("gatewayPackagePointcut()")
    public Object logExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("Intercepting method: {}.{}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
        try {
            return joinPoint.proceed();
        } catch (Exception ex) {
            logger.error("Exception in {}.{}: {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    ex.getMessage(), ex);
            throw ex;
        }
    }
}

