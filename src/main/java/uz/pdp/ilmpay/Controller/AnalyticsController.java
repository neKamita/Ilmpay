package uz.pdp.ilmpay.Controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.pdp.ilmpay.service.VisitorService;
import java.util.Map;

/**
 * 📊 Analytics Controller
 * Serving up fresh statistics for the admin dashboard!
 * 
 * @author Your Analytics Guru
 * @version 1.0 (The "Data Whisperer" Edition)
 */
@RestController
@RequestMapping("/api/analytics")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AnalyticsController {
    
    private static final Logger log = LoggerFactory.getLogger(AnalyticsController.class);
    private final VisitorService visitorService;

    /**
     * 📈 Get dashboard analytics
     * All the juicy stats in one place!
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats(
            @RequestParam(defaultValue = "7") int days
    ) {
        Map<String, Object> stats = visitorService.getVisitorStats(days);
        return ResponseEntity.ok(stats);
    }

    /**
     * 🌡️ Get activity heatmap
     * See when your users are most active!
     */
    @GetMapping("/activity-heatmap")
    public ResponseEntity<?> getActivityHeatmap(
            @RequestParam(defaultValue = "7") int days
    ) {
        try {
            return ResponseEntity.ok(visitorService.getActivityHeatmap(days));
        } catch (Exception e) {
            log.error("Error generating heatmap: ", e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 📊 Get basic visitor metrics
     * Quick overview of the important numbers
     */
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Long>> getBasicMetrics() {
        Map<String, Long> metrics = Map.of(
            "totalVisitors", visitorService.getTotalVisitors(),
            "todayVisitors", visitorService.getTodayVisitors(),
            "appDownloads", visitorService.getAppDownloads(),
            "activeUsers", visitorService.getActiveUsers()
        );
        return ResponseEntity.ok(metrics);
    }
}
