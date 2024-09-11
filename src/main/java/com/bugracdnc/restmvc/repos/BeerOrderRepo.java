package com.bugracdnc.restmvc.repos;

import com.bugracdnc.restmvc.entities.BeerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BeerOrderRepo extends JpaRepository<BeerOrder, UUID> {
}
