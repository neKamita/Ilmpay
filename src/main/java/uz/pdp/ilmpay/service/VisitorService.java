package uz.pdp.ilmpay.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uz.pdp.ilmpay.model.Visitor;
import uz.pdp.ilmpay.repository.VisitorRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

/**
 * 👥 Visitor Service
 * Keeping track of our awesome visitors!
 *
 * @author Your Friendly Neighborhood Developer
 * @version 2.0 (The "Analytics Master" Edition)
 */
@Service
public class VisitorService {

    private static final Logger log = LoggerFactory.getLogger(VisitorService.class);
    private final VisitorRepository visitorRepository;
    private final HttpServletRequest request;
    private final HttpSession session;

    @Value("${session.timeout.minutes:30}") // Default to 30 minutes
    private int sessionTimeoutMinutes;

    @Value("${active.user.threshold.minutes:15}") // Consider user active if activity within last 15 minutes
    private int activeThresholdMinutes;

    public VisitorService(VisitorRepository visitorRepository, HttpServletRequest request, HttpSession session) {
        this.visitorRepository = visitorRepository;
        this.request = request;
        this.session = session;
    }

    /**
     * 🧹 Cleanup expired sessions
     * Called periodically to maintain data accuracy
     */
    private void cleanupExpiredSessions() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(sessionTimeoutMinutes);
        visitorRepository.findAll().stream()
            .filter(v -> v.isActive() && v.getLastActiveTime().isBefore(cutoffTime))
            .forEach(v -> {
                v.setActive(false);
                v.setSessionDuration(ChronoUnit.SECONDS.between(v.getFirstVisitTime(), v.getLastActiveTime()));
                visitorRepository.save(v);
                log.info("Marked session as inactive: {} (duration: {} seconds)", v.getSessionId(), v.getSessionDuration());
            });
    }

    /**
     * Check if visitor stats should be calculated
     * Always returns true since access control is handled by @PreAuthorize
     */
    private boolean shouldCalculateStats() {
        log.debug("Stats calculation requested - access control handled by security annotations");
        return true;
    }

    /**
     * 📊 Get total number of visitors
     * Only calculates when needed for admin dashboard
     */
    public Long getTotalVisitors() {
        if (!shouldCalculateStats()) {
            return 0L;
        }
        return visitorRepository.countUniqueVisitors();
    }

    /**
     * 🎯 Get today's visitor count
     * Only calculates when needed for admin dashboard
     */
    public Long getTodayVisitors() {
        if (!shouldCalculateStats()) {
            return 0L;
        }
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        return visitorRepository.countTodayUniqueVisitors(startOfDay);
    }

    /**
     * 📱 Get total app downloads
     * Only calculates when needed for admin dashboard
     */
    public Long getAppDownloads() {
        if (!shouldCalculateStats()) {
            return 0L;
        }
        return visitorRepository.countByIsDownloadedTrue();
    }

    /**
     * 🌟 Get number of active users
     * Uses time-based activity tracking for accuracy
     */
    public Long getActiveUsers() {
        if (!shouldCalculateStats()) {
            return 0L;
        }
        
        // Cleanup expired sessions first
        cleanupExpiredSessions();
        
        // Consider users active if they have activity within the threshold
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(activeThresholdMinutes);
        long activeCount = visitorRepository.countRecentlyActiveUsers(cutoffTime);
        
        log.info("Active users count: {} (threshold: {} minutes)", activeCount, activeThresholdMinutes);
        return activeCount;
    }

    /**
     * 📊 Get visitor stats for charting
     */
    /**
     * Calculate percentage change between two values
     */
    private double calculatePercentageChange(double oldValue, double newValue) {
        if (oldValue == 0) return newValue > 0 ? 100.0 : 0.0;
        return ((newValue - oldValue) / oldValue) * 100.0;
    }

    /**
     * Get comparison stats for different time periods
     */
    private Map<String, Object> getComparisonStats(LocalDateTime current, LocalDateTime previous, int days) {
        Map<String, Object> stats = new HashMap<>();

        // Total visitors comparison
        long currentVisitors = visitorRepository.getVisitorsCountForPeriod(previous, current);
        long previousVisitors = visitorRepository.getVisitorsCountForPeriod(
            previous.minusDays(days), // Corrected previous date calculation, using days parameter
            previous
        );
        double visitorChange = calculatePercentageChange(previousVisitors, currentVisitors);

        // Active users comparison
        long currentActive = visitorRepository.getActiveUsersForPeriod(previous, current);
        long previousActive = visitorRepository.getActiveUsersForPeriod(
            previous.minusDays(days), // Corrected previous date calculation, using days parameter
            previous
        );
        double activeChange = calculatePercentageChange(previousActive, currentActive);

        // Session duration comparison
        Double currentDuration = visitorRepository.getAvgSessionDurationForPeriod(previous, current);
        Double previousDuration = visitorRepository.getAvgSessionDurationForPeriod(
            previous.minusDays(days), // Corrected previous date calculation, using days parameter
            previous
        );
        double durationChange = calculatePercentageChange(previousDuration, currentDuration);

        stats.put("totalVisitors", currentVisitors);
        stats.put("visitorChange", visitorChange);
        stats.put("activeUsers", currentActive);
        stats.put("activeChange", activeChange);
        stats.put("avgSessionDuration", currentDuration);
        stats.put("durationChange", durationChange);

        // Bounce rate comparison
        Double currentBounceRate = visitorRepository.getBounceRateForPeriod(previous, current);
        Double previousBounceRate = visitorRepository.getBounceRateForPeriod(
            previous.minusDays(days), // Corrected previous date calculation, using days parameter
            previous
        );
        double bounceChange = calculatePercentageChange(previousBounceRate, currentBounceRate);
        stats.put("bounceRate", currentBounceRate);
        stats.put("bounceChange", bounceChange);

        log.info("Comparison Stats - Visitors: current={}, previous={}, change={}", currentVisitors, previousVisitors, visitorChange); // Added logs
        log.info("Comparison Stats - Active Users: current={}, previous={}, change={}", currentActive, previousActive, activeChange); // Added logs
        log.info("Comparison Stats - Avg Duration: current={}, previous={}, change={}", currentDuration, previousDuration, durationChange); // Added logs
        log.info("Comparison Stats - Bounce Rate: current={}, previous={}, change={}", currentBounceRate, previousBounceRate, bounceChange); // Added logs

        return stats;
    }

    public Map<String, Object> getVisitorStats(int days) {
        if (!shouldCalculateStats()) {
            return new HashMap<>();
        }

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);

        log.info("getVisitorStats - days: {}, startDate: {}, endDate: {}", days, startDate, endDate);

        Map<String, Object> stats = new HashMap<>();

        // Get daily visitor stats
        List<Object[]> dailyStats = visitorRepository.getDailyVisitorStats(startDate, endDate);
        stats.put("dailyVisitors", formatDailyStats(dailyStats));

        // Get comparison stats
        Map<String, Object> comparisons = getComparisonStats(endDate, startDate, days); // Passing days parameter
        stats.putAll(comparisons);

        return stats;
    }

    /**
     * 🌡️ Get activity heatmap data
     * Only calculates when needed for admin dashboard
     */
    public List<Map<String, Object>> getActivityHeatmap(int days) {
        if (!shouldCalculateStats()) {
            return List.of();
        }

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);

        List<Object[]> heatmapData = visitorRepository.getActivityHeatmap(startDate, endDate);

        return heatmapData.stream()
            .map(data -> {
                Map<String, Object> point = new HashMap<>();
                point.put("hour", ((Number) data[0]).intValue());
                point.put("dayOfWeek", ((Number) data[1]).intValue());
                point.put("count", ((Number) data[2]).intValue());
                return point;
            })
            .collect(Collectors.toList());
    }

    /**
     * 📝 Record a page visit and update session information
     * Enhanced with proper session management and status tracking
     */
    public void recordVisit(String page) {
        try {
            String sessionId = session.getId();
            String clientIp = getClientIp();
            LocalDateTime now = LocalDateTime.now();

            // Try to find an existing active visitor record for this session
            LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(sessionTimeoutMinutes);
            List<Visitor> recentVisitors = visitorRepository.findRecentBySessionId(sessionId, cutoffTime);
            Optional<Visitor> existingVisitor = recentVisitors.isEmpty() ? Optional.empty() : Optional.of(recentVisitors.get(0));

            if (existingVisitor.isPresent()) {
                // Update existing visitor with improved session handling
                Visitor visitor = existingVisitor.get();
                LocalDateTime previousActiveTime = visitor.getLastActiveTime();
                visitor.setLastActiveTime(now);
                visitor.setLastPageVisited(page);
                visitor.setPageVisitCount(visitor.getPageVisitCount() + 1);
                
                // Update session duration
                long durationSeconds = ChronoUnit.SECONDS.between(visitor.getFirstVisitTime(), now);
                visitor.setSessionDuration(durationSeconds);
                
                // Check for session timeout
                if (ChronoUnit.MINUTES.between(previousActiveTime, now) > sessionTimeoutMinutes) {
                    log.info("Session timed out - creating new session for {}", sessionId);
                    visitor.setActive(false);
                    visitorRepository.save(visitor);
                    
                    // Create new session
                    visitor = new Visitor();
                    visitor.setSessionId(sessionId);
                    visitor.setIpAddress(clientIp);
                    visitor.setUserAgent(request.getHeader("User-Agent"));
                    visitor.setFirstVisitTime(now);
                    visitor.setLastActiveTime(now);
                    visitor.setLastPageVisited(page);
                    visitor.setActive(true);
                    visitor.setPageVisitCount(1);
                } else {
                    visitor.setActive(true);
                }
                
                visitorRepository.save(visitor);
                log.info("Updated visit for session {} on page {}", sessionId, page);
            } else {
                // Create new visitor record
                Visitor visitor = new Visitor();
                visitor.setSessionId(sessionId);
                visitor.setIpAddress(clientIp);
                visitor.setUserAgent(request.getHeader("User-Agent"));
                visitor.setFirstVisitTime(now);
                visitor.setLastActiveTime(now);
                visitor.setLastPageVisited(page);
                visitor.setActive(true);
                visitor.setPageVisitCount(1);
                
                visitorRepository.save(visitor);
                log.info("Recorded new visit from IP: {} with session {} on page {}", clientIp, sessionId, page);
            }
        } catch (Exception e) {
            log.error("Failed to record visit: {}", e.getMessage(), e);
        }
    }

    /**
     * 🔍 Get client's IP address
     * Handles proxy scenarios too!
     */
    private String getClientIp() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }

    /**
     * 📊 Format daily stats for charting
     */
    private List<Map<String, Object>> formatDailyStats(List<Object[]> dailyStats) {
        return dailyStats.stream()
            .map(stat -> {
                Map<String, Object> point = new HashMap<>();
                point.put("date", stat[0]);
                point.put("visitors", stat[1]);
                return point;
            })
            .collect(Collectors.toList());
    }
}
