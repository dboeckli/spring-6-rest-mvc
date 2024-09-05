package ch.springframeworkguru.springrestmvc.controller;

import ch.springframeworkguru.springrestmvc.model.Customer;
import ch.springframeworkguru.springrestmvc.service.CustomerService;
import ch.springframeworkguru.springrestmvc.service.CustomerServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    CustomerService customerService;

    CustomerServiceImpl customerServiceImpl;

    @BeforeEach
    void setUp() {
        customerServiceImpl = new CustomerServiceImpl();
    }

    @Test
    void testGetCustomerById() throws Exception {
        Customer givenCustomer = customerServiceImpl.listCustomers().getFirst();
        given(customerService.getCustomerById(givenCustomer.getId())).willReturn(givenCustomer);

        mockMvc.perform(get("/api/v1/customer/getCustomerById/" + givenCustomer.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(givenCustomer.getId().toString())))
                .andExpect(jsonPath("$.customerName", is(givenCustomer.getCustomerName())))
                .andExpect(content().json(objectMapper.writeValueAsString(givenCustomer)));  // oder das ganze Object
    }

    @Test
    void testCreateCustomer() throws Exception {
        Customer givenCustomer = customerServiceImpl.listCustomers().getFirst();
        givenCustomer.setCustomerName("pumukel");
        givenCustomer.setId(null);

        given(customerService.saveNewCustomer(any(Customer.class))).willReturn(givenCustomer);

        mockMvc.perform(post("/api/v1/customer/createCustomer")
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

        mockMvc.perform(put("/api/v1/customer/editCustomer/" + givenCustomerToEdit.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenCustomerToEdit)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(givenCustomerToEdit)));  // oder das ganze Object;
    }

    @Test
    void deleteCustomer() {
    }

    @Test
    void listCustomer() {
    }

    @Test
    void patchCustomer() {
    }
}