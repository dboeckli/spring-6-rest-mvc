package ch.springframeworkguru.springrestmvc.repository;

import ch.springframeworkguru.springrestmvc.entity.Beer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BeerRepositoryTest {

    @Autowired
    BeerRepository beerRepository;

    @Test
    void testSave() {
        Beer savedBeer = beerRepository.save(Beer.builder().
                beerName("hallo")
                .build());

        assertAll(
                () -> assertNotNull(savedBeer),
                () -> assertNotNull(savedBeer.getId()),
                () -> assertNotNull(savedBeer.getBeerName()),
                () -> assertEquals("hallo", savedBeer.getBeerName())
        );
    }

}