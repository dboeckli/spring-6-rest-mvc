package ch.springframeworkguru.springrestmvc.mapper;

import ch.guru.springframework.spring6restmvcapi.dto.CustomerDTO;
import ch.springframeworkguru.springrestmvc.entity.Customer;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {

    Customer customerDtoToCustomer(CustomerDTO customerDto);

    CustomerDTO customerToCustomerDto(Customer customer);
}
