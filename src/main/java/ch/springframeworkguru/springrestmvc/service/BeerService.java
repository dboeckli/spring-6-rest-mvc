package ch.springframeworkguru.springrestmvc.service;

import ch.springframeworkguru.springrestmvc.model.Beer;

import java.util.List;
import java.util.UUID;

public interface BeerService {

    List<Beer> listBeers();

    Beer getBeerById(UUID id);

    Beer saveNewBeer(Beer newBeer);

    Beer editBeer(UUID beerId, Beer beer);

    Beer deleteBeer(UUID beerId);
}
