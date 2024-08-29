package com.bugracdnc.restmvc.repos;

import com.bugracdnc.restmvc.entities.Beer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BeerRepo extends JpaRepository<Beer, UUID> {
}
