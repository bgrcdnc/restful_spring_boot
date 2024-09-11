package com.bugracdnc.restmvc.controllers;

import com.bugracdnc.restmvc.entities.Customer;
import com.bugracdnc.restmvc.mappers.CustomerMapper;
import com.bugracdnc.restmvc.models.CustomerDTO;
import com.bugracdnc.restmvc.repos.CustomerRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CustomerControllerIT {
    @Autowired
    CustomerController customerController;

    @Autowired
    CustomerRepo customerRepo;

    @Autowired
    CustomerMapper customerMapper;

    @Test
    void testPatchNotFound() {
        assertThrows(NotFoundException.class, () -> {
            customerController.patchById(UUID.randomUUID(), CustomerDTO.builder().build());
        });
    }

    @Rollback
    @Transactional
    @Test
    void testPatchById() {
        Customer customer = customerRepo.findAll().getFirst();
        CustomerDTO customerDTO = customerMapper.customerToCustomerDTO(customer);
        customerDTO.setVersion(null);
        customerDTO.setId(null);
        final String customerName = "Integration Test";
        customerDTO.setCustomerName(customerName);

        ResponseEntity<HttpStatus> responseEntity = customerController.patchById(customer.getId(), customerDTO);

        Customer patchedCustomer = customerRepo.findById(customer.getId()).get();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        assertThat(patchedCustomer).isNotNull();
        assertThat(patchedCustomer.getCustomerName()).isEqualTo(customerName);
    }

    @Test
    void testDeleteNotFound() {
        assertThrows(NotFoundException.class, () -> {
            customerController.deleteById(UUID.randomUUID());
        });
    }

    @Rollback
    @Transactional
    @Test
    void testDeleteById() {
        Customer customer = customerRepo.findAll().getFirst();

        ResponseEntity<HttpStatus> responseEntity = customerController.deleteById(customer.getId());

        Customer deletedCustomer = customerRepo.findById(customer.getId()).orElse(null);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        assertThat(deletedCustomer).isNull();
    }

    @Test
    void testUpdateNotFound() {
        assertThrows(NotFoundException.class, () -> {
            customerController.updateById(UUID.randomUUID(), CustomerDTO.builder().build());
        });
    }

    @Rollback
    @Transactional
    @Test
    void testUpdateCustomerById() {
        Customer customer = customerRepo.findAll().getFirst();
        CustomerDTO customerDTO = customerMapper.customerToCustomerDTO(customer);
        customerDTO.setId(null);
        customerDTO.setVersion(null);
        final String customerName = "Integration Testing";
        customerDTO.setCustomerName(customerName);

        ResponseEntity<HttpStatus> responseEntity = customerController.updateById(customer.getId(), customerDTO);
        Customer updatedCustomer = customerRepo.findById(customer.getId()).get();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        assertThat(updatedCustomer).isNotNull();
        assertThat(updatedCustomer.getCustomerName()).isEqualTo(customerName);
    }

    @Rollback
    @Transactional
    @Test
    void testSaveNewCustomer() {
        CustomerDTO customerDTO = CustomerDTO.builder()
                                             .customerName("Integration Testing")
                                             .build();

        ResponseEntity<HttpStatus> responseEntity = customerController.handlePost(customerDTO);

        @SuppressWarnings("null")
        String[] locationUUID = responseEntity.getHeaders().getLocation().getPath().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[4]);
        Customer customer = customerRepo.findById(savedUUID).get();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();
        assertThat(customer).isNotNull();
    }

    @Test
    void testCustomerIdNotFound() {
        assertThrows(NotFoundException.class, () -> {
            customerController.getCustomerById(UUID.randomUUID());
        });
    }

    @Test
    void testGetById() {
        Customer customer = customerRepo.findAll().getFirst();

        CustomerDTO customerDTO = customerController.getCustomerById(customer.getId());

        assertThat(customerDTO).isNotNull();
    }

    @Test
    void testListCustomers() {
        List<CustomerDTO> customers = customerController.listCustomers();

        assertThat(customers.size()).isEqualTo(3);
    }

    @Rollback
    @Transactional
    @Test
    void testEmptyList() {
        customerRepo.deleteAll();
        List<CustomerDTO> customers = customerController.listCustomers();

        assertThat(customers.size()).isEqualTo(0);
    }
}