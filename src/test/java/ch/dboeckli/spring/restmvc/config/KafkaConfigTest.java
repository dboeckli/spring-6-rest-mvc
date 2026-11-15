package ch.dboeckli.spring.restmvc.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaConfigTest {

    @Test
    void topicNamesAreConfigured() {
        assertThat(KafkaConfig.ORDER_PLACED_TOPIC).isEqualTo("order.placed");
        assertThat(KafkaConfig.DRINK_REQUEST_ICE_COLD_TOPIC).isEqualTo("drink.request.icecold");
        assertThat(KafkaConfig.DRINK_REQUEST_COLD_TOPIC).isEqualTo("drink.request.cold");
        assertThat(KafkaConfig.DRINK_REQUEST_COOL_TOPIC).isEqualTo("drink.request.cool");
        assertThat(KafkaConfig.DRINK_PREPARED_TOPIC).isEqualTo("drink.prepared");
    }

}
