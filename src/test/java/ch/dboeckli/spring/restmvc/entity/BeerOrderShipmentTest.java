package ch.dboeckli.spring.restmvc.entity;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BeerOrderShipmentTest {

    private static final UUID ID = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");

    private static final Timestamp TIMESTAMP = Timestamp.valueOf(LocalDateTime.of(2024, 1, 2, 3, 4, 5));

    @Test
    void builderMapsAllFields() {
        BeerOrderShipment shipment = BeerOrderShipment.builder()
            .id(ID)
            .version(2L)
            .trackingNumber("track-1")
            .createdDate(TIMESTAMP)
            .lastModifiedDate(TIMESTAMP)
            .build();

        assertThat(shipment.getId()).isEqualTo(ID);
        assertThat(shipment.getVersion()).isEqualTo(2L);
        assertThat(shipment.getTrackingNumber()).isEqualTo("track-1");
        assertThat(shipment.getCreatedDate()).isEqualTo(TIMESTAMP);
        assertThat(shipment.getLastModifiedDate()).isEqualTo(TIMESTAMP);
    }

    @Test
    void hashCodeIsStable() {
        BeerOrderShipment shipment = BeerOrderShipment.builder().trackingNumber("track-1").build();

        assertThat(shipment.hashCode()).isEqualTo(shipment.hashCode());
    }

}
