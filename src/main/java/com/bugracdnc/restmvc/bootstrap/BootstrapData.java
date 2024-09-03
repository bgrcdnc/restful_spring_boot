package com.bugracdnc.restmvc.bootstrap;

import com.bugracdnc.restmvc.entities.Beer;
import com.bugracdnc.restmvc.entities.Customer;
import com.bugracdnc.restmvc.models.BeerStyle;
import com.bugracdnc.restmvc.repos.BeerRepo;
import com.bugracdnc.restmvc.repos.CustomerRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Component
public class BootstrapData implements CommandLineRunner {
    private final BeerRepo beerRepo;
    private final CustomerRepo customerRepo;

    public BootstrapData(BeerRepo beerRepo, CustomerRepo customerRepo) {
        this.beerRepo = beerRepo;
        this.customerRepo = customerRepo;
    }

    @Override
    public void run(String... args) {
        loadBeerData();
        loadCustomerData();

        log.debug("In Bootstrap");
        log.debug("Beer Count: {}", beerRepo.count());
        log.debug("Customer Count: {}", customerRepo.count());
    }

    private void loadCustomerData() {
        if(customerRepo.count() == 0) {
            Customer customer1, customer2, customer3;

            customer1 = Customer.builder().customerName("Ayse").createdDate(LocalDateTime.now()).lastModifiedDate(LocalDateTime.now()).build();
            customer2 = Customer.builder().customerName("Fatma").createdDate(LocalDateTime.now()).lastModifiedDate(LocalDateTime.now()).build();
            customer3 = Customer.builder().customerName("Hayriye").createdDate(LocalDateTime.now()).lastModifiedDate(LocalDateTime.now()).build();

            customerRepo.saveAll(Arrays.asList(customer1, customer2, customer3));
        }
    }

    private void loadBeerData() {
        if(beerRepo.count() == 0) {
            Beer beer1, beer2, beer3;

            beer1 = Beer.builder().beerName("Efes Pilsen").beerStyle(BeerStyle.LAGER).upc("11111").price(new BigDecimal("54.99")).quantityOnHand(64).createdDate(LocalDateTime.now()).updateDate(LocalDateTime.now()).build();
            beer2 = Beer.builder().beerName("Tuborg").beerStyle(BeerStyle.LAGER).upc("22222").price(new BigDecimal("57.99")).quantityOnHand(32).createdDate(LocalDateTime.now()).updateDate(LocalDateTime.now()).build();
            beer3 = Beer.builder().beerName("Carlsberg").beerStyle(BeerStyle.LAGER).upc("33333").price(new BigDecimal("52.99")).quantityOnHand(48).createdDate(LocalDateTime.now()).updateDate(LocalDateTime.now()).build();

            beerRepo.saveAll(Arrays.asList(beer1, beer2, beer3));
        }
    }
}
