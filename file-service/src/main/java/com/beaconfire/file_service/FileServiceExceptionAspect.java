package com.beaconfire.file_service;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FileServiceExceptionAspect {
    private static final Logger logger = LoggerFactory.getLogger(FileServiceExceptionAspect.class);

    @Pointcut("within(com.beaconfire.file_service..*)")
    public void fileServicePointcut() {}

    @Around("fileServicePointcut()")
    public Object handleFileServiceExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception ex) {
            logger.error("File-Service Exception in {}.{}: {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    ex.getMessage(), ex);
            throw new RuntimeException("An error occurred while processing the file request.");
        }
    }
}
