package com.bugracdnc.restmvc.services;

import com.bugracdnc.restmvc.mappers.BeerMapper;
import com.bugracdnc.restmvc.models.BeerDTO;
import com.bugracdnc.restmvc.repos.BeerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class BeerServiceJPA implements BeerService {

    private final BeerRepo beerRepo;
    private final BeerMapper beerMapper;

    @Override
    public List<BeerDTO> listBeers() {
        return beerRepo.findAll()
                       .stream()
                       .map(beerMapper::beerToBeerDto)
                       .collect(Collectors.toList());
    }

    @Override
    public Optional<BeerDTO> getBeerById(UUID id) {
        return Optional.ofNullable(beerMapper.beerToBeerDto(beerRepo.findById(id)
                                                                    .orElse(null)));
    }

    @Override
    public BeerDTO saveNewBeer(BeerDTO beerDTO) {
        return beerMapper.beerToBeerDto(beerRepo.save(beerMapper.beerDtoToBeer(beerDTO)));
    }

    @Override
    public Optional<BeerDTO> updateBeerById(UUID beerId, BeerDTO beerDTO) {
        AtomicReference<Optional<BeerDTO>> atomicReference = new AtomicReference<>();

        beerRepo.findById(beerId).ifPresentOrElse(foundBeer -> {
            foundBeer.setBeerName(beerDTO.getBeerName());
            foundBeer.setBeerStyle(beerDTO.getBeerStyle());
            foundBeer.setUpc(beerDTO.getUpc());
            foundBeer.setPrice(beerDTO.getPrice());
            atomicReference.set(Optional.of(beerMapper.beerToBeerDto(beerRepo.save(foundBeer))));
        }, () -> {
            atomicReference.set(Optional.empty());
        });

        return atomicReference.get();
    }

    @Override
    public Boolean deleteBeerById(UUID beerId) {
        if(beerRepo.existsById(beerId)) {
            beerRepo.deleteById(beerId);
            return true;
        }
        return false;
    }

    @Override
    public Optional<BeerDTO> patchBeerById(UUID beerId, BeerDTO beerDTO) {
        AtomicReference<Optional<BeerDTO>> atomicReference = new AtomicReference<>();

        if(beerRepo.existsById(beerId)) {
            BeerDTO existing = beerMapper.beerToBeerDto(beerRepo.findById(beerId).get());
            if(StringUtils.hasText(beerDTO.getBeerName())) {existing.setBeerName(beerDTO.getBeerName());}
            if(beerDTO.getPrice() != null) {existing.setPrice(beerDTO.getPrice());}
            if(beerDTO.getBeerStyle() != null) {existing.setBeerStyle(beerDTO.getBeerStyle());}
            if(beerDTO.getUpc() != null) {existing.setUpc(beerDTO.getUpc());}
            if(beerDTO.getQuantityOnHand() != null) {existing.setQuantityOnHand(beerDTO.getQuantityOnHand());}
            if(beerDTO.getCreatedDate() != null) {existing.setCreatedDate(beerDTO.getCreatedDate());}
            existing.setUpdateDate(LocalDateTime.now());
            atomicReference.set(Optional.of(beerMapper.beerToBeerDto(beerRepo.save(beerMapper.beerDtoToBeer(existing)))));
        } else {atomicReference.set(Optional.empty());}
        return atomicReference.get();
    }
}
