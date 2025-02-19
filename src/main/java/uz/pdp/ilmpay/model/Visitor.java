package uz.pdp.ilmpay.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 👥 Visitor Entity
 * Tracks user visits and sessions with improved session management
 *
 * @author Your Friendly Neighborhood Developer
 * @version 2.1 (The "Session Master" Edition)
 */
@Entity
@Table(name = "visitors", indexes = {
    @Index(name = "idx_session_id", columnList = "sessionId"),
    @Index(name = "idx_last_active", columnList = "lastActiveTime")
})
public class Visitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔑 Session tracking
    @Column(nullable = false)
    private String sessionId; // Spring Session ID
    
    @Column(nullable = false)
    private String ipAddress;
    
    private String userAgent;
    
    // ⏰ Timestamp tracking
    @Column(nullable = false)
    private LocalDateTime firstVisitTime; // When the session started
    
    @Column(nullable = false)
    private LocalDateTime lastActiveTime; // Last activity timestamp
    
    private String lastPageVisited;
    private boolean isDownloaded;
    
    // 📊 Analytics data
    private boolean isActive = true;
    private boolean bounced = true; // Initially true, set to false if user views multiple pages
    private Long sessionDuration; // Duration in seconds, updated on session end
    
    // 🔄 Visit counter
    private int pageVisitCount = 1; // Tracks number of pages visited in this session

    @PreUpdate
    protected void onUpdate() {
        // If user visits more than one page, they haven't bounced
        if (pageVisitCount > 1) {
            bounced = false;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getFirstVisitTime() {
        return firstVisitTime;
    }

    public void setFirstVisitTime(LocalDateTime firstVisitTime) {
        this.firstVisitTime = firstVisitTime;
    }

    public LocalDateTime getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(LocalDateTime lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    public String getLastPageVisited() {
        return lastPageVisited;
    }

    public void setLastPageVisited(String lastPageVisited) {
        this.lastPageVisited = lastPageVisited;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isBounced() {
        return bounced;
    }

    public void setBounced(boolean bounced) {
        this.bounced = bounced;
    }

    public Long getSessionDuration() {
        return sessionDuration;
    }

    public void setSessionDuration(Long sessionDuration) {
        this.sessionDuration = sessionDuration;
    }

    public int getPageVisitCount() {
        return pageVisitCount;
    }

    public void setPageVisitCount(int pageVisitCount) {
        this.pageVisitCount = pageVisitCount;
    }
}
