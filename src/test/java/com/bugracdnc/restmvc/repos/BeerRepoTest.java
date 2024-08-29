package com.bugracdnc.restmvc.repos;

import com.bugracdnc.restmvc.entities.Beer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BeerRepoTest {
    @Autowired
    BeerRepo beerRepo;

    @Test
    void testSaveBeer() {
        Beer savedBeer = beerRepo.save(Beer.builder()
                                           .beerName("Test Beer")
                                           .build());

        assertThat(savedBeer).isNotNull();
        assertThat(savedBeer.getId()).isNotNull();
    }

}