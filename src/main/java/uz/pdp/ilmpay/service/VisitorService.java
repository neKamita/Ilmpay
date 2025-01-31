package uz.pdp.ilmpay.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.pdp.ilmpay.model.Visitor;
import uz.pdp.ilmpay.repository.VisitorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;

/**
 * 👥 Visitor Service
 * Keeping track of our awesome visitors!
 * 
 * @author Your Friendly Neighborhood Developer
 * @version 1.0 (The "People Counter" Edition)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VisitorService {

    private final VisitorRepository visitorRepository;
    private final HttpServletRequest request;

    /**
     * 📊 Get total number of visitors
     * Every visitor counts! (literally)
     */
    public Long getTotalVisitors() {
        return visitorRepository.countUniqueVisitors();
    }

    /**
     * 🎯 Get today's visitor count
     * Fresh faces of the day!
     */
    public Long getTodayVisitors() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        return visitorRepository.countTodayUniqueVisitors(startOfDay);
    }

    /**
     * 📱 Get total app downloads
     * Because who doesn't love our app?
     */
    public Long getAppDownloads() {
        return visitorRepository.countByIsDownloadedTrue();
    }

    /**
     * 🌟 Get number of active users
     * The real MVPs of our platform!
     */
    public Long getActiveUsers() {
        return visitorRepository.countByIsActiveTrue();
    }

    /**
     * 📝 Record a new visit
     * Rolling out the virtual red carpet!
     */
    public void recordVisit(String page) {
        try {
            Visitor visitor = new Visitor();
            visitor.setIpAddress(getClientIp());
            visitor.setUserAgent(request.getHeader("User-Agent"));
            visitor.setVisitTime(LocalDateTime.now());
            visitor.setPageVisited(page);
            visitor.setActive(true);
            
            visitorRepository.save(visitor);
            log.info("Recorded visit from IP: {} to page: {}", visitor.getIpAddress(), page);
        } catch (Exception e) {
            log.error("Failed to record visit", e);
        }
    }

    private String getClientIp() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
