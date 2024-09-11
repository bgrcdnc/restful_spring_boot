package com.bugracdnc.restmvc.repos;

import com.bugracdnc.restmvc.bootstrap.BootstrapData;
import com.bugracdnc.restmvc.entities.Beer;
import com.bugracdnc.restmvc.models.BeerStyle;
import com.bugracdnc.restmvc.services.BeerCsvServiceImpl;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvServiceImpl.class})
class BeerRepoTest {
    @Autowired
    BeerRepo beerRepo;

    @Test
    void testGetBeerListByName() {
        Page<Beer> page = beerRepo.findAllByBeerNameIsLikeIgnoreCase("%IPA%", null);

        assertThat(page.getContent().size()).isEqualTo(336);
    }

    @Test
    void testSaveBeerTooLong() {
        assertThrows(ConstraintViolationException.class, () -> {
            beerRepo.save(Beer.builder()
                              .beerName("Test Beer8917239812739128739128739182739817239812739812739812739817293871293871298371298379128379218")
                              .beerStyle(BeerStyle.PILSNER)
                              .upc("123456")
                              .price(new BigDecimal("25.5"))
                              .build());

            beerRepo.flush();
        });
    }

    @Test
    void testSaveBeer() {
        Beer savedBeer = beerRepo.save(Beer.builder()
                                           .beerName("Test Beer")
                                           .beerStyle(BeerStyle.PILSNER)
                                           .upc("123456")
                                           .price(new BigDecimal("25.5"))
                                           .build());

        beerRepo.flush();

        assertThat(savedBeer).isNotNull();
        assertThat(savedBeer.getId()).isNotNull();
    }

}