package uz.pdp.ilmpay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import uz.pdp.ilmpay.model.Visitor;

import java.time.LocalDateTime;
@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    long countByVisitTimeBetween(LocalDateTime start, LocalDateTime end);
    long countByIsDownloadedTrue();
    long countByIsActiveTrue();
    long countByBouncedTrue(); // Corrected method name
    
    @Query("SELECT COUNT(DISTINCT v.ipAddress) FROM Visitor v")
    long countUniqueVisitors();
    
    @Query("SELECT COUNT(DISTINCT v.ipAddress) FROM Visitor v WHERE v.visitTime >= :startTime")
    long countTodayUniqueVisitors(@Param("startTime") LocalDateTime startTime);

    long count();
}