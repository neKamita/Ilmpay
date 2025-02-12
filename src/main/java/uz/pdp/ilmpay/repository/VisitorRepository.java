package uz.pdp.ilmpay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import uz.pdp.ilmpay.model.Visitor;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    long countByVisitTimeBetween(LocalDateTime start, LocalDateTime end);
    long countByIsDownloadedTrue();
    long countByIsActiveTrue();
    long countByBouncedTrue();
    
    @Query("SELECT COUNT(DISTINCT v.ipAddress) FROM Visitor v")
    long countUniqueVisitors();
    
    @Query("SELECT COUNT(DISTINCT v.ipAddress) FROM Visitor v WHERE v.visitTime >= :startTime")
    long countTodayUniqueVisitors(@Param("startTime") LocalDateTime startTime);

    long count();

    // Daily visitor statistics
    @Query("SELECT DATE(v.visitTime) as date, COUNT(DISTINCT v.ipAddress) as count " +
           "FROM Visitor v " +
           "WHERE v.visitTime BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(v.visitTime) " +
           "ORDER BY date")
    List<Object[]> getDailyVisitorStats(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);

    // Hourly activity heatmap data
    @Query(value = 
           "SELECT EXTRACT(HOUR FROM visit_time) as hour, " +
           "EXTRACT(DOW FROM visit_time) as day_of_week, " +
           "COUNT(DISTINCT ip_address) as count " +
           "FROM visitors " +
           "WHERE visit_time BETWEEN :startDate AND :endDate " +
           "GROUP BY EXTRACT(HOUR FROM visit_time), EXTRACT(DOW FROM visit_time)", 
           nativeQuery = true)
    List<Object[]> getActivityHeatmap(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);

    // Bounce rate calculation
    @Query("SELECT COALESCE(" +
           "CASE WHEN COUNT(*) = 0 THEN 0.0 " +
           "ELSE COUNT(CASE WHEN v.bounced = true THEN 1 END) * 100.0 / COUNT(*) END, " +
           "0.0) " +
           "FROM Visitor v " +
           "WHERE v.visitTime BETWEEN :startDate AND :endDate")
    Double getBounceRate(@Param("startDate") LocalDateTime startDate, 
                        @Param("endDate") LocalDateTime endDate);

    // Get average session duration in seconds
    @Query("SELECT COALESCE(" +
           "CASE WHEN COUNT(v.sessionDuration) = 0 THEN 0.0 " +
           "ELSE AVG(COALESCE(v.sessionDuration, 0)) END, " +
           "0.0) " +
           "FROM Visitor v " +
           "WHERE v.visitTime BETWEEN :startDate AND :endDate")
    Double getAverageSessionDuration(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);

    @Query("SELECT v FROM Visitor v " +
           "WHERE v.ipAddress = :ipAddress " +
           "AND v.visitTime > :cutoffTime " +
           "ORDER BY v.visitTime DESC")
    List<Visitor> findRecentByIpAddress(@Param("ipAddress") String ipAddress, 
                                      @Param("cutoffTime") LocalDateTime cutoffTime);

    // Get visitors count for a specific period
    @Query("SELECT COUNT(DISTINCT v.ipAddress) FROM Visitor v WHERE v.visitTime BETWEEN :startDate AND :endDate")
    long getVisitorsCountForPeriod(@Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);

    // Get active users for a specific period
    @Query("SELECT COUNT(DISTINCT v.ipAddress) FROM Visitor v " +
           "WHERE v.isActive = true AND v.visitTime BETWEEN :startDate AND :endDate")
    long getActiveUsersForPeriod(@Param("startDate") LocalDateTime startDate, 
                                @Param("endDate") LocalDateTime endDate);

    // Get bounce rate for a specific period
    @Query("SELECT COALESCE(" +
           "CASE WHEN COUNT(*) = 0 THEN 0.0 " +
           "ELSE COUNT(CASE WHEN v.bounced = true THEN 1 END) * 100.0 / COUNT(*) END, " +
           "0.0) " +
           "FROM Visitor v " +
           "WHERE v.visitTime BETWEEN :startDate AND :endDate")
    Double getBounceRateForPeriod(@Param("startDate") LocalDateTime startDate, 
                                 @Param("endDate") LocalDateTime endDate);

    // Get average session duration for a specific period
    @Query("SELECT COALESCE(" +
           "CASE WHEN COUNT(v.sessionDuration) = 0 THEN 0.0 " +
           "ELSE AVG(COALESCE(v.sessionDuration, 0)) END, " +
           "0.0) " +
           "FROM Visitor v " +
           "WHERE v.visitTime BETWEEN :startDate AND :endDate")
    Double getAvgSessionDurationForPeriod(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
}
