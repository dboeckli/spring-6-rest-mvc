package ch.springframeworkguru.springrestmvc.controller;

import ch.springframeworkguru.springrestmvc.repository.BeerRepository;
import ch.springframeworkguru.springrestmvc.service.dto.BeerDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BeerControllerIT {

    @Autowired
    BeerController beerController;

    @Autowired
    BeerRepository beerRepository;

    @Test
    void testListBeers() {
        ResponseEntity<List<BeerDTO>> beersDtoResponseEntity = beerController.listBeers();
        List<BeerDTO> beersDtos = beersDtoResponseEntity.getBody();

        assertAll(
                () -> {
                    assert beersDtos != null;
                    assertEquals(3, beersDtos.size());
                }
        );
    }

    @Test
    @Transactional
    @Rollback(true) // we rollback to deletion to assuere that the other tests are not failling
    void testEmtpyListBeer() {
        beerRepository.deleteAll();
        ResponseEntity<List<BeerDTO>> beersDtoResponseEntity = beerController.listBeers();
        List<BeerDTO> beerDtos = beersDtoResponseEntity.getBody();

        assertAll(
                () -> {
                    assert beerDtos != null;
                    assertEquals(0, beerDtos.size());
                }
        );
    }

    @Test
    void testGetBeerById() {
        UUID givenBeerId = beerRepository.findAll().getFirst().getId();

        ResponseEntity<BeerDTO> beerDTOResponseEntity = beerController.getBeerById(givenBeerId);
        BeerDTO beerDTO = beerDTOResponseEntity.getBody();

        assertAll(
                () -> {
                    assert beerDTO != null;
                    assertEquals(givenBeerId, beerDTO.getId());
                }
        );
    }

    @Test
    void testGetBeerByIdNotFound() {
        assertThrows(NotfoundException.class, () -> beerController.getBeerById(UUID.randomUUID()));
    }
}