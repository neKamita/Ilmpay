package uz.pdp.ilmpay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import uz.pdp.ilmpay.model.Visitor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    Optional<Visitor> findBySessionId(String sessionId);
    long countByFirstVisitTimeBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT v FROM Visitor v WHERE v.sessionId = :sessionId AND v.isActive = true")
    Optional<Visitor> findActiveBySessionId(@Param("sessionId") String sessionId);
    long countByIsDownloadedTrue();
    long countByIsActiveTrue();
    long countByBouncedTrue();
    
    @Query("SELECT COUNT(DISTINCT v.sessionId) FROM Visitor v")
    long countUniqueVisitors();
    
    @Query("SELECT COUNT(DISTINCT v.sessionId) FROM Visitor v WHERE v.firstVisitTime >= :startTime")
    long countTodayUniqueVisitors(@Param("startTime") LocalDateTime startTime);

    long count();

    // Daily visitor statistics
    @Query(value="-- Daily visitor statistics query - startDate: :startDate, endDate: :endDate\nSELECT DATE(v.first_visit_time) as date, COUNT(DISTINCT v.session_id) as count " +
            "FROM visitors v " +
            "WHERE v.first_visit_time BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(v.first_visit_time) " +
            "ORDER BY date", nativeQuery = true)
    List<Object[]> getDailyVisitorStats(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Hourly activity heatmap data
    @Query(value = 
           "SELECT EXTRACT(HOUR FROM first_visit_time) as hour, " +
           "EXTRACT(DOW FROM first_visit_time) as day_of_week, " +
           "COUNT(DISTINCT session_id) as count " +
           "FROM visitors " +
           "WHERE first_visit_time BETWEEN :startDate AND :endDate " +
           "GROUP BY EXTRACT(HOUR FROM first_visit_time), EXTRACT(DOW FROM first_visit_time)", 
           nativeQuery = true)
    List<Object[]> getActivityHeatmap(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);

    // Bounce rate calculation
    @Query("SELECT COALESCE(" +
           "CASE WHEN COUNT(*) = 0 THEN 0.0 " +
           "ELSE COUNT(CASE WHEN v.bounced = true THEN 1 END) * 100.0 / COUNT(*) END, " +
           "0.0) " +
           "FROM Visitor v " +
           "WHERE v.firstVisitTime BETWEEN :startDate AND :endDate")
    Double getBounceRate(@Param("startDate") LocalDateTime startDate, 
                        @Param("endDate") LocalDateTime endDate);

    // Get average session duration in seconds
    @Query("SELECT COALESCE(" +
           "CASE WHEN COUNT(v.sessionDuration) = 0 THEN 0.0 " +
           "ELSE AVG(COALESCE(v.sessionDuration, 0)) END, " +
           "0.0) " +
           "FROM Visitor v " +
           "WHERE v.firstVisitTime BETWEEN :startDate AND :endDate")
    Double getAverageSessionDuration(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);

    @Query("SELECT v FROM Visitor v " +
           "WHERE v.sessionId = :sessionId " +
           "AND v.lastActiveTime > :cutoffTime " +
           "ORDER BY v.lastActiveTime DESC")
    List<Visitor> findRecentBySessionId(@Param("sessionId") String sessionId, 
                                      @Param("cutoffTime") LocalDateTime cutoffTime);

    // Get visitors count for a specific period
    @Query("SELECT COUNT(DISTINCT v.sessionId) FROM Visitor v WHERE v.firstVisitTime BETWEEN :startDate AND :endDate")
    long getVisitorsCountForPeriod(@Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);

    // Get active users for a specific period
    @Query("SELECT COUNT(DISTINCT v.sessionId) FROM Visitor v " +
           "WHERE v.isActive = true AND v.lastActiveTime BETWEEN :startDate AND :endDate")
    long getActiveUsersForPeriod(@Param("startDate") LocalDateTime startDate, 
                                @Param("endDate") LocalDateTime endDate);

    // Get bounce rate for a specific period
    @Query("SELECT COALESCE(" +
           "CASE WHEN COUNT(*) = 0 THEN 0.0 " +
           "ELSE COUNT(CASE WHEN v.bounced = true THEN 1 END) * 100.0 / COUNT(*) END, " +
           "0.0) " +
           "FROM Visitor v " +
           "WHERE v.firstVisitTime BETWEEN :startDate AND :endDate")
    Double getBounceRateForPeriod(@Param("startDate") LocalDateTime startDate, 
                                 @Param("endDate") LocalDateTime endDate);

    // Get average session duration for a specific period
    @Query("SELECT COALESCE(" +
           "CASE WHEN COUNT(v.sessionDuration) = 0 THEN 0.0 " +
           "ELSE AVG(COALESCE(v.sessionDuration, 0)) END, " +
           "0.0) " +
           "FROM Visitor v " +
           "WHERE v.firstVisitTime BETWEEN :startDate AND :endDate")
    Double getAvgSessionDurationForPeriod(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
}
