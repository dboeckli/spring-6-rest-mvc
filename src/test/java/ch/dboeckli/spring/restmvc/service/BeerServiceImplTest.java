package ch.dboeckli.spring.restmvc.service;

import ch.guru.springframework.spring6restmvcapi.dto.BeerDTO;
import ch.guru.springframework.spring6restmvcapi.dto.BeerStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BeerServiceImplTest {

    private BeerServiceImpl beerService;

    @BeforeEach
    void setUp() {
        beerService = new BeerServiceImpl();
    }

    @Test
    void listBeersReturnsAllBeers() {
        assertThat(allBeers()).hasSize(3);
    }

    @Test
    void getBeerByIdReturnsExistingBeer() {
        BeerDTO existing = firstBeer();

        assertThat(beerService.getBeerById(existing.getId())).isPresent();
        assertThat(beerService.getBeerById(existing.getId()).orElseThrow().getBeerName())
            .isEqualTo(existing.getBeerName());
    }

    @Test
    void saveNewBeerAddsBeerToStore() {
        BeerDTO saved = beerService.saveNewBeer(BeerDTO.builder()
            .beerName("New Beer")
            .beerStyle(BeerStyle.STOUT)
            .upc("99001")
            .price(new BigDecimal("1.99"))
            .quantityOnHand(5)
            .version(1)
            .build());

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getBeerName()).isEqualTo("New Beer");
        assertThat(allBeers()).hasSize(4);
        assertThat(beerService.getBeerById(saved.getId())).isPresent();
    }

    @Test
    void editBeerUpdatesAllProvidedFields() {
        BeerDTO existing = firstBeer();

        BeerDTO edited = beerService
            .editBeer(existing.getId(),
                    BeerDTO.builder()
                        .beerName("Changed")
                        .beerStyle(BeerStyle.LAGER)
                        .upc("55555")
                        .price(new BigDecimal("9.99"))
                        .quantityOnHand(42)
                        .version(2)
                        .build())
            .orElseThrow();

        assertThat(edited.getBeerName()).isEqualTo("Changed");
        assertThat(edited.getBeerStyle()).isEqualTo(BeerStyle.LAGER);
        assertThat(edited.getUpc()).isEqualTo("55555");
        assertThat(edited.getPrice()).isEqualTo(new BigDecimal("9.99"));
        assertThat(edited.getQuantityOnHand()).isEqualTo(42);
        assertThat(edited.getVersion()).isEqualTo(2);
        assertThat(beerService.getBeerById(existing.getId()).orElseThrow().getBeerName()).isEqualTo("Changed");
    }

    @Test
    void deleteBeerRemovesBeerFromStore() {
        BeerDTO existing = firstBeer();

        assertThat(beerService.deleteBeer(existing.getId())).isTrue();
        assertThat(allBeers()).hasSize(2);
    }

    @Test
    void patchBeerUpdatesOnlyProvidedFields() {
        BeerDTO existing = firstBeer();

        BeerDTO patched = beerService
            .patchBeer(existing.getId(),
                    BeerDTO.builder()
                        .beerName("Patched")
                        .beerStyle(BeerStyle.IPA)
                        .upc("77777")
                        .price(new BigDecimal("3.33"))
                        .quantityOnHand(7)
                        .version(3)
                        .build())
            .orElseThrow();

        assertThat(patched.getBeerName()).isEqualTo("Patched");
        assertThat(patched.getBeerStyle()).isEqualTo(BeerStyle.IPA);
        assertThat(patched.getUpc()).isEqualTo("77777");
        assertThat(patched.getPrice()).isEqualTo(new BigDecimal("3.33"));
        assertThat(patched.getQuantityOnHand()).isEqualTo(7);
        assertThat(patched.getVersion()).isEqualTo(3);
    }

    @Test
    void patchBeerLeavesFieldsUnchangedWhenNotProvided() {
        BeerDTO existing = firstBeer();

        BeerDTO patched = beerService.patchBeer(existing.getId(), BeerDTO.builder().build()).orElseThrow();

        assertThat(patched.getBeerName()).isEqualTo(existing.getBeerName());
        assertThat(patched.getBeerStyle()).isEqualTo(existing.getBeerStyle());
        assertThat(patched.getUpc()).isEqualTo(existing.getUpc());
    }

    @Test
    void patchBeerDoesNotUpdateTimestampWhenNothingChanged() {
        BeerDTO existing = firstBeer();

        BeerDTO patched = beerService.patchBeer(existing.getId(), existing).orElseThrow();

        assertThat(patched.getUpdateDate()).isEqualTo(existing.getUpdateDate());
    }

    private List<BeerDTO> allBeers() {
        return beerService.listBeers("", BeerStyle.LAGER, false, 0, 10).getContent();
    }

    private BeerDTO firstBeer() {
        return allBeers().getFirst();
    }

}
