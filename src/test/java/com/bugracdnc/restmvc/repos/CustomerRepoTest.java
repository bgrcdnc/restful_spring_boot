package com.bugracdnc.restmvc.repos;

import com.bugracdnc.restmvc.entities.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRepoTest {
    @Autowired
    CustomerRepo customerRepo;

    @Test
    void testSaveCustomer() {
        Customer savedCustomer = customerRepo.save(Customer.builder()
                                                           .customerName("Bugra")
                                                           .build());

        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getId()).isNotNull();
    }

}