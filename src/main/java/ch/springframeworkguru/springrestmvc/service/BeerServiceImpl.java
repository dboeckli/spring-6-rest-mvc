package ch.springframeworkguru.springrestmvc.service;

import ch.springframeworkguru.springrestmvc.model.Beer;
import ch.springframeworkguru.springrestmvc.model.BeerStyle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class BeerServiceImpl implements BeerService {

    private Map<UUID, Beer> beerMap;

    public BeerServiceImpl() {
        this.beerMap = new HashMap<>();

        Beer beer1 = Beer.builder()
                .id(UUID.randomUUID())
                .version(1)
                .beerName("Galaxy Cat")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("12356")
                .price(new BigDecimal("12.99"))
                .quantityOnHand(122)
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        Beer beer2 = Beer.builder()
                .id(UUID.randomUUID())
                .version(1)
                .beerName("Crank")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("12356222")
                .price(new BigDecimal("11.99"))
                .quantityOnHand(392)
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        Beer beer3 = Beer.builder()
                .id(UUID.randomUUID())
                .version(1)
                .beerName("Sunshine City")
                .beerStyle(BeerStyle.IPA)
                .upc("12356")
                .price(new BigDecimal("13.99"))
                .quantityOnHand(144)
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        beerMap.put(beer1.getId(), beer1);
        beerMap.put(beer2.getId(), beer2);
        beerMap.put(beer3.getId(), beer3);
    }

    @Override
    public List<Beer> listBeers(){
        return new ArrayList<>(beerMap.values());
    }

    @Override
    public Beer getBeerById(UUID id) {

        log.debug("Get Beer by Id - in service. Id: " + id.toString());

        return beerMap.get(id);
    }

    @Override
    public Beer saveNewBeer(Beer newBeer) {
        Beer savedBeer = Beer.builder()
                .id(UUID.randomUUID())
                .version(newBeer.getVersion())
                .beerName(newBeer.getBeerName())
                .beerStyle(newBeer.getBeerStyle())
                .upc(newBeer.getUpc())
                .price(newBeer.getPrice())
                .quantityOnHand(newBeer.getQuantityOnHand())
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
        beerMap.put(savedBeer.getId(), savedBeer);
        return savedBeer;
    }

    @Override
    public Beer editBeer(UUID beerId, Beer beer) {
        Beer beerToChange = beerMap.get(beerId);
        beerToChange.setBeerName(beer.getBeerName());
        beerToChange.setBeerStyle(beer.getBeerStyle());
        beerToChange.setPrice(beer.getPrice());
        beerToChange.setQuantityOnHand(beer.getQuantityOnHand());
        beerToChange.setUpdateDate(LocalDateTime.now());
        beerToChange.setUpc(beer.getUpc());
        beerToChange.setVersion(beer.getVersion());
        beerMap.replace(beerId, beerToChange);
        return beerToChange;
    }

    @Override
    public Beer deleteBeer(UUID beerId) {
        return beerMap.remove(beerId);
    }
}
