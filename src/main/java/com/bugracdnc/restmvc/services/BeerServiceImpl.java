package com.bugracdnc.restmvc.services;

import com.bugracdnc.restmvc.models.BeerDTO;
import com.bugracdnc.restmvc.models.BeerStyle;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BeerServiceImpl implements BeerService {
    private final Map<UUID, BeerDTO> beerMap;

    public BeerServiceImpl() {
        this.beerMap = new HashMap<>();
        BeerDTO beerDTO1, beerDTO2, beerDTO3;

        beerDTO1 = BeerDTO.builder().id(UUID.randomUUID()).version(1).beerName("Efes Pilsen").beerStyle(BeerStyle.LAGER).upc("11111").price(new BigDecimal("54.99")).quantityOnHand(64).createdDate(LocalDateTime.now()).updateDate(LocalDateTime.now()).build();

        beerDTO2 = BeerDTO.builder().id(UUID.randomUUID()).version(1).beerName("Tuborg").beerStyle(BeerStyle.LAGER).upc("22222").price(new BigDecimal("57.99")).quantityOnHand(32).createdDate(LocalDateTime.now()).updateDate(LocalDateTime.now()).build();

        beerDTO3 = BeerDTO.builder().id(UUID.randomUUID()).version(1).beerName("Carlsberg").beerStyle(BeerStyle.LAGER).upc("33333").price(new BigDecimal("52.99")).quantityOnHand(48).createdDate(LocalDateTime.now()).updateDate(LocalDateTime.now()).build();

        beerMap.put(beerDTO1.getId(), beerDTO1);
        beerMap.put(beerDTO2.getId(), beerDTO2);
        beerMap.put(beerDTO3.getId(), beerDTO3);
    }

    @Override
    public List<BeerDTO> listBeers() {
        return new ArrayList<>(beerMap.values());
    }

    @Override
    public Optional<BeerDTO> getBeerById(UUID id) {
        return Optional.ofNullable(beerMap.get(id));
    }

    @Override
    public BeerDTO saveNewBeer(BeerDTO beerDTO) {
        BeerDTO savedBeerDTO = BeerDTO.builder().id(UUID.randomUUID()).createdDate(LocalDateTime.now()).updateDate(LocalDateTime.now()).beerName(beerDTO.getBeerName()).beerStyle(beerDTO.getBeerStyle()).quantityOnHand(beerDTO.getQuantityOnHand()).upc(beerDTO.getUpc()).version(1).price(beerDTO.getPrice()).build();
        beerMap.put(savedBeerDTO.getId(), savedBeerDTO);
        return savedBeerDTO;
    }

    @Override
    public Optional<BeerDTO> updateBeerById(UUID beerId, BeerDTO beerDTO) {
        BeerDTO existing = beerMap.get(beerId);
        existing.setBeerName(beerDTO.getBeerName());
        existing.setBeerStyle(beerDTO.getBeerStyle());
        existing.setCreatedDate(beerDTO.getCreatedDate());
        existing.setPrice(beerDTO.getPrice());
        existing.setQuantityOnHand(beerDTO.getQuantityOnHand());
        existing.setUpc(beerDTO.getUpc());
        existing.setUpdateDate(LocalDateTime.now());
        existing.setVersion(beerDTO.getVersion());
        return Optional.of(existing);
    }

    @Override
    public Boolean deleteBeerById(UUID beerId) {
        return beerMap.remove(beerId) != null;
    }

    @Override
    public Optional<BeerDTO> patchBeerById(UUID beerId, BeerDTO beerDTO) {
        BeerDTO existing = beerMap.get(beerId);

        if(StringUtils.hasText(beerDTO.getBeerName())) {
            existing.setBeerName(beerDTO.getBeerName());
        }

        if(beerDTO.getBeerStyle() != null) {
            existing.setBeerStyle(beerDTO.getBeerStyle());
        }

        if(beerDTO.getPrice() != null) {
            existing.setPrice(beerDTO.getPrice());
        }

        if(beerDTO.getQuantityOnHand() != null) {
            existing.setQuantityOnHand(beerDTO.getQuantityOnHand());
        }

        if(StringUtils.hasText(beerDTO.getUpc())) {
            existing.setUpc(beerDTO.getUpc());
        }

        return Optional.of(existing);
    }
}
