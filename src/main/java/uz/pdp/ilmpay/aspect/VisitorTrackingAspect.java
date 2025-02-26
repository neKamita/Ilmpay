package uz.pdp.ilmpay.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import uz.pdp.ilmpay.service.VisitorService;

/**
 * 🕵️ Visitor Tracking Aspect
 * Now with smarter tracking - only cares about the homepage!
 * 
 * @author Your Friendly Analytics Engineer
 * @version 2.0 (The "Selective Tracker" Edition)
 */
@Aspect
@Component
public class VisitorTrackingAspect {
    private final VisitorService visitorService;

    public VisitorTrackingAspect(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    @AfterReturning(value = "@annotation(mapping) && execution(* uz.pdp.ilmpay.Controller.*.*(..))")
    public void trackPageVisit(JoinPoint joinPoint, GetMapping mapping) {
        String path = mapping.value().length > 0 ? mapping.value()[0] : "";
        
        // Skip tracking for admin/dashboard/content pages
        if (path.startsWith("/admin/dashboard/content")) {
            return;
        }

        // Get the actual controller and method name for better tracking
        String controllerName = joinPoint.getTarget().getClass().getSimpleName().replace("Controller", "").toLowerCase();
        String methodName = joinPoint.getSignature().getName();
        
        // Construct a meaningful page identifier
        String pageId = path.isEmpty() ? 
            String.format("%s-%s", controllerName, methodName) : 
            path.substring(1); // Remove leading slash
        
        visitorService.recordVisit(pageId);
    }
}
