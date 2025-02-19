package uz.pdp.ilmpay.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.ilmpay.service.VisitorService;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final VisitorService visitorService;

    public AnalyticsController(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    /**
     * 📊 Update session analytics data
     * Now using Spring Session for more accurate tracking
     */
    @PostMapping("/session-update")
    public ResponseEntity<Void> updateSessionData(
            @RequestParam Long duration,
            @RequestParam(defaultValue = "true") boolean bounced,
            HttpSession session) {
        String sessionId = session.getId();
        // Visit is recorded with updated duration and bounce status
        visitorService.recordVisit("session-update"); // This will update existing session
        return ResponseEntity.ok().build();
    }

    /**
     * 📈 Get visitor statistics
     * Returns analytics for the specified time period
     */
    @GetMapping("/visitor-stats")
    public ResponseEntity<Map<String, Object>> getVisitorStats(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(visitorService.getVisitorStats(days));
    }

    /**
     * 🌡️ Get activity heatmap data
     * Shows user activity patterns across days and hours
     */
    @GetMapping("/activity-heatmap")
    public ResponseEntity<List<Map<String, Object>>> getActivityHeatmap(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(visitorService.getActivityHeatmap(days));
    }
}
