package com.bugracdnc.restmvc.repos;

import com.bugracdnc.restmvc.entities.Beer;
import com.bugracdnc.restmvc.entities.Customer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class BeerOrderRepoTest {

    @Autowired
    BeerOrderRepo beerOrderRepo;

    @Autowired
    CustomerRepo customerRepo;

    @Autowired
    BeerRepo beerRepo;

    Customer testCustomer;
    Beer testBeer;

    @BeforeEach
    void setUp() {
        testCustomer = customerRepo.findAll().getFirst();
        testBeer = beerRepo.findAll().getFirst();
    }

    @Test
    void testBeerOrders() {
        log.debug("beerOrderRepo.count: {}", beerOrderRepo.count());
        log.debug("customerRepo.count: {}", customerRepo.count());
        log.debug("beerRepo.count: {}", beerRepo.count());
        log.debug("testCustomer.name: {}", testCustomer.getCustomerName());
        log.debug("testBeer.name: {}", testBeer.getBeerName());
    }

}