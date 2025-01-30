package uz.pdp.ilmpay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.pdp.ilmpay.model.User;

import java.time.LocalDateTime;

public interface UserRepository extends JpaRepository<User, Long> {
    long countBySource(String source);


}
