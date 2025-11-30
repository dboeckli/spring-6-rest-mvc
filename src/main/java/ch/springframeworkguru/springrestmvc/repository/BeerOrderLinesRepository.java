package ch.springframeworkguru.springrestmvc.repository;

import ch.springframeworkguru.springrestmvc.entity.BeerOrderLine;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BeerOrderLinesRepository extends JpaRepository<@NonNull BeerOrderLine, @NonNull UUID> {
}
