package ch.dboeckli.spring.restmvc.repository;

import ch.dboeckli.spring.restmvc.entity.BeerAudit;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@NullMarked
public interface BeerAuditRepository extends JpaRepository<BeerAudit, UUID> {
}
