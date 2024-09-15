package ch.springframeworkguru.springrestmvc.repository;

import ch.springframeworkguru.springrestmvc.entity.Beer;
import ch.springframeworkguru.springrestmvc.service.dto.BeerStyle;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BeerRepositoryTest {

    @Autowired
    BeerRepository beerRepository;

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
