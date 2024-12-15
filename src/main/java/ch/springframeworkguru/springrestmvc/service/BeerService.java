package ch.springframeworkguru.springrestmvc.service;

import ch.guru.springframework.spring6restmvcapi.dto.BeerDTO;
import ch.guru.springframework.spring6restmvcapi.dto.BeerStyle;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface BeerService {

    Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer page, Integer pageSize);

    Optional<BeerDTO> getBeerById(UUID id);

    BeerDTO saveNewBeer(BeerDTO newBeer);

    Optional<BeerDTO> editBeer(UUID beerId, BeerDTO beer);

    Boolean deleteBeer(UUID beerId);

    Optional<BeerDTO> patchBeer(UUID beerId, BeerDTO beer);
}
