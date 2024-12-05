package ch.springframeworkguru.springrestmvc.controller;

import ch.springframeworkguru.springrestmvc.config.SpringSecurityConfigRest;
import ch.springframeworkguru.springrestmvc.service.CustomerService;
import ch.springframeworkguru.springrestmvc.service.CustomerServiceImpl;
import ch.springframeworkguru.springrestmvc.service.dto.CustomerDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@Import(SpringSecurityConfigRest.class)
@ActiveProfiles("test")
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    CustomerService customerService;

    @Value("${controllers.customer-controller.request-path}")
    private String requestPath;

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    CustomerServiceImpl customerServiceImpl;

    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRequestPostProcessor =
        jwt().jwt(jwt -> {
            jwt.claims(claims -> {
                    claims.put("scope", "message.read");
                    claims.put("scope", "message.write");
                })
                .subject("messaging-client")
                .notBefore(Instant.now().minusSeconds(5l));
        });

    @BeforeEach
    void setUp() {
        customerServiceImpl = new CustomerServiceImpl();
    }

    @Test
    void testGetCustomerById() throws Exception {
        CustomerDTO givenCustomer = customerServiceImpl.listCustomers().getFirst();
        given(customerService.getCustomerById(givenCustomer.getId())).willReturn(Optional.of(givenCustomer));

        mockMvc.perform(get(requestPath + "/getCustomerById/" + givenCustomer.getId())
                .with(jwtRequestPostProcessor)
                //.with(httpBasic(username,password))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(givenCustomer.getId().toString())))
                .andExpect(jsonPath("$.customerName", is(givenCustomer.getCustomerName())))
                .andExpect(content().json(objectMapper.writeValueAsString(givenCustomer)));  // oder das ganze Object
    }

    @Test
    void testGetCustomerByIdWithWrongUsernameAndPassword() throws Exception {
        CustomerDTO givenCustomer = customerServiceImpl.listCustomers().getFirst();
        given(customerService.getCustomerById(givenCustomer.getId())).willReturn(Optional.of(givenCustomer));

        mockMvc.perform(get(requestPath + "/getCustomerById/" + givenCustomer.getId())
                .with(httpBasic("wrongusername","wrongpassword"))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized()); 
    }

    @Test
    void testGetCustomerByIdAndThrowsNotFoundException() throws Exception {
        //given(customerService.getCustomerById(any())).willThrow(NotfoundException.class);
        given(customerService.getCustomerById(any())).willReturn(Optional.empty());

        mockMvc.perform(get(requestPath + "/getCustomerById/" + UUID.randomUUID())
                .with(jwtRequestPostProcessor)
                //.with(httpBasic(username,password))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateCustomer() throws Exception {
        CustomerDTO givenCustomer = customerServiceImpl.listCustomers().getFirst();
        givenCustomer.setCustomerName("pumukel");
        givenCustomer.setId(null);

        given(customerService.saveNewCustomer(any(CustomerDTO.class))).willReturn(givenCustomer);

        mockMvc.perform(post(requestPath + "/createCustomer")
                .with(jwtRequestPostProcessor)
                //.with(httpBasic(username,password))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(givenCustomer)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    void testEditCustomer() throws Exception {
        CustomerDTO givenCustomerToEdit = customerServiceImpl.listCustomers().getFirst();
        givenCustomerToEdit.setCustomerName("veryveryNew Customer");

        given(customerService.editCustomer(givenCustomerToEdit.getId(), givenCustomerToEdit)).willReturn(Optional.of(givenCustomerToEdit));

        mockMvc.perform(put(requestPath + "/editCustomer/" + givenCustomerToEdit.getId())
                .with(jwtRequestPostProcessor)
                //.with(httpBasic(username,password))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(givenCustomerToEdit)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(givenCustomerToEdit)));  // oder das ganze Object;
    }

    @Test
    void testDeleteCustomer() throws Exception {
        CustomerDTO givenCustomerToDelete = customerServiceImpl.listCustomers().getFirst();

        given(customerService.deleteCustomer(givenCustomerToDelete.getId())).willReturn(true);

        mockMvc.perform(delete(requestPath + "/deleteCustomer/" + givenCustomerToDelete.getId())
                .with(jwtRequestPostProcessor)
                //.with(httpBasic(username,password))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(givenCustomerToDelete)))
                .andExpect(status().isOk());  
    }

    @Test
    void testListCustomer() throws Exception {
        List<CustomerDTO> givenCustomers = customerServiceImpl.listCustomers();
        given(customerService.listCustomers()).willReturn(givenCustomers);

        MvcResult result = mockMvc.perform(get(requestPath + "/listCustomer")
                .with(jwtRequestPostProcessor)
                //.with(httpBasic(username,password))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3))
            .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<CustomerDTO> customerList = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        // Manuelle Assertions
        assertEquals(3, customerList.size());
    }

    @Test
    void testPatchCustomer() throws Exception {
        CustomerDTO givenCustomerToPatch = customerServiceImpl.listCustomers().getFirst();
        givenCustomerToPatch.setCustomerName("patchedCustomerName");

        given(customerService.patchCustomer(givenCustomerToPatch.getId(), givenCustomerToPatch)).willReturn(Optional.of(givenCustomerToPatch));

        mockMvc.perform(patch(requestPath + "/patchCustomer/" + givenCustomerToPatch.getId())
                .with(jwtRequestPostProcessor)
                //.with(httpBasic(username,password))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(givenCustomerToPatch)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(givenCustomerToPatch)));  // oder das
    }
}
