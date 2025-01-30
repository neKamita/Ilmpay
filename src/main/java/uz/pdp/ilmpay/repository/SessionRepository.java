package uz.pdp.ilmpay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.pdp.ilmpay.model.UserSession;

import java.time.Duration;

public interface SessionRepository extends JpaRepository<UserSession, Long> {

} 