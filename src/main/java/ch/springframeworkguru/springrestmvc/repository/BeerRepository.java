package ch.springframeworkguru.springrestmvc.repository;

import ch.guru.springframework.spring6restmvcapi.dto.BeerStyle;
import ch.springframeworkguru.springrestmvc.entity.Beer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BeerRepository extends JpaRepository<Beer, UUID> {
    
    Page<Beer> findAllByBeerNameIsLikeIgnoreCase(String beerName, Pageable pageable);

    Page<Beer> findAllByBeerStyle(BeerStyle beerStyle, Pageable pageable);

    Page<Beer> findAllByBeerStyleAndBeerNameIsLikeIgnoreCase(BeerStyle beerStyle, String beerName, Pageable pageable);
    
}
