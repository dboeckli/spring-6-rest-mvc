package ch.springframeworkguru.springrestmvc.controller;

import ch.guru.springframework.spring6restmvcapi.dto.BeerOrderDTO;
import ch.guru.springframework.spring6restmvcapi.dto.create.BeerOrderCreateDTO;
import ch.guru.springframework.spring6restmvcapi.dto.create.BeerOrderLineCreateDTO;
import ch.guru.springframework.spring6restmvcapi.dto.update.BeerOrderLineUpdateDTO;
import ch.guru.springframework.spring6restmvcapi.dto.update.BeerOrderShipmentUpdateDTO;
import ch.guru.springframework.spring6restmvcapi.dto.update.BeerOrderUpdateDTO;
import ch.springframeworkguru.springrestmvc.entity.Beer;
import ch.springframeworkguru.springrestmvc.entity.BeerOrder;
import ch.springframeworkguru.springrestmvc.entity.Customer;
import ch.springframeworkguru.springrestmvc.repository.BeerOrderRepository;
import ch.springframeworkguru.springrestmvc.repository.BeerRepository;
import ch.springframeworkguru.springrestmvc.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static ch.springframeworkguru.springrestmvc.controller.BeerOrderController.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class BeerOrderControllerIT {

    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRequestPostProcessor =
        jwt().jwt(jwt -> jwt.claims(claims -> {
                claims.put(OAuth2TokenIntrospectionClaimNames.SCOPE, "message.read");
                claims.put(OAuth2TokenIntrospectionClaimNames.SCOPE, "message.write");
            })
            .subject("messaging-client")
            .notBefore(Instant.now().minusSeconds(5L)));
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
    @Value("${controllers.beer-order-controller.request-path}")
    private String requestPath;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
            .apply(springSecurity())
            .build();
    }

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
        BeerOrder beerOrder = beerOrderRepository.findAll().getFirst();

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
    @Transactional
    void testCreateBeerOrder() throws Exception {
        Customer customer = customerRepository.findAll().getFirst();
        Beer beer = beerRepository.findAll().getFirst();

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

        assertNotNull(beerOrderDTO.getId());
        assertNotNull(beerOrderDTO.getCustomer());
        assertEquals(1, beerOrderDTO.getBeerOrderLines().size());
    }

    @Test
    @Transactional
    void testUpdateBeerOrder() throws Exception {
        BeerOrder beerOrderToUpdate = beerOrderRepository.findAll().getFirst();

        Set<BeerOrderLineUpdateDTO> lines = new HashSet<>();
        beerOrderToUpdate.getBeerOrderLines().forEach(beerOrderLine -> lines.add(BeerOrderLineUpdateDTO.builder()
            .id(beerOrderLine.getId())
            .beerId(beerOrderLine.getBeer().getId())
            .orderQuantity(beerOrderLine.getOrderQuantity())
            .quantityAllocated(beerOrderLine.getQuantityAllocated())
            .build()));

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
                .content(objectMapper.writeValueAsString(beerOrderUpdateDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerRef", is("UpdatedTestRef")))
            .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        BeerOrderDTO beerOrderDTO = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });

        assertNotNull(beerOrderDTO.getId());
    }

    @Test
    @Transactional
    void testDeleteBeerOrder() throws Exception {
        BeerOrder beerOrderToDelete = beerOrderRepository.findAll().getFirst();

        mockMvc.perform(delete(requestPath + DELETE_BEER_ORDER_BY_ID, beerOrderToDelete.getId())
                .with(jwtRequestPostProcessor))
            .andExpect(status().isNoContent());

        assertTrue(beerOrderRepository.findById(beerOrderToDelete.getId()).isEmpty());
        assertFalse(beerOrderRepository.existsById(beerOrderToDelete.getId()));

        mockMvc.perform(delete(requestPath + DELETE_BEER_ORDER_BY_ID, beerOrderToDelete.getId())
                .with(jwtRequestPostProcessor))
            .andExpect(status().isNotFound());
    }
}
