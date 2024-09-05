package ch.springframeworkguru.springrestmvc.service;

import ch.springframeworkguru.springrestmvc.model.Customer;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final Map<UUID, Customer> customerMap;

    public CustomerServiceImpl() {
        this.customerMap = new HashMap<>();

        Customer customer1 = Customer.builder()
                .id(UUID.randomUUID())
                .customerName("Pumukel")
                .createdDate(LocalDate.now())
                .lastModifiedDate(LocalDate.now())
                .version("1.0")
                .build();

        Customer customer2 = Customer.builder()
                .id(UUID.randomUUID())
                .customerName("Pumukel")
                .createdDate(LocalDate.now())
                .lastModifiedDate(LocalDate.now())
                .version("1.0")
                .build();

        Customer customer3 = Customer.builder()
                .id(UUID.randomUUID())
                .customerName("Pumukel")
                .createdDate(LocalDate.now())
                .lastModifiedDate(LocalDate.now())
                .version("1.0")
                .build();

        customerMap.put(customer1.getId(), customer1);
        customerMap.put(customer2.getId(), customer2);
        customerMap.put(customer3.getId(), customer3);
    }

    @Override
    public List<Customer> listCustomers() {
        return new ArrayList<>(customerMap.values());
    }

    @Override
    public Customer getCustomerById(UUID id) {
        return customerMap.get(id);
    }

    @Override
    public Customer saveNewCustomer(Customer newCustomer) {
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .customerName(newCustomer.getCustomerName())
                .createdDate(LocalDate.now())
                .lastModifiedDate(LocalDate.now())
                .version(newCustomer.getVersion())
                .build();
        customerMap.put(customer.getId(), customer);
        return customer;
    }

    @Override
    public Customer editCustomer(UUID customerId, Customer customerToEdit) {
        Customer editedCustomer = customerMap.get(customerId);
        editedCustomer.setCustomerName(customerToEdit.getCustomerName());
        editedCustomer.setVersion(customerToEdit.getVersion());
        customerMap.replace(customerId, editedCustomer);
        return editedCustomer;
    }

    @Override
    public Customer deleteCustomer(UUID customerId) {
        return customerMap.remove(customerId);
    }

    @Override
    public Customer patchCustomer(UUID customerId, Customer customer) {
        Customer customerToChange = customerMap.get(customerId);
        if (StringUtils.isNotEmpty(customer.getCustomerName())) {
            customerToChange.setCustomerName(customer.getCustomerName());
        }
        if (StringUtils.isNotEmpty(customer.getVersion())) {
            customerToChange.setVersion(customer.getVersion());
        }
        if (!customer.equals(customerToChange)) {
            customerToChange.setLastModifiedDate(LocalDate.now());
            customerMap.replace(customerId, customerToChange);
        }
        return customerToChange;
    }

}
