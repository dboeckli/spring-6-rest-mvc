package ch.springframeworkguru.springrestmvc.repository;

import ch.springframeworkguru.springrestmvc.entity.BeerAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BeerAuditRepository extends JpaRepository<BeerAudit, UUID>  {
}
