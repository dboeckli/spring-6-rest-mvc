package ch.springframeworkguru.springrestmvc.repository;

import ch.springframeworkguru.springrestmvc.entity.Beer;
import ch.springframeworkguru.springrestmvc.service.dto.BeerStyle;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BeerRepositoryTest {

    @Autowired
    BeerRepository beerRepository;
    
    @Test
    void findBeerByName() {
        Beer savedBeer = beerRepository.save(Beer.builder()
            .beerName("hallo")
            .beerStyle(BeerStyle.PALE_ALE)
            .upc("123")
            .price(BigDecimal.valueOf(10))
            .build());

        List<Beer> beerList = beerRepository.findAllByBeerNameIsLikeIgnoreCase("hallo");
        
        assertAll(
            () -> assertEquals(1, beerList.size()),
            () -> assertEquals(savedBeer, beerList.getFirst())
        );
    }

    @Test
    void findBeerByNameWithSeveralBeersByWildCard() {
        Beer firstBeer = beerRepository.save(Beer.builder()
            .beerName("YYYYYYYYhalloXXXXXXX")
            .beerStyle(BeerStyle.PALE_ALE)
            .upc("123")
            .price(BigDecimal.valueOf(10))
            .build());

        Beer secondBeer = beerRepository.save(Beer.builder()
            .beerName("hallo")
            .beerStyle(BeerStyle.PALE_ALE)
            .upc("123")
            .price(BigDecimal.valueOf(10))
            .build());

        Beer thirdBeer = beerRepository.save(Beer.builder()
            .beerName("guguseli")
            .beerStyle(BeerStyle.PALE_ALE)
            .upc("123")
            .price(BigDecimal.valueOf(10))
            .build());

        List<Beer> beerList = beerRepository.findAllByBeerNameIsLikeIgnoreCase("%hallo%");

        assertAll(
            () -> assertEquals(2, beerList.size()),
            () -> assertTrue(beerList.contains(firstBeer)),
            () -> assertTrue(beerList.contains(secondBeer)),
            () -> assertFalse(beerList.contains(thirdBeer))
        );
    }

    @Test
    void findBeerByBeerStyle() {
        Beer firstBeer = beerRepository.save(Beer.builder()
            .beerName("hallo")
            .beerStyle(BeerStyle.PALE_ALE)
            .upc("123")
            .price(BigDecimal.valueOf(10))
            .build());

        beerRepository.save(Beer.builder()
            .beerName("hallo")
            .beerStyle(BeerStyle.IPA)
            .upc("123")
            .price(BigDecimal.valueOf(10))
            .build());

        List<Beer> beerList = beerRepository.findAllByBeerStyle(BeerStyle.PALE_ALE);

        assertAll(
            () -> assertEquals(1, beerList.size()),
            () -> assertEquals(firstBeer, beerList.getFirst())
        );
    }

    @Test
    void findAllByBeerStyleAndBeerNameIsLikeIgnoreCase() {
        Beer firstBeer = beerRepository.save(Beer.builder()
            .beerName("XXXhalloYYY")
            .beerStyle(BeerStyle.PALE_ALE)
            .upc("123")
            .price(BigDecimal.valueOf(10))
            .build());

        beerRepository.save(Beer.builder()
            .beerName("guguseli")
            .beerStyle(BeerStyle.PALE_ALE)
            .upc("123")
            .price(BigDecimal.valueOf(10))
            .build());

        List<Beer> beerList = beerRepository.findAllByBeerStyleAndBeerNameIsLikeIgnoreCase(BeerStyle.PALE_ALE, "%hallo%");

        assertAll(
            () -> assertEquals(1, beerList.size()),
            () -> assertEquals(firstBeer, beerList.getFirst())
        );
    }

    @Test
    void testSave() {
        Beer savedBeer = beerRepository.save(Beer.builder()
                .beerName("hallo")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("123")
                .price(BigDecimal.valueOf(10))
                .build());

        beerRepository.flush();

        assertAll(
                () -> assertNotNull(savedBeer),
                () -> assertNotNull(savedBeer.getId()),
                () -> assertNotNull(savedBeer.getBeerName()),
                () -> assertEquals("hallo", savedBeer.getBeerName())
        );
    }

    @Test
    void testSaveBeerNameTooLong() {
        assertThrows(ConstraintViolationException.class, () -> {
            beerRepository.save(Beer.builder()
                .beerName("hallo 01234567890123456789012345678901234567890123456789")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("123")
                .price(BigDecimal.valueOf(10))
                .build());

            beerRepository.flush();
        });

    }

}
