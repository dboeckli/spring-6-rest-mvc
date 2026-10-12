package ch.dboeckli.spring.restmvc.service;

import ch.guru.springframework.spring6restmvcapi.dto.CustomerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerServiceImplTest {

    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl();
    }

    @Test
    void listCustomersReturnsAllCustomers() {
        assertThat(customerService.listCustomers()).hasSize(3);
    }

    @Test
    void getCustomerByIdReturnsExistingCustomer() {
        CustomerDTO existing = firstCustomer();

        assertThat(customerService.getCustomerById(existing.getId())).isPresent();
        assertThat(customerService.getCustomerById(existing.getId()).orElseThrow().getName())
            .isEqualTo(existing.getName());
    }

    @Test
    void saveNewCustomerAddsCustomerToStore() {
        CustomerDTO saved = customerService
            .saveNewCustomer(CustomerDTO.builder().name("New Customer").version("2.0").build());

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("New Customer");
        assertThat(customerService.listCustomers()).hasSize(4);
        assertThat(customerService.getCustomerById(saved.getId())).isPresent();
    }

    @Test
    void editCustomerUpdatesFields() {
        CustomerDTO existing = firstCustomer();

        CustomerDTO edited = customerService
            .editCustomer(existing.getId(), CustomerDTO.builder().name("Edited").version("3.0").build())
            .orElseThrow();

        assertThat(edited.getName()).isEqualTo("Edited");
        assertThat(edited.getVersion()).isEqualTo("3.0");
        assertThat(customerService.getCustomerById(existing.getId()).orElseThrow().getName()).isEqualTo("Edited");
    }

    @Test
    void patchCustomerUpdatesOnlyProvidedFields() {
        CustomerDTO existing = firstCustomer();

        CustomerDTO patched = customerService
            .patchCustomer(existing.getId(), CustomerDTO.builder().name("Patched").version("4.0").build())
            .orElseThrow();

        assertThat(patched.getName()).isEqualTo("Patched");
        assertThat(patched.getVersion()).isEqualTo("4.0");
    }

    @Test
    void patchCustomerLeavesFieldsUnchangedWhenNotProvided() {
        CustomerDTO existing = firstCustomer();

        CustomerDTO patched = customerService.patchCustomer(existing.getId(), CustomerDTO.builder().build())
            .orElseThrow();

        assertThat(patched.getName()).isEqualTo(existing.getName());
        assertThat(patched.getVersion()).isEqualTo(existing.getVersion());
    }

    @Test
    void patchCustomerDoesNotUpdateTimestampWhenNothingChanged() {
        CustomerDTO existing = firstCustomer();

        CustomerDTO patched = customerService.patchCustomer(existing.getId(), existing).orElseThrow();

        assertThat(patched.getUpdateDate()).isEqualTo(existing.getUpdateDate());
    }

    @Test
    void deleteCustomerRemovesCustomerFromStore() {
        CustomerDTO existing = firstCustomer();

        assertThat(customerService.deleteCustomer(existing.getId())).isTrue();
        assertThat(customerService.listCustomers()).hasSize(2);
    }

    private CustomerDTO firstCustomer() {
        return customerService.listCustomers().getFirst();
    }

}
