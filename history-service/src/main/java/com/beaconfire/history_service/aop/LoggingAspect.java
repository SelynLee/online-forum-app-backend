package com.beaconfire.history_service.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(com.beaconfire.history_service.controller..*)")
    public void controllerPointcut() {}

    @Pointcut("within(com.beaconfire.history_service.service..*)")
    public void servicePointcut() {}

    @Around("controllerPointcut() || servicePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.info("==> Starting execution of {}.{}", className, methodName);

        try {
            Object result = joinPoint.proceed();

            log.info("<== Completed execution of {}.{} in {} ms",
                    className,
                    methodName,
                    System.currentTimeMillis() - startTime);

            return result;
        } catch (Exception e) {
            log.error("Exception in {}.{}: {}",
                    className,
                    methodName,
                    e.getMessage(),
                    e);
            throw e;
        }
    }
}
