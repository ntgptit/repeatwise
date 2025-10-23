package com.repeatwise.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect for automatic logging using @Loggable annotation.
 * Provides method entry/exit logging, exception logging, and execution time tracking.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * Pointcut for methods annotated with @Loggable.
     */
    @Pointcut("@annotation(com.repeatwise.log.Loggable)")
    public void loggableMethods() {}

    /**
     * Pointcut for classes annotated with @Loggable.
     */
    @Pointcut("@within(com.repeatwise.log.Loggable)")
    public void loggableClasses() {}

    /**
     * Around advice for @Loggable methods.
     */
    @Around("loggableMethods() || loggableClasses()")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Loggable loggable = signature.getMethod().getAnnotation(Loggable.class);

        // If method doesn't have @Loggable, try class level
        if (loggable == null) {
            loggable = signature.getMethod().getDeclaringClass().getAnnotation(Loggable.class);
        }

        if (loggable == null) {
            return joinPoint.proceed();
        }

        String methodName = signature.getName();
        String className = signature.getDeclaringClass().getSimpleName();
        LogEvent event = loggable.event();
        LogLevel level = loggable.level();

        long startTime = System.currentTimeMillis();

        // Log method entry
        logEntry(className, methodName, event, level, loggable, joinPoint.getArgs());

        try {
            Object result = joinPoint.proceed();

            // Log successful exit
            long executionTime = System.currentTimeMillis() - startTime;
            logExit(className, methodName, event, level, loggable, result, executionTime);

            return result;

        } catch (Exception e) {
            // Log exception
            long executionTime = System.currentTimeMillis() - startTime;
            logException(className, methodName, event, e, executionTime);
            throw e;
        }
    }

    /**
     * Log method entry.
     */
    private void logEntry(String className, String methodName, LogEvent event,
                         LogLevel level, Loggable loggable, Object[] args) {
        StringBuilder msg = new StringBuilder();
        msg.append("[").append(event).append("]")
           .append(" ").append(className).append(".").append(methodName)
           .append(" - ENTRY");

        if (loggable.logArgs() && args != null && args.length > 0) {
            msg.append(" | args=").append(Arrays.toString(args));
        }

        if (!loggable.message().isEmpty()) {
            msg.append(" | ").append(loggable.message());
        }

        logAtLevel(level, msg.toString());
    }

    /**
     * Log method exit.
     */
    private void logExit(String className, String methodName, LogEvent event,
                        LogLevel level, Loggable loggable, Object result, long executionTime) {
        StringBuilder msg = new StringBuilder();
        msg.append("[").append(event).append("]")
           .append(" ").append(className).append(".").append(methodName)
           .append(" - EXIT");

        if (loggable.logResult() && result != null) {
            msg.append(" | result=").append(result);
        }

        if (loggable.logExecutionTime()) {
            msg.append(" | executionTime=").append(executionTime).append("ms");
        }

        logAtLevel(level, msg.toString());
    }

    /**
     * Log exception.
     */
    private void logException(String className, String methodName, LogEvent event,
                             Exception e, long executionTime) {
        String msg = String.format("[%s] %s.%s - EXCEPTION | type=%s | message=%s | executionTime=%dms",
            event, className, methodName,
            e.getClass().getSimpleName(), e.getMessage(), executionTime);

        log.error(msg, e);
    }

    /**
     * Log at the specified level.
     */
    private void logAtLevel(LogLevel level, String message) {
        switch (level) {
            case TRACE -> log.trace(message);
            case DEBUG -> log.debug(message);
            case INFO -> log.info(message);
            case WARN -> log.warn(message);
            case ERROR -> log.error(message);
        }
    }

    /**
     * After throwing advice for all public methods in service layer.
     * Provides fallback exception logging for methods without @Loggable.
     */
    @AfterThrowing(
        pointcut = "execution(public * com.repeatwise.service..*(..))",
        throwing = "exception"
    )
    public void logServiceException(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringClass().getSimpleName();

        log.error("[EX_INTERNAL_SERVER] {}.{} - Unhandled exception: {}",
            className, methodName, exception.getMessage(), exception);
    }

    /**
     * After throwing advice for all public methods in controller layer.
     * Provides fallback exception logging for controllers without @Loggable.
     */
    @AfterThrowing(
        pointcut = "execution(public * com.repeatwise.controller..*(..))",
        throwing = "exception"
    )
    public void logControllerException(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringClass().getSimpleName();

        log.error("[EX_INTERNAL_SERVER] {}.{} - Controller exception: {}",
            className, methodName, exception.getMessage(), exception);
    }
}
