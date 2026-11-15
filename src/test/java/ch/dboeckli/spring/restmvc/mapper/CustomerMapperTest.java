package ch.dboeckli.spring.restmvc.mapper;

import ch.dboeckli.spring.restmvc.entity.Customer;
import ch.guru.springframework.spring6restmvcapi.dto.CustomerDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerMapperTest {

    private static final UUID ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    private static final LocalDateTime CREATED_DATE = LocalDateTime.of(2024, 1, 2, 3, 4, 5);

    private static final LocalDateTime UPDATE_DATE = LocalDateTime.of(2024, 2, 3, 4, 5, 6);

    private final CustomerMapper mapper = new CustomerMapperImpl();

    @Test
    void customerDtoToCustomerReturnsNullForNullDto() {
        assertThat(mapper.customerDtoToCustomer(null)).isNull();
    }

    @Test
    void customerDtoToCustomerMapsAllFields() {
        Customer customer = mapper.customerDtoToCustomer(CustomerDTO.builder()
            .id(ID)
            .version("4")
            .name("Jane")
            .email("jane@example.com")
            .createdDate(CREATED_DATE)
            .updateDate(UPDATE_DATE)
            .build());

        assertThat(customer.getId()).isEqualTo(ID);
        assertThat(customer.getVersion()).isEqualTo(4);
        assertThat(customer.getName()).isEqualTo("Jane");
        assertThat(customer.getEmail()).isEqualTo("jane@example.com");
        assertThat(customer.getCreatedDate()).isEqualTo(CREATED_DATE);
        assertThat(customer.getUpdateDate()).isEqualTo(UPDATE_DATE);
    }

    @Test
    void customerDtoToCustomerLeavesVersionNullWhenNotProvided() {
        Customer customer = mapper.customerDtoToCustomer(CustomerDTO.builder().name("Jane").build());

        assertThat(customer.getVersion()).isNull();
    }

    @Test
    void customerToCustomerDtoReturnsNullForNullEntity() {
        assertThat(mapper.customerToCustomerDto(null)).isNull();
    }

    @Test
    void customerToCustomerDtoMapsAllFields() {
        CustomerDTO dto = mapper.customerToCustomerDto(Customer.builder()
            .id(ID)
            .version(4)
            .name("Jane")
            .email("jane@example.com")
            .createdDate(CREATED_DATE)
            .updateDate(UPDATE_DATE)
            .build());

        assertThat(dto.getId()).isEqualTo(ID);
        assertThat(dto.getVersion()).isEqualTo("4");
        assertThat(dto.getName()).isEqualTo("Jane");
        assertThat(dto.getEmail()).isEqualTo("jane@example.com");
        assertThat(dto.getCreatedDate()).isEqualTo(CREATED_DATE);
        assertThat(dto.getUpdateDate()).isEqualTo(UPDATE_DATE);
    }

    @Test
    void customerToCustomerDtoLeavesVersionNullWhenNotProvided() {
        CustomerDTO dto = mapper.customerToCustomerDto(Customer.builder().name("Jane").build());

        assertThat(dto.getVersion()).isNull();
    }

}
