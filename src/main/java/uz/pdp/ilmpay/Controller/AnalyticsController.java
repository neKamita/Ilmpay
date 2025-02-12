package uz.pdp.ilmpay.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.ilmpay.service.VisitorService;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final VisitorService visitorService;

    public AnalyticsController(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    @PostMapping("/session-update")
    public ResponseEntity<Void> updateSessionData(
            @RequestParam Long duration,
            @RequestParam(defaultValue = "true") boolean bounced,
            HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        visitorService.updateVisitorSession(ipAddress, duration, bounced);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/visitor-stats")
    public ResponseEntity<Map<String, Object>> getVisitorStats(@RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(visitorService.getVisitorStats(days));
    }

    @GetMapping("/activity-heatmap")
    public ResponseEntity<List<Map<String, Object>>> getActivityHeatmap(@RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(visitorService.getActivityHeatmap(days));
    }
}
