package ch.springframeworkguru.springrestmvc.repository;

import ch.guru.springframework.spring6restmvcapi.dto.BeerStyle;
import ch.springframeworkguru.springrestmvc.entity.Beer;
import jakarta.validation.ConstraintViolationException;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

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

        Page<@NonNull Beer> beerList = beerRepository.findAllByBeerNameIsLikeIgnoreCase("hallo", Pageable.unpaged());

        assertAll(
            () -> assertEquals(1, beerList.getTotalElements()),
            () -> assertEquals(1, beerList.getTotalPages()),
            () -> assertEquals(0, beerList.getNumber()),
            () -> assertEquals(savedBeer, beerList.getContent().getFirst())
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

        Page<@NonNull Beer> beerList = beerRepository.findAllByBeerNameIsLikeIgnoreCase("%hallo%", Pageable.unpaged());

        assertAll(
            () -> assertEquals(2, beerList.getTotalElements()),
            () -> assertEquals(1, beerList.getTotalPages()),
            () -> assertEquals(0, beerList.getNumber()),
            () -> assertTrue(beerList.getContent().contains(firstBeer)),
            () -> assertTrue(beerList.getContent().contains(secondBeer)),
            () -> assertFalse(beerList.getContent().contains(thirdBeer))
        );
    }

    @Test
    void findBeerByNameWithSeveralBeersByWildCardPaged() {
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

        Page<@NonNull Beer> beerLisPage1 = beerRepository.findAllByBeerNameIsLikeIgnoreCase("%hallo%", PageRequest.of(0, 1));
        Page<@NonNull Beer> beerListPage2 = beerRepository.findAllByBeerNameIsLikeIgnoreCase("%hallo%", PageRequest.of(1, 1));

        assertAll(
            () -> assertEquals(2, beerLisPage1.getTotalElements()),
            () -> assertEquals(2, beerLisPage1.getTotalPages()),

            () -> assertEquals(0, beerLisPage1.getNumber()),
            () -> assertEquals(1, beerLisPage1.getNumberOfElements()),
            () -> assertTrue(beerLisPage1.getContent().contains(firstBeer)),
            () -> assertFalse(beerLisPage1.getContent().contains(thirdBeer)),

            () -> assertEquals(1, beerListPage2.getNumber()),
            () -> assertEquals(1, beerListPage2.getNumberOfElements()),
            () -> assertTrue(beerListPage2.getContent().contains(secondBeer)),
            () -> assertFalse(beerLisPage1.getContent().contains(thirdBeer))
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

        Page<@NonNull Beer> beerList = beerRepository.findAllByBeerStyle(BeerStyle.PALE_ALE, Pageable.unpaged());

        assertAll(
            () -> assertEquals(1, beerList.getTotalElements()),
            () -> assertEquals(1, beerList.getTotalPages()),
            () -> assertEquals(0, beerList.getNumber()),
            () -> assertEquals(firstBeer, beerList.getContent().getFirst())
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

        Page<@NonNull Beer> beerList = beerRepository.findAllByBeerStyleAndBeerNameIsLikeIgnoreCase(BeerStyle.PALE_ALE, "%hallo%", Pageable.unpaged());

        assertAll(
            () -> assertEquals(1, beerList.getTotalElements()),
            () -> assertEquals(1, beerList.getTotalPages()),
            () -> assertEquals(0, beerList.getNumber()),
            () -> assertEquals(firstBeer, beerList.getContent().getFirst())
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
