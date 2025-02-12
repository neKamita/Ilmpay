package uz.pdp.ilmpay.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "visitors")
public class Visitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String ipAddress;
    private String userAgent;
    private LocalDateTime visitTime;
    private String pageVisited;
    private boolean isDownloaded;
    private boolean isActive;
    private boolean bounced = false;
    private Long sessionDuration; // Duration in seconds

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

    public LocalDateTime getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(LocalDateTime visitTime) {
        this.visitTime = visitTime;
    }

    public String getPageVisited() {
        return pageVisited;
    }

    public void setPageVisited(String pageVisited) {
        this.pageVisited = pageVisited;
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
}
