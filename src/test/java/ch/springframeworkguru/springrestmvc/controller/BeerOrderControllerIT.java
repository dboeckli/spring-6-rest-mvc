package ch.springframeworkguru.springrestmvc.controller;

import ch.springframeworkguru.springrestmvc.entity.BeerOrder;
import ch.springframeworkguru.springrestmvc.repository.BeerOrderRepository;
import ch.springframeworkguru.springrestmvc.service.dto.BeerOrderDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;

import static ch.springframeworkguru.springrestmvc.controller.BeerOrderController.GET_BEER_ORDER_BY_ID;
import static ch.springframeworkguru.springrestmvc.controller.BeerOrderController.LIST_BEER_ORDERS;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class BeerOrderControllerIT {

    @Value("${controllers.beer-order-controller.request-path}")
    private String requestPath;

    @Autowired
    WebApplicationContext wac;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
            .apply(springSecurity())
            .build();
    }

    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRequestPostProcessor =
        jwt().jwt(jwt -> {
            jwt.claims(claims -> {
                    claims.put("scope", "message.read");
                    claims.put("scope", "message.write");
                })
                .subject("messaging-client")
                .notBefore(Instant.now().minusSeconds(5l));
        });

    @Test
    void testListBeerOrders() throws Exception {
        MvcResult result = mockMvc.perform(get(requestPath + LIST_BEER_ORDERS)
                .with(jwtRequestPostProcessor))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.size()", greaterThan(0)))
            .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        // TODO. DOES NOT WORK. PageImpl is not Serializable
        // TODO: SEE https://stackoverflow.com/questions/52490399/spring-boot-page-deserialization-pageimpl-no-constructor
        //PageImpl<List<BeerOrderDTO>> beerOrderDTOSs = objectMapper.readValue(jsonResponse, new TypeReference<>() {}); 
    }

    @Test
    void testGetBeerOrderById() throws Exception {
        BeerOrder beerOrder = beerOrderRepository.findAll().get(0);

        MvcResult result = mockMvc.perform(get(requestPath + GET_BEER_ORDER_BY_ID, beerOrder.getId())
                .with(jwtRequestPostProcessor))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(beerOrder.getId().toString())))
            .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        BeerOrderDTO beerOrderDTO = objectMapper.readValue(jsonResponse, new TypeReference<>() {});
        
        assertEquals(beerOrder.getId(), beerOrderDTO.getId());
    }
}
