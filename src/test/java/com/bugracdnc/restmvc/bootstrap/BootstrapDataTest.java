package com.bugracdnc.restmvc.bootstrap;

import com.bugracdnc.restmvc.repos.BeerRepo;
import com.bugracdnc.restmvc.repos.CustomerRepo;
import com.bugracdnc.restmvc.services.BeerCsvService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BootstrapDataTest {

    @Autowired
    BeerRepo beerRepo;

    @Autowired
    CustomerRepo customerRepo;

    @Autowired
    BeerCsvService beerCsvService;

    BootstrapData bootstrapData;

    @BeforeEach
    void setUp() {
        bootstrapData = new BootstrapData(beerRepo, customerRepo, beerCsvService);
    }

    @Test
    void testRun() throws Exception {
        bootstrapData.run((String) null);

        assertThat(beerRepo.count()).isEqualTo(3);
        assertThat(customerRepo.count()).isEqualTo(3);
    }
}