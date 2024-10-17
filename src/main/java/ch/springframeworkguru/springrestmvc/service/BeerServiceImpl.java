package ch.springframeworkguru.springrestmvc.service;

import ch.springframeworkguru.springrestmvc.service.dto.BeerDTO;
import ch.springframeworkguru.springrestmvc.service.dto.BeerStyle;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class BeerServiceImpl implements BeerService {

    private Map<UUID, BeerDTO> beerMap;

    public BeerServiceImpl() {
        this.beerMap = new HashMap<>();

        BeerDTO beer1 = BeerDTO.builder()
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

        BeerDTO beer2 = BeerDTO.builder()
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

        BeerDTO beer3 = BeerDTO.builder()
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
    public List<BeerDTO> listBeers(String beerName){
        return new ArrayList<>(beerMap.values());
    }

    @Override
    public Optional<BeerDTO> getBeerById(UUID id) {

        log.debug("Get Beer by Id - in service. Id: " + id.toString());

        return Optional.of(beerMap.get(id));
    }

    @Override
    public BeerDTO saveNewBeer(BeerDTO newBeer) {
        BeerDTO savedBeer = BeerDTO.builder()
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
    public Optional<BeerDTO> editBeer(UUID beerId, BeerDTO beer) {
        BeerDTO beerToChange = beerMap.get(beerId);
        beerToChange.setBeerName(beer.getBeerName());
        beerToChange.setBeerStyle(beer.getBeerStyle());
        beerToChange.setPrice(beer.getPrice());
        beerToChange.setQuantityOnHand(beer.getQuantityOnHand());
        beerToChange.setUpdateDate(LocalDateTime.now());
        beerToChange.setUpc(beer.getUpc());
        beerToChange.setVersion(beer.getVersion());
        beerMap.replace(beerId, beerToChange);
        return Optional.of(beerToChange);
    }

    @Override
    public Boolean deleteBeer(UUID beerId) {
        beerMap.remove(beerId);
        return true;
    }

    @Override
    public Optional<BeerDTO> patchBeer(UUID beerId, BeerDTO beer) {
        BeerDTO beerToChange = beerMap.get(beerId);
        if (StringUtils.isNotEmpty(beer.getBeerName())) {
            beerToChange.setBeerName(beer.getBeerName());
        }
        if (StringUtils.isNotEmpty(beer.getUpc())) {
            beerToChange.setUpc(beer.getUpc());
        }
        if (beer.getBeerStyle() != null) {
            beerToChange.setBeerStyle(beer.getBeerStyle());
        }
        if (beer.getPrice() != null) {
            beerToChange.setPrice(beer.getPrice());
        }
        if (beer.getQuantityOnHand() != null) {
            beerToChange.setQuantityOnHand(beer.getQuantityOnHand());
        }
        if (beer.getVersion() != null) {
            beerToChange.setVersion(beer.getVersion());
        }
        if (!beer.equals(beerToChange)) {
            beerToChange.setUpdateDate(LocalDateTime.now());
            beerMap.replace(beerId, beerToChange);
        }
        return Optional.of(beerToChange);
    }
}
