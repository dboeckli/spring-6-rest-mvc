package ch.dboeckli.spring.restmvc.repository;

import ch.dboeckli.spring.restmvc.entity.BeerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BeerOrderRepository extends JpaRepository<BeerOrder, UUID> {
}
