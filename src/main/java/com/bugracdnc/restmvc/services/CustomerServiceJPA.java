package com.bugracdnc.restmvc.services;

import com.bugracdnc.restmvc.mappers.CustomerMapper;
import com.bugracdnc.restmvc.models.CustomerDTO;
import com.bugracdnc.restmvc.repos.CustomerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class CustomerServiceJPA implements CustomerService {
    private final CustomerRepo customerRepo;
    private final CustomerMapper customerMapper;
    private final MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Override
    public List<CustomerDTO> listCustomers() {
        return customerRepo.findAll()
                           .stream()
                           .map(customerMapper::customerToCustomerDTO)
                           .collect(Collectors.toList());
    }

    @Override
    public Optional<CustomerDTO> getCustomerById(UUID customerId) {
        return Optional.ofNullable(customerMapper.customerToCustomerDTO(customerRepo.findById(customerId)
                                                                                    .orElse(null)));
    }

    @Override
    public CustomerDTO saveNewCustomer(CustomerDTO customerDTO) {
        return customerMapper.customerToCustomerDTO(customerRepo.save(customerMapper.customerDtoToCustomer(customerDTO)));
    }

    @Override
    public Optional<CustomerDTO> updateCustomerById(UUID customerId, CustomerDTO customerDTO) {
        AtomicReference<Optional<CustomerDTO>> atomicReference = new AtomicReference<>();

        customerRepo.findById(customerId).ifPresentOrElse(customer -> {
            customer.setCustomerName(customerDTO.getCustomerName());
            customer.setCreatedDate(customerDTO.getCreatedDate());
            customer.setLastModifiedDate(LocalDateTime.now());
            atomicReference.set(Optional.of(customerMapper.customerToCustomerDTO(customerRepo.save(customer))));
        }, () -> {
            atomicReference.set(Optional.empty());
        });

        return atomicReference.get();
    }

    @Override
    public Boolean deleteCustomerById(UUID customerId) {
        if(customerRepo.existsById(customerId)) {
            customerRepo.deleteById(customerId);
            return true;
        }
        return false;
    }

    @Override
    public Optional<CustomerDTO> patchCustomerById(UUID customerId, CustomerDTO customerDTO) {
        AtomicReference<Optional<CustomerDTO>> atomicReference = new AtomicReference<>();

        if(customerRepo.existsById(customerId)) {
            CustomerDTO existing = customerMapper.customerToCustomerDTO(customerRepo.findById(customerId).get());
            if(StringUtils.hasText(customerDTO.getCustomerName())) {
                existing.setCustomerName(customerDTO.getCustomerName());
            }
            if(customerDTO.getCreatedDate() != null) {existing.setCreatedDate(customerDTO.getCreatedDate());}
            atomicReference.set(Optional.of(customerMapper.customerToCustomerDTO(customerRepo.save(customerMapper.customerDtoToCustomer(existing)))));
        } else {atomicReference.set(Optional.empty());}
        return atomicReference.get();
    }
}
