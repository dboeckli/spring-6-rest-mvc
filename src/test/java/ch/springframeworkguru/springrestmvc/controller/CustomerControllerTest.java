package ch.springframeworkguru.springrestmvc.controller;

import ch.springframeworkguru.springrestmvc.model.Customer;
import ch.springframeworkguru.springrestmvc.service.CustomerService;
import ch.springframeworkguru.springrestmvc.service.CustomerServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@ActiveProfiles("test")
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    CustomerService customerService;

    @Value("${controllers.customer-controller.request-path}")
    private String requestPath;

    CustomerServiceImpl customerServiceImpl;

    @BeforeEach
    void setUp() {
        customerServiceImpl = new CustomerServiceImpl();
    }

    @Test
    void testGetCustomerById() throws Exception {
        Customer givenCustomer = customerServiceImpl.listCustomers().getFirst();
        given(customerService.getCustomerById(givenCustomer.getId())).willReturn(Optional.of(givenCustomer));

        mockMvc.perform(get(requestPath + "/getCustomerById/" + givenCustomer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(givenCustomer.getId().toString())))
                .andExpect(jsonPath("$.customerName", is(givenCustomer.getCustomerName())))
                .andExpect(content().json(objectMapper.writeValueAsString(givenCustomer)));  // oder das ganze Object
    }

    @Test
    void testGetCustomerByIdAndThrowsNotFoundException() throws Exception {
        //given(customerService.getCustomerById(any())).willThrow(NotfoundException.class);
        given(customerService.getCustomerById(any())).willReturn(Optional.empty());

        mockMvc.perform(get(requestPath + "/getCustomerById/" + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateCustomer() throws Exception {
        Customer givenCustomer = customerServiceImpl.listCustomers().getFirst();
        givenCustomer.setCustomerName("pumukel");
        givenCustomer.setId(null);

        given(customerService.saveNewCustomer(any(Customer.class))).willReturn(givenCustomer);

        mockMvc.perform(post(requestPath + "/createCustomer")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenCustomer)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    void testEditCustomer() throws Exception {
        Customer givenCustomerToEdit = customerServiceImpl.listCustomers().getFirst();
        givenCustomerToEdit.setCustomerName("veryveryNew Customer");

        given(customerService.editCustomer(givenCustomerToEdit.getId(), givenCustomerToEdit)).willReturn(givenCustomerToEdit);

        mockMvc.perform(put(requestPath + "/editCustomer/" + givenCustomerToEdit.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenCustomerToEdit)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(givenCustomerToEdit)));  // oder das ganze Object;
    }

    @Test
    void testDeleteCustomer() throws Exception {
        Customer givenCustomerToDelete = customerServiceImpl.listCustomers().getFirst();

        given(customerService.deleteCustomer(givenCustomerToDelete.getId())).willReturn(givenCustomerToDelete);

        mockMvc.perform(delete(requestPath + "/deleteCustomer/" + givenCustomerToDelete.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenCustomerToDelete)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(givenCustomerToDelete)));  // oder das
    }

    @Test
    void listCustomer() {
    }

    @Test
    void testPatchCustomer() throws Exception {
        Customer givenCustomerToPatch = customerServiceImpl.listCustomers().getFirst();
        givenCustomerToPatch.setCustomerName("patchedCustomerName");

        given(customerService.patchCustomer(givenCustomerToPatch.getId(), givenCustomerToPatch)).willReturn(givenCustomerToPatch);

        mockMvc.perform(patch(requestPath + "/patchCustomer/" + givenCustomerToPatch.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenCustomerToPatch)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(givenCustomerToPatch)));  // oder das
    }
}