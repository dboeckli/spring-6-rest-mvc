package ch.springframeworkguru.springrestmvc.mapper;

import ch.springframeworkguru.springrestmvc.entity.Customer;
import ch.springframeworkguru.springrestmvc.service.dto.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {

    Customer customerDtoToCustomer(CustomerDTO customerDto);

    CustomerDTO customerToCustomerDto(Customer customer);
}
