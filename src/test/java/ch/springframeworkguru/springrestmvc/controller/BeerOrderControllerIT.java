package ch.springframeworkguru.springrestmvc.controller;

import ch.springframeworkguru.springrestmvc.entity.Beer;
import ch.springframeworkguru.springrestmvc.entity.BeerOrder;
import ch.springframeworkguru.springrestmvc.entity.Customer;
import ch.springframeworkguru.springrestmvc.repository.BeerOrderRepository;
import ch.springframeworkguru.springrestmvc.repository.BeerRepository;
import ch.springframeworkguru.springrestmvc.repository.CustomerRepository;
import ch.springframeworkguru.springrestmvc.service.dto.create.BeerOrderCreateDTO;
import ch.springframeworkguru.springrestmvc.service.dto.BeerOrderDTO;
import ch.springframeworkguru.springrestmvc.service.dto.create.BeerOrderLineCreateDTO;
import ch.springframeworkguru.springrestmvc.service.dto.update.BeerOrderLineUpdateDTO;
import ch.springframeworkguru.springrestmvc.service.dto.update.BeerOrderShipmentUpdateDTO;
import ch.springframeworkguru.springrestmvc.service.dto.update.BeerOrderUpdateDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static ch.springframeworkguru.springrestmvc.controller.BeerOrderController.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerRepository beerRepository;

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
        BeerOrderDTO beerOrderDTO = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });

        assertEquals(beerOrder.getId(), beerOrderDTO.getId());
    }

    @Test
    void testCreateBeerOrder() throws Exception {
        Customer customer = customerRepository.findAll().get(0);
        Beer beer = beerRepository.findAll().get(0);

        BeerOrderCreateDTO newBeerOrderDTO = BeerOrderCreateDTO.builder()
            .customerId(customer.getId())
            .customerRef("ABC123")
            .beerOrderLines(Set.of(BeerOrderLineCreateDTO.builder()
                .beerId(beer.getId())
                .orderQuantity(1)
                .build()))
            .build();

        MvcResult result = mockMvc.perform(post(requestPath + CREATE_BEER_ORDER)
                .with(jwtRequestPostProcessor)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBeerOrderDTO)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(header().string("Location", matchesPattern(requestPath + GET_BEER_ORDER + "/[0-9a-fA-F-]{36}")))
            .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        BeerOrderDTO beerOrderDTO = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });

        // TODO: beerOrderlines and Customer are not persisted or wired into BeerOrder
        assertNotNull(beerOrderDTO.getId());
    }

    @Test
    // TODO: TEST IS CURRENTLY FAILING. Controller and Service not yet implemented
    void testUpdateBeerOrder() throws Exception {
        BeerOrder beerOrderToUpdate = beerOrderRepository.findAll().get(0);

        Set<BeerOrderLineUpdateDTO> lines = new HashSet<>();
        beerOrderToUpdate.getBeerOrderLines().forEach(beerOrderLine -> {
            lines.add(BeerOrderLineUpdateDTO.builder()
                .id(beerOrderLine.getId())
                .beerId(beerOrderLine.getBeer().getId())
                .orderQuantity(beerOrderLine.getOrderQuantity())
                .quantityAllocated(beerOrderLine.getQuantityAllocated())
                .build());
        });

        BeerOrderUpdateDTO beerOrderUpdateDTO = BeerOrderUpdateDTO.builder()
            .customerId(beerOrderToUpdate.getCustomer().getId())
            .customerRef("UpdatedTestRef")
            .beerOrderLines(lines)
            .beerOrderShipment(BeerOrderShipmentUpdateDTO.builder()
                .trackingNumber("123456")
                .build())
            .build();

        MvcResult result = mockMvc.perform(put(requestPath + UPDATE_BEER_ORDER_BY_ID, beerOrderToUpdate.getId())
                .with(jwtRequestPostProcessor)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerOrderToUpdate)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerRef", is("UpdatedTestRef")))
            .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        BeerOrderDTO beerOrderDTO = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });

        assertNotNull(beerOrderDTO.getId());
    }
}
