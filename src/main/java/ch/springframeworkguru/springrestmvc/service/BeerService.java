package ch.springframeworkguru.springrestmvc.service;

import ch.springframeworkguru.springrestmvc.dto.BeerDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeerService {

    List<BeerDTO> listBeers();

    Optional<BeerDTO> getBeerById(UUID id);

    BeerDTO saveNewBeer(BeerDTO newBeer);

    BeerDTO editBeer(UUID beerId, BeerDTO beer);

    BeerDTO deleteBeer(UUID beerId);

    BeerDTO patchBeer(UUID beerId, BeerDTO beer);
}
