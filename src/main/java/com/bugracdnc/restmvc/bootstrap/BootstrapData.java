package com.bugracdnc.restmvc.bootstrap;

import com.bugracdnc.restmvc.entities.Beer;
import com.bugracdnc.restmvc.entities.Customer;
import com.bugracdnc.restmvc.models.BeerCSVRecord;
import com.bugracdnc.restmvc.models.BeerStyle;
import com.bugracdnc.restmvc.repos.BeerRepo;
import com.bugracdnc.restmvc.repos.CustomerRepo;
import com.bugracdnc.restmvc.services.BeerCsvService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {
    private final BeerRepo beerRepo;
    private final CustomerRepo customerRepo;
    private final BeerCsvService beerCsvService;

    @Transactional
    @Override
    public void run(String... args) throws FileNotFoundException {
        loadBeerCSVData();
        loadBeerData();
        loadCustomerData();

        log.debug("In Bootstrap");
        log.debug("Beer Count: {}", beerRepo.count());
        log.debug("Customer Count: {}", customerRepo.count());
    }

    private void loadBeerCSVData() throws FileNotFoundException {
        if(beerRepo.count() < 10) {
            File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");

            List<BeerCSVRecord> recs = beerCsvService.convertCSV(file);
            for(BeerCSVRecord rec: recs) {
                beerRepo.save(Beer.builder()
                                  .beerName(StringUtils.abbreviate(rec.getBeer(), 50))
                                  .beerStyle(rec.getBeerStyle())
                                  .price(BigDecimal.TEN)
                                  .upc(rec.getRow().toString())
                                  .quantityOnHand(rec.getCount())
                                  .build());
            }
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

    private void loadCustomerData() {
        if(customerRepo.count() == 0) {
            Customer customer1, customer2, customer3;

            customer1 = Customer.builder().customerName("Ayse").createdDate(LocalDateTime.now()).lastModifiedDate(LocalDateTime.now()).build();
            customer2 = Customer.builder().customerName("Fatma").createdDate(LocalDateTime.now()).lastModifiedDate(LocalDateTime.now()).build();
            customer3 = Customer.builder().customerName("Hayriye").createdDate(LocalDateTime.now()).lastModifiedDate(LocalDateTime.now()).build();

            customerRepo.saveAll(Arrays.asList(customer1, customer2, customer3));
        }
    }
}
