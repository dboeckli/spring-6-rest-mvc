package ch.dboeckli.spring.restmvc.mapper;

import ch.dboeckli.spring.restmvc.entity.Beer;
import ch.dboeckli.spring.restmvc.entity.BeerAudit;
import ch.guru.springframework.spring6restmvcapi.dto.BeerDTO;
import ch.guru.springframework.spring6restmvcapi.dto.BeerStyle;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BeerMapperTest {

    private static final UUID ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    private static final LocalDateTime CREATED_DATE = LocalDateTime.of(2024, 1, 2, 3, 4, 5);

    private static final LocalDateTime UPDATE_DATE = LocalDateTime.of(2024, 2, 3, 4, 5, 6);

    private final BeerMapper mapper = new BeerMapperImpl();

    @Test
    void beerDtoToBeerReturnsNullForNullDto() {
        assertThat(mapper.beerDtoToBeer(null)).isNull();
    }

    @Test
    void beerDtoToBeerMapsAllFields() {
        Beer beer = mapper.beerDtoToBeer(fullBeerDto());

        assertThat(beer.getId()).isEqualTo(ID);
        assertThat(beer.getVersion()).isEqualTo(3);
        assertThat(beer.getBeerName()).isEqualTo("Beer");
        assertThat(beer.getBeerStyle()).isEqualTo(BeerStyle.IPA);
        assertThat(beer.getUpc()).isEqualTo("12345");
        assertThat(beer.getQuantityOnHand()).isEqualTo(42);
        assertThat(beer.getPrice()).isEqualByComparingTo("9.99");
        assertThat(beer.getCreatedDate()).isEqualTo(CREATED_DATE);
        assertThat(beer.getUpdateDate()).isEqualTo(UPDATE_DATE);
        assertThat(beer.getCategories()).isEmpty();
        assertThat(beer.getBeerOrderLines()).isNull();
    }

    @Test
    void beerToBeerDtoReturnsNullForNullEntity() {
        assertThat(mapper.beerToBeerDto(null)).isNull();
    }

    @Test
    void beerToBeerDtoMapsAllFields() {
        BeerDTO dto = mapper.beerToBeerDto(fullBeer());

        assertThat(dto.getId()).isEqualTo(ID);
        assertThat(dto.getVersion()).isEqualTo(3);
        assertThat(dto.getBeerName()).isEqualTo("Beer");
        assertThat(dto.getBeerStyle()).isEqualTo(BeerStyle.IPA);
        assertThat(dto.getUpc()).isEqualTo("12345");
        assertThat(dto.getQuantityOnHand()).isEqualTo(42);
        assertThat(dto.getPrice()).isEqualByComparingTo("9.99");
        assertThat(dto.getCreatedDate()).isEqualTo(CREATED_DATE);
        assertThat(dto.getUpdateDate()).isEqualTo(UPDATE_DATE);
    }

    @Test
    void beerToBeerAuditReturnsNullForNullEntity() {
        assertThat(mapper.beerToBeerAudit(null)).isNull();
    }

    @Test
    void beerToBeerAuditMapsAllFields() {
        BeerAudit audit = mapper.beerToBeerAudit(fullBeer());

        assertThat(audit.getAuditId()).isNull();
        assertThat(audit.getId()).isEqualTo(ID);
        assertThat(audit.getVersion()).isEqualTo(3);
        assertThat(audit.getBeerName()).isEqualTo("Beer");
        assertThat(audit.getBeerStyle()).isEqualTo(BeerStyle.IPA);
        assertThat(audit.getUpc()).isEqualTo("12345");
        assertThat(audit.getQuantityOnHand()).isEqualTo(42);
        assertThat(audit.getPrice()).isEqualByComparingTo("9.99");
        assertThat(audit.getCreatedDate()).isEqualTo(CREATED_DATE);
        assertThat(audit.getUpdateDate()).isEqualTo(UPDATE_DATE);
        assertThat(audit.getCreatedDateAudit()).isNull();
        assertThat(audit.getPrincipalName()).isNull();
        assertThat(audit.getAuditEventType()).isNull();
    }

    private static BeerDTO fullBeerDto() {
        return BeerDTO.builder()
            .id(ID)
            .version(3)
            .beerName("Beer")
            .beerStyle(BeerStyle.IPA)
            .upc("12345")
            .quantityOnHand(42)
            .price(new BigDecimal("9.99"))
            .createdDate(CREATED_DATE)
            .updateDate(UPDATE_DATE)
            .build();
    }

    private static Beer fullBeer() {
        return Beer.builder()
            .id(ID)
            .version(3)
            .beerName("Beer")
            .beerStyle(BeerStyle.IPA)
            .upc("12345")
            .quantityOnHand(42)
            .price(new BigDecimal("9.99"))
            .createdDate(CREATED_DATE)
            .updateDate(UPDATE_DATE)
            .build();
    }

}
