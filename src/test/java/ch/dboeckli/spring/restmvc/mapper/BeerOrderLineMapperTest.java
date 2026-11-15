package ch.dboeckli.spring.restmvc.mapper;

import ch.dboeckli.spring.restmvc.entity.Beer;
import ch.dboeckli.spring.restmvc.entity.BeerOrderLine;
import ch.guru.springframework.spring6restmvcapi.dto.BeerDTO;
import ch.guru.springframework.spring6restmvcapi.dto.BeerOrderLineDTO;
import ch.guru.springframework.spring6restmvcapi.dto.BeerOrderLineStatus;
import ch.guru.springframework.spring6restmvcapi.dto.BeerStyle;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BeerOrderLineMapperTest {

    private static final UUID LINE_ID = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

    private static final UUID BEER_ID = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");

    private static final Timestamp CREATED_DATE = Timestamp.valueOf(LocalDateTime.of(2024, 1, 2, 3, 4, 5));

    private static final Timestamp UPDATE_DATE = Timestamp.valueOf(LocalDateTime.of(2024, 2, 3, 4, 5, 6));

    private final BeerOrderLineMapper mapper = new BeerOrderLineMapperImpl();

    @Test
    void beerOrderLineDtoToBeerOrderLineReturnsNullForNullDto() {
        assertThat(mapper.beerOrderLineDtoToBeerOrderLine(null)).isNull();
    }

    @Test
    void beerOrderLineDtoToBeerOrderLineMapsAllFields() {
        BeerOrderLine line = mapper.beerOrderLineDtoToBeerOrderLine(fullLineDto());

        assertThat(line.getId()).isEqualTo(LINE_ID);
        assertThat(line.getVersion()).isEqualTo(1L);
        assertThat(line.getCreatedDate()).isEqualTo(CREATED_DATE);
        assertThat(line.getUpdateDate()).isEqualTo(UPDATE_DATE);
        assertThat(line.getOrderQuantity()).isEqualTo(5);
        assertThat(line.getQuantityAllocated()).isEqualTo(2);
        assertThat(line.getOrderLineStatus()).isEqualTo(BeerOrderLineStatus.NEW);
        assertThat(line.getBeer()).isNotNull();
        assertThat(line.getBeer().getId()).isEqualTo(BEER_ID);
    }

    @Test
    void beerOrderLineDtoToBeerOrderLineHandlesNullBeer() {
        BeerOrderLine line = mapper.beerOrderLineDtoToBeerOrderLine(BeerOrderLineDTO.builder().id(LINE_ID).build());

        assertThat(line.getBeer()).isNull();
    }

    @Test
    void beerOrderLineToBeerOrderLineDtoReturnsNullForNullEntity() {
        assertThat(mapper.beerOrderLineToBeerOrderLineDto(null)).isNull();
    }

    @Test
    void beerOrderLineToBeerOrderLineDtoMapsAllFields() {
        BeerOrderLineDTO dto = mapper.beerOrderLineToBeerOrderLineDto(fullLine());

        assertThat(dto.getId()).isEqualTo(LINE_ID);
        assertThat(dto.getVersion()).isEqualTo(1L);
        assertThat(dto.getCreatedDate()).isEqualTo(CREATED_DATE);
        assertThat(dto.getUpdateDate()).isEqualTo(UPDATE_DATE);
        assertThat(dto.getOrderQuantity()).isEqualTo(5);
        assertThat(dto.getQuantityAllocated()).isEqualTo(2);
        assertThat(dto.getOrderLineStatus()).isEqualTo(BeerOrderLineStatus.NEW);
        assertThat(dto.getBeer()).isNotNull();
        assertThat(dto.getBeer().getId()).isEqualTo(BEER_ID);
    }

    @Test
    void beerOrderLineToBeerOrderLineDtoHandlesNullBeer() {
        BeerOrderLineDTO dto = mapper.beerOrderLineToBeerOrderLineDto(BeerOrderLine.builder().id(LINE_ID).build());

        assertThat(dto.getBeer()).isNull();
    }

    private static BeerOrderLineDTO fullLineDto() {
        return BeerOrderLineDTO.builder()
            .id(LINE_ID)
            .version(1L)
            .createdDate(CREATED_DATE)
            .updateDate(UPDATE_DATE)
            .orderQuantity(5)
            .quantityAllocated(2)
            .orderLineStatus(BeerOrderLineStatus.NEW)
            .beer(BeerDTO.builder()
                .id(BEER_ID)
                .beerName("Beer")
                .beerStyle(BeerStyle.IPA)
                .price(new BigDecimal("9.99"))
                .build())
            .build();
    }

    private static BeerOrderLine fullLine() {
        return BeerOrderLine.builder()
            .id(LINE_ID)
            .version(1L)
            .createdDate(CREATED_DATE)
            .updateDate(UPDATE_DATE)
            .orderQuantity(5)
            .quantityAllocated(2)
            .orderLineStatus(BeerOrderLineStatus.NEW)
            .beer(Beer.builder().id(BEER_ID).beerName("Beer").beerStyle(BeerStyle.IPA).build())
            .build();
    }

}
