package com.beaconfire.posts_service.exception;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PostsServiceExceptionAspect {

    private static final Logger logger = LoggerFactory.getLogger(PostsServiceExceptionAspect.class);

    @Pointcut("within(com.beaconfire.posts_service..*)")
    public void postsServicePointcut() {}

    @Around("postsServicePointcut()")
    public Object handlePostsServiceExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception ex) {
            logger.error("Posts-Service Exception in {}.{}: {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    ex.getMessage(), ex);
            throw new RuntimeException("Error occurred while processing a post request.");
        }
    }
}
