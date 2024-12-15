package com.beaconfire.posts_service.aop;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExceptionLoggingAspect {

    @AfterThrowing(pointcut = "execution(* com.beaconfire.posts_service.controller..*(..))", throwing = "ex")
    public void logException(Exception ex) {
        System.err.println("Exception caught in controller: " + ex.getMessage());
        
    }
}
