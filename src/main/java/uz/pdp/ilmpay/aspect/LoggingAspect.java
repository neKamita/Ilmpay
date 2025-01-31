package uz.pdp.ilmpay.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // 🎭 Aspect for logging controller methods
    @Around("execution(* uz.pdp.ilmpay.Controller.*.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        // 📝 Log the request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        log.info("🚀 Request -> {} {} | Class: {} | Method: {} | Args: {}", 
            request.getMethod(), 
            request.getRequestURI(),
            className,
            methodName,
            Arrays.toString(joinPoint.getArgs())
        );

        try {
            // 🎯 Execute the method
            Object result = joinPoint.proceed();
            
            // 📝 Log the response
            long executionTime = System.currentTimeMillis() - start;
            log.info("✅ Response -> {} {} | Time: {}ms | Result: {}", 
                request.getMethod(), 
                request.getRequestURI(),
                executionTime,
                result
            );
            
            return result;
        } catch (Exception e) {
            // 💥 Log any errors
            log.error("❌ Error in {} {} | Class: {} | Method: {} | Error: {}", 
                request.getMethod(), 
                request.getRequestURI(),
                className,
                methodName,
                e.getMessage()
            );
            throw e;
        }
    }
}
