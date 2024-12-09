package ch.springframeworkguru.springrestmvc.repository;

import ch.springframeworkguru.springrestmvc.entity.BeerOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BeerOrderLinesRepository extends JpaRepository<BeerOrderLine, UUID>  {
}
