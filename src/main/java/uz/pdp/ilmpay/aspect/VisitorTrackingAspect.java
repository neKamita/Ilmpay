package uz.pdp.ilmpay.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import uz.pdp.ilmpay.service.VisitorService;

@Aspect
@Component
@RequiredArgsConstructor
public class VisitorTrackingAspect {
    private final VisitorService visitorService;

    @AfterReturning("@annotation(org.springframework.web.bind.annotation.GetMapping) && execution(* uz.pdp.ilmpay.Controller.*.*(..))")
    public void trackPageVisit(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        visitorService.recordVisit(methodName);
    }
} 