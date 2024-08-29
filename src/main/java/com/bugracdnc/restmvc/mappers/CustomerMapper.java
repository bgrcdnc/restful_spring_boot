package com.bugracdnc.restmvc.mappers;

import com.bugracdnc.restmvc.entities.Customer;
import com.bugracdnc.restmvc.models.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {
    Customer customerDtoToCustomer(CustomerDTO dto);

    CustomerDTO customerToCustomerDTO(Customer customer);
}
