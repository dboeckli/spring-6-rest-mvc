package ch.dboeckli.spring.restmvc.repository;

import ch.dboeckli.spring.restmvc.entity.BeerOrderLine;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@NullMarked
public interface BeerOrderLineRepository extends JpaRepository<BeerOrderLine, UUID> {
}
