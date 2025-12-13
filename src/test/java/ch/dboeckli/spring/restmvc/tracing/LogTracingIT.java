package ch.dboeckli.spring.restmvc.tracing;

import ch.dboeckli.spring.restmvc.event.listener.BeerCreatedListener;
import ch.dboeckli.spring.restmvc.rest.controller.BeerController;
import ch.guru.springframework.spring6restmvcapi.dto.BeerDTO;
import ch.guru.springframework.spring6restmvcapi.dto.BeerStyle;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.micrometer.tracing.test.autoconfigure.AutoConfigureTracing;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureMockMvc
@AutoConfigureTracing
public class LogTracingIT {

    @Autowired
    MockMvc mockMvc;

    @Value("${controllers.beer-controller.request-path}")
    private String requestPath;

    @Autowired
    private ObjectMapper objectMapper;

    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRequestPostProcessor =
        jwt().jwt(jwt -> jwt.claims(claims -> claims.put("scope", "message.write"))
            .subject("messaging-client")
            .notBefore(Instant.now().minusSeconds(5L)));

    @Test
    void test_logsMessage_listBeers() throws Exception {
        Logger logger = (Logger) LoggerFactory.getLogger(BeerController.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);


        mockMvc.perform(get(requestPath + "/listBeers")
                .with(jwtRequestPostProcessor)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        List<ILoggingEvent> logEvents = listAppender.list;
        assertAll(
            () -> assertNotNull(logEvents),
            () -> assertEquals(1, logEvents.size()),
            () -> assertThat(logEvents.getFirst().getFormattedMessage()).isEqualTo("listBeers beerName=null"),
            () -> assertThat(logEvents.getFirst().getMDCPropertyMap().get("traceId")).isNotBlank().matches("[0-9a-f]{32}"),
            () -> assertThat(logEvents.getFirst().getMDCPropertyMap().get("spanId")).as("span_id").isNotBlank().matches("[0-9a-f]{16}")
        );

        logger.detachAppender(listAppender);
        listAppender.stop();
    }

    @Test
    void test_logsMessages_createBeer() throws Exception {
        Logger beerControllerLogger = (Logger) LoggerFactory.getLogger(BeerController.class);
        Logger beerCreatedListenerLogger = (Logger) LoggerFactory.getLogger(BeerCreatedListener.class);

        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        beerControllerLogger.addAppender(listAppender);
        beerCreatedListenerLogger.addAppender(listAppender);

        BeerDTO beerToCreate = BeerDTO
            .builder()
            .beerName("pilgrim")
            .beerStyle(BeerStyle.PALE_ALE)
            .upc("123456789")
            .price(BigDecimal.valueOf(5L))
            .build();

        mockMvc.perform(post(requestPath + "/createBeer")
                .with(jwtRequestPostProcessor)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerToCreate)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"));

        List<ILoggingEvent> logEvents = listAppender.list;
        await()
            .atMost(5, SECONDS)
            .pollInterval(100, java.util.concurrent.TimeUnit.MILLISECONDS)
            .untilAsserted(() -> assertEquals(5, logEvents.size()));
        assertAll(
            () -> assertNotNull(logEvents),
            () -> assertEquals(5, logEvents.size()),
            () -> assertThat(logEvents.getFirst().getFormattedMessage()).startsWith("createBeer newBeer=BeerDTO"),
            () -> assertThat(logEvents.getFirst().getMDCPropertyMap().get("traceId")).isNotBlank().matches("[0-9a-f]{32}"),
            () -> assertThat(logEvents.getFirst().getMDCPropertyMap().get("spanId")).isNotBlank().matches("[0-9a-f]{16}"),
            () -> assertThat(logEvents.get(1).getFormattedMessage()).startsWith("Beer Event Listener called for event"),
            () -> assertThat(logEvents.get(1).getMDCPropertyMap().get("traceId")).isNotBlank().matches("[0-9a-f]{32}"),
            () -> assertThat(logEvents.get(1).getMDCPropertyMap().get("spanId")).isNotBlank().matches("[0-9a-f]{16}"),

            () -> assertThat(logEvents.getFirst().getMDCPropertyMap().get("traceId")).isEqualTo(logEvents.get(1).getMDCPropertyMap().get("traceId")),
            () -> assertThat(logEvents.getFirst().getMDCPropertyMap().get("spanId")).isNotEqualTo(logEvents.get(1).getMDCPropertyMap().get("spanId")),

            () -> assertThat(logEvents.getFirst().getThreadName()).isNotEqualTo(logEvents.get(1).getThreadName())
        );

        beerControllerLogger.detachAppender(listAppender);
        beerCreatedListenerLogger.detachAppender(listAppender);
        listAppender.stop();
    }

}
