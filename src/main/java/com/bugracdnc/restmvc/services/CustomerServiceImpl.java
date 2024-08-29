package com.bugracdnc.restmvc.services;

import com.bugracdnc.restmvc.models.CustomerDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final Map<UUID, CustomerDTO> customerMap = new HashMap<>();

    public CustomerServiceImpl() {
        CustomerDTO customerDTO1 = CustomerDTO.builder().customerName("Ayse").id(UUID.randomUUID()).version(1).createdDate(LocalDateTime.now()).lastModifiedDate(LocalDateTime.now()).build();

        CustomerDTO customerDTO2 = CustomerDTO.builder().customerName("Fatma").id(UUID.randomUUID()).version(1).createdDate(LocalDateTime.now()).lastModifiedDate(LocalDateTime.now()).build();

        CustomerDTO customerDTO3 = CustomerDTO.builder().customerName("Hayriye").id(UUID.randomUUID()).version(1).createdDate(LocalDateTime.now()).lastModifiedDate(LocalDateTime.now()).build();

        customerMap.put(customerDTO1.getId(), customerDTO1);
        customerMap.put(customerDTO2.getId(), customerDTO2);
        customerMap.put(customerDTO3.getId(), customerDTO3);
    }

    @Override
    public List<CustomerDTO> listCustomers() {
        return new ArrayList<>(customerMap.values());
    }

    @Override
    public Optional<CustomerDTO> getCustomerById(UUID customerId) {
        return Optional.ofNullable(customerMap.get(customerId));
    }

    @Override
    public CustomerDTO saveNewCustomer(CustomerDTO customerDTO) {
        CustomerDTO newCustomerDTO = CustomerDTO.builder().customerName(customerDTO.getCustomerName()).id(UUID.randomUUID()).version(1).createdDate(LocalDateTime.now()).lastModifiedDate(LocalDateTime.now()).build();
        customerMap.put(newCustomerDTO.getId(), newCustomerDTO);
        return newCustomerDTO;
    }

    @Override
    public Optional<CustomerDTO> updateCustomerById(UUID customerId, CustomerDTO customerDTO) {
        CustomerDTO existing = customerMap.get(customerId);
        existing.setCustomerName(customerDTO.getCustomerName());
        existing.setCreatedDate(customerDTO.getCreatedDate());
        existing.setLastModifiedDate(LocalDateTime.now());
        existing.setVersion(customerDTO.getVersion());
        return Optional.ofNullable(customerMap.put(existing.getId(), existing));
    }

    @Override
    public Boolean deleteCustomerById(UUID customerId) {
        return customerMap.remove(customerId) != null;
    }

    @Override
    public Optional<CustomerDTO> patchCustomerById(UUID customerId, CustomerDTO customerDTO) {
        CustomerDTO existing = customerMap.get(customerId);

        if(StringUtils.hasText(customerDTO.getCustomerName())) {
            existing.setCustomerName(customerDTO.getCustomerName());
        }

        if(customerDTO.getCreatedDate() != null) {
            existing.setCreatedDate(customerDTO.getCreatedDate());
        }

        return Optional.of(existing);
    }
}
