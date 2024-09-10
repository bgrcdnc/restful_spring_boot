package com.bugracdnc.restmvc.repos;

import com.bugracdnc.restmvc.entities.Beer;
import com.bugracdnc.restmvc.models.BeerStyle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BeerRepo extends JpaRepository<Beer, UUID> {

    List<Beer> findAllByBeerNameIsLikeIgnoreCase(String beerName);

    List<Beer> findAllByBeerStyle(BeerStyle beerName);

    List<Beer> findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle(String beerName, BeerStyle beerStyle);
}
