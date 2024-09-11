package com.bugracdnc.restmvc.services;

import com.bugracdnc.restmvc.entities.Beer;
import com.bugracdnc.restmvc.mappers.BeerMapper;
import com.bugracdnc.restmvc.models.BeerDTO;
import com.bugracdnc.restmvc.models.BeerStyle;
import com.bugracdnc.restmvc.repos.BeerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Primary
@RequiredArgsConstructor
public class BeerServiceJPA implements BeerService {

    public final static int DEFAULT_PAGE = 0;
    public final static int DEFAULT_PAGE_SIZE = 25;
    public final static int MAX_PAGE_SIZE = 1000;
    private final BeerRepo beerRepo;
    private final BeerMapper beerMapper;

    @Override
    public Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInv, Integer pageNumber, Integer pageSize) {
        Page<Beer> beerPage;

        boolean nameIsPresent = StringUtils.hasText(beerName);
        boolean beerStyleIsPresent = beerStyle != null;

        PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);

        if(nameIsPresent && beerStyleIsPresent) {
            beerPage = listBeersByNameAndBeerStyle(beerName, beerStyle, pageRequest);
        } else if(nameIsPresent) {
            beerPage = listBeersByName(beerName, pageRequest);
        } else if(beerStyleIsPresent) {
            beerPage = listBeersByBeerStyle(beerStyle, pageRequest);
        } else {
            beerPage = beerRepo.findAll(pageRequest);
        }

        if(showInv != null && !showInv) {
            beerPage.forEach(beer -> beer.setQuantityOnHand(null));
        }

        return beerPage.map(beerMapper::beerToBeerDto);
    }

    public PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
        int queryPageNumber;
        int queryPageSize;

        if(pageNumber != null && pageNumber > 0) {
            queryPageNumber = pageNumber - 1;
        } else {
            queryPageNumber = DEFAULT_PAGE;
        }

        if(pageSize != null && pageSize > 0) {
            if(pageSize > MAX_PAGE_SIZE) {
                queryPageSize = MAX_PAGE_SIZE;
            } else {
                queryPageSize = pageSize;
            }
        } else {
            queryPageSize = DEFAULT_PAGE_SIZE;
        }

        Sort sort = Sort.by(Sort.Order.asc("beerName"));

        return PageRequest.of(queryPageNumber, queryPageSize, sort);
    }

    private Page<Beer> listBeersByNameAndBeerStyle(String beerName, BeerStyle beerStyle, Pageable pageable) {
        return beerRepo.findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle("%" + beerName + "%", beerStyle, pageable);
    }

    private Page<Beer> listBeersByBeerStyle(BeerStyle beerStyle, Pageable pageable) {
        return beerRepo.findAllByBeerStyle(beerStyle, pageable);
    }

    public Page<Beer> listBeersByName(String beerName, Pageable pageable) {
        return beerRepo.findAllByBeerNameIsLikeIgnoreCase("%" + beerName + "%", pageable);
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
        }, () -> atomicReference.set(Optional.empty()));

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

        if(beerRepo.findById(beerId).isPresent()) {
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
