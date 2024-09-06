package ch.springframeworkguru.springrestmvc.service;

import ch.springframeworkguru.springrestmvc.service.dto.CustomerDTO;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final Map<UUID, CustomerDTO> customerMap;

    public CustomerServiceImpl() {
        this.customerMap = new HashMap<>();

        CustomerDTO customer1 = CustomerDTO.builder()
                .id(UUID.randomUUID())
                .customerName("Pumukel")
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .version("1.0")
                .build();

        CustomerDTO customer2 = CustomerDTO.builder()
                .id(UUID.randomUUID())
                .customerName("Pumukel")
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .version("1.0")
                .build();

        CustomerDTO customer3 = CustomerDTO.builder()
                .id(UUID.randomUUID())
                .customerName("Pumukel")
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .version("1.0")
                .build();

        customerMap.put(customer1.getId(), customer1);
        customerMap.put(customer2.getId(), customer2);
        customerMap.put(customer3.getId(), customer3);
    }

    @Override
    public List<CustomerDTO> listCustomers() {
        return new ArrayList<>(customerMap.values());
    }

    @Override
    public Optional<CustomerDTO> getCustomerById(UUID id) {
        return Optional.of(customerMap.get(id));
    }

    @Override
    public CustomerDTO saveNewCustomer(CustomerDTO newCustomer) {
        CustomerDTO customer = CustomerDTO.builder()
                .id(UUID.randomUUID())
                .customerName(newCustomer.getCustomerName())
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .version(newCustomer.getVersion())
                .build();
        customerMap.put(customer.getId(), customer);
        return customer;
    }

    @Override
    public CustomerDTO editCustomer(UUID customerId, CustomerDTO customerToEdit) {
        CustomerDTO editedCustomer = customerMap.get(customerId);
        editedCustomer.setCustomerName(customerToEdit.getCustomerName());
        editedCustomer.setVersion(customerToEdit.getVersion());
        customerMap.replace(customerId, editedCustomer);
        return editedCustomer;
    }

    @Override
    public CustomerDTO deleteCustomer(UUID customerId) {
        return customerMap.remove(customerId);
    }

    @Override
    public CustomerDTO patchCustomer(UUID customerId, CustomerDTO customer) {
        CustomerDTO customerToChange = customerMap.get(customerId);
        if (StringUtils.isNotEmpty(customer.getCustomerName())) {
            customerToChange.setCustomerName(customer.getCustomerName());
        }
        if (StringUtils.isNotEmpty(customer.getVersion())) {
            customerToChange.setVersion(customer.getVersion());
        }
        if (!customer.equals(customerToChange)) {
            customerToChange.setLastModifiedDate(LocalDateTime.now());
            customerMap.replace(customerId, customerToChange);
        }
        return customerToChange;
    }

}
