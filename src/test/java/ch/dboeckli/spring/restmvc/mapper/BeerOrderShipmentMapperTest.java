package ch.dboeckli.spring.restmvc.mapper;

import ch.dboeckli.spring.restmvc.entity.BeerOrderShipment;
import ch.guru.springframework.spring6restmvcapi.dto.create.BeerOrderShipmentDTO;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BeerOrderShipmentMapperTest {

    private static final UUID ID = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");

    private static final Timestamp CREATED_DATE = Timestamp.valueOf(LocalDateTime.of(2024, 1, 2, 3, 4, 5));

    private static final Timestamp LAST_MODIFIED_DATE = Timestamp.valueOf(LocalDateTime.of(2024, 2, 3, 4, 5, 6));

    private final BeerOrderShipmentMapper mapper = new BeerOrderShipmentMapperImpl();

    @Test
    void beerOrderShipmentDtoToBeerOrderShipmentReturnsNullForNullDto() {
        assertThat(mapper.beerOrderShipmentDtoToBeerOrderShipment(null)).isNull();
    }

    @Test
    void beerOrderShipmentDtoToBeerOrderShipmentMapsAllFields() {
        BeerOrderShipment shipment = mapper.beerOrderShipmentDtoToBeerOrderShipment(fullDto());

        assertThat(shipment.getId()).isEqualTo(ID);
        assertThat(shipment.getVersion()).isEqualTo(2L);
        assertThat(shipment.getTrackingNumber()).isEqualTo("track-1");
        assertThat(shipment.getCreatedDate()).isEqualTo(CREATED_DATE);
        assertThat(shipment.getLastModifiedDate()).isEqualTo(LAST_MODIFIED_DATE);
        assertThat(shipment.getBeerOrder()).isNull();
    }

    @Test
    void beerOrderShipmentToBeerOrderShipmentDtoReturnsNullForNullEntity() {
        assertThat(mapper.beerOrderShipmentToBeerOrderShipmentDto(null)).isNull();
    }

    @Test
    void beerOrderShipmentToBeerOrderShipmentDtoMapsAllFields() {
        BeerOrderShipmentDTO dto = mapper.beerOrderShipmentToBeerOrderShipmentDto(fullEntity());

        assertThat(dto.getId()).isEqualTo(ID);
        assertThat(dto.getVersion()).isEqualTo(2L);
        assertThat(dto.getTrackingNumber()).isEqualTo("track-1");
        assertThat(dto.getCreatedDate()).isEqualTo(CREATED_DATE);
        assertThat(dto.getLastModifiedDate()).isEqualTo(LAST_MODIFIED_DATE);
    }

    private static BeerOrderShipmentDTO fullDto() {
        return BeerOrderShipmentDTO.builder()
            .id(ID)
            .version(2L)
            .trackingNumber("track-1")
            .createdDate(CREATED_DATE)
            .lastModifiedDate(LAST_MODIFIED_DATE)
            .build();
    }

    private static BeerOrderShipment fullEntity() {
        return BeerOrderShipment.builder()
            .id(ID)
            .version(2L)
            .trackingNumber("track-1")
            .createdDate(CREATED_DATE)
            .lastModifiedDate(LAST_MODIFIED_DATE)
            .build();
    }

}
