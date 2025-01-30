package uz.pdp.ilmpay.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uz.pdp.ilmpay.model.Visitor;
import uz.pdp.ilmpay.repository.VisitorRepository;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class VisitorService {
    private final VisitorRepository visitorRepository;
    private final HttpServletRequest request;

    public void recordVisit(String page) {
        try {
            Visitor visitor = new Visitor();
            visitor.setIpAddress(getClientIp());
            visitor.setUserAgent(request.getHeader("User-Agent"));
            visitor.setVisitTime(LocalDateTime.now());
            visitor.setPageVisited(page);
            visitor.setActive(true);
            
            visitorRepository.save(visitor);
            log.info("Recorded visit from IP: {}", visitor.getIpAddress());
        } catch (Exception e) {
            log.error("Failed to record visitor", e);
        }
    }

    public long getTotalVisitors() {
        return visitorRepository.countUniqueVisitors();
    }

    public long getTodayVisitors() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        return visitorRepository.countTodayUniqueVisitors(startOfDay);
    }

    public long getTotalDownloads() {
        return visitorRepository.countByIsDownloadedTrue();
    }

    public long getActiveUsers() {
        return visitorRepository.countByIsActiveTrue();
    }

    private String getClientIp() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
