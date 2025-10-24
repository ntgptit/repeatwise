package com.repeatwise.log.aspect;

import com.repeatwise.log.annotation.PerformanceLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect for performance monitoring using @PerformanceLog annotation.
 * Tracks execution time and logs warnings/errors when thresholds are exceeded.
 */
@Aspect
@Component
@Slf4j
public class PerformanceAspect {

    /**
     * Pointcut for methods annotated with @PerformanceLog.
     */
    @Pointcut("@annotation(com.repeatwise.log.annotation.PerformanceLog)")
    public void performanceLogMethods() {}

    /**
     * Pointcut for classes annotated with @PerformanceLog.
     */
    @Pointcut("@within(com.repeatwise.log.annotation.PerformanceLog)")
    public void performanceLogClasses() {}

    /**
     * Around advice for @PerformanceLog methods.
     */
    @Around("performanceLogMethods() || performanceLogClasses()")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        PerformanceLog perfLog = signature.getMethod().getAnnotation(PerformanceLog.class);

        // If method doesn't have @PerformanceLog, try class level
        if (perfLog == null) {
            perfLog = signature.getMethod().getDeclaringClass().getAnnotation(PerformanceLog.class);
        }

        if (perfLog == null) {
            return joinPoint.proceed();
        }

        String methodName = signature.getName();
        String className = signature.getDeclaringClass().getSimpleName();
        String operation = perfLog.operation().isEmpty()
            ? className + "." + methodName
            : perfLog.operation();

        long startTime = System.nanoTime();
        boolean success = true;
        Throwable thrownException = null;

        try {
            Object result = joinPoint.proceed();
            return result;

        } catch (Throwable e) {
            success = false;
            thrownException = e;
            throw e;

        } finally {
            long endTime = System.nanoTime();
            long executionTimeMs = (endTime - startTime) / 1_000_000;

            logPerformance(operation, executionTimeMs, success, thrownException,
                perfLog, joinPoint.getArgs());
        }
    }

    /**
     * Log performance metrics based on thresholds.
     */
    private void logPerformance(String operation, long executionTimeMs, boolean success,
                               Throwable exception, PerformanceLog perfLog, Object[] args) {
        StringBuilder msg = new StringBuilder();
        msg.append("[PERFORMANCE] operation=").append(operation)
           .append(" | executionTime=").append(executionTimeMs).append("ms")
           .append(" | success=").append(success);

        if (perfLog.logArgs() && args != null && args.length > 0) {
            msg.append(" | args=").append(Arrays.toString(args));
        }

        if (exception != null) {
            msg.append(" | exception=").append(exception.getClass().getSimpleName())
               .append(":").append(exception.getMessage());
        }

        // Determine log level based on execution time and thresholds
        if (executionTimeMs >= perfLog.errorThresholdMs()) {
            msg.append(" | status=CRITICAL - Exceeded error threshold (")
               .append(perfLog.errorThresholdMs()).append("ms)");
            log.error(msg.toString());

        } else if (executionTimeMs >= perfLog.warnThresholdMs()) {
            msg.append(" | status=SLOW - Exceeded warn threshold (")
               .append(perfLog.warnThresholdMs()).append("ms)");
            log.warn(msg.toString());

        } else if (perfLog.alwaysLog()) {
            msg.append(" | status=OK");
            log.info(msg.toString());
        }
        // If not alwaysLog and below warn threshold, don't log (for performance)
    }

    /**
     * Monitor all repository methods for slow database queries.
     */
    @Around("execution(* com.repeatwise.repository..*(..))")
    public Object monitorRepositoryPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringClass().getSimpleName();

        long startTime = System.nanoTime();

        try {
            return joinPoint.proceed();

        } finally {
            long endTime = System.nanoTime();
            long executionTimeMs = (endTime - startTime) / 1_000_000;

            // Log slow queries (>100ms is considered slow for DB operations)
            if (executionTimeMs > 100) {
                log.warn("[PERFORMANCE] [SLOW_QUERY] repository={} | method={} | executionTime={}ms",
                    className, methodName, executionTimeMs);
            }
        }
    }

    /**
     * Monitor all controller methods for slow HTTP requests.
     */
    @Around("execution(public * com.repeatwise.controller..*(..))")
    public Object monitorControllerPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringClass().getSimpleName();

        long startTime = System.nanoTime();
        boolean success = true;

        try {
            Object result = joinPoint.proceed();
            return result;

        } catch (Throwable e) {
            success = false;
            throw e;

        } finally {
            long endTime = System.nanoTime();
            long executionTimeMs = (endTime - startTime) / 1_000_000;

            // Log slow HTTP requests (>1000ms)
            if (executionTimeMs > 1000) {
                log.warn("[PERFORMANCE] [SLOW_REQUEST] controller={} | endpoint={} | executionTime={}ms | success={}",
                    className, methodName, executionTimeMs, success);
            } else if (executionTimeMs > 500) {
                log.info("[PERFORMANCE] [REQUEST] controller={} | endpoint={} | executionTime={}ms | success={}",
                    className, methodName, executionTimeMs, success);
            }
        }
    }
}
