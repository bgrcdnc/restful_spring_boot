package com.bugracdnc.restmvc.controllers;

import com.bugracdnc.restmvc.models.CustomerDTO;
import com.bugracdnc.restmvc.services.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@Controller
public class CustomerController {
    public static final String CUSTOMER_PATH = "/api/v1/customer";
    public static final String CUSTOMER_ID_PATH = CUSTOMER_PATH + "/{customerId}";
    private final CustomerService customerService;

    @DeleteMapping(CUSTOMER_ID_PATH)
    public ResponseEntity<HttpStatus> deleteById(@PathVariable("customerId") UUID customerId) {
        if(customerService.deleteCustomerById(customerId)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            throw new NotFoundException();
        }
    }

    @PutMapping(CUSTOMER_ID_PATH)
    public ResponseEntity<HttpStatus> updateById(@PathVariable UUID customerId, @RequestBody CustomerDTO customerDTO) {
        if(customerService.updateCustomerById(customerId, customerDTO).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {throw new NotFoundException();}
    }

    @PostMapping(CUSTOMER_PATH)
    public ResponseEntity<HttpStatus> handlePost(@RequestBody CustomerDTO customerDTO) {
        CustomerDTO savedCustomerDTO = customerService.saveNewCustomer(customerDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/v1/customer/" + savedCustomerDTO.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping(CUSTOMER_PATH)
    public List<CustomerDTO> listCustomers() {
        return customerService.listCustomers();
    }

    @PatchMapping(CUSTOMER_ID_PATH)
    public ResponseEntity<HttpStatus> patchById(@PathVariable("customerId") UUID customerId, @RequestBody CustomerDTO customerDTO) {
        if(customerService.patchCustomerById(customerId, customerDTO).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {throw new NotFoundException();}
    }

    @GetMapping(CUSTOMER_ID_PATH)
    public CustomerDTO getCustomerById(@PathVariable UUID customerId) {
        return customerService.getCustomerById(customerId).orElseThrow(NotFoundException::new);
    }
}
