package ch.springframeworkguru.springrestmvc.service;

import ch.springframeworkguru.springrestmvc.service.dto.BeerDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeerService {

    List<BeerDTO> listBeers();

    Optional<BeerDTO> getBeerById(UUID id);

    BeerDTO saveNewBeer(BeerDTO newBeer);

    Optional<BeerDTO> editBeer(UUID beerId, BeerDTO beer);

    Boolean deleteBeer(UUID beerId);

    BeerDTO patchBeer(UUID beerId, BeerDTO beer);
}
