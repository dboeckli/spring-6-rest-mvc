package ch.dboeckli.spring.restmvc.repository;

import ch.dboeckli.spring.restmvc.entity.BeerOrderLine;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BeerOrderLinesRepository extends JpaRepository<@NonNull BeerOrderLine, @NonNull UUID> {
}
