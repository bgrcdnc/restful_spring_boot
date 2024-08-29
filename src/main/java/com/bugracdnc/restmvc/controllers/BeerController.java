package com.bugracdnc.restmvc.controllers;

import com.bugracdnc.restmvc.models.BeerDTO;
import com.bugracdnc.restmvc.services.BeerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BeerController {
    public static final String BEER_PATH = "/api/v1/beer";
    public static final String BEER_ID_PATH = BEER_PATH + "/{beerId}";
    private final BeerService beerService;

    @PatchMapping(BEER_ID_PATH)
    public ResponseEntity<HttpStatus> patchById(@PathVariable("beerId") UUID beerId, @RequestBody BeerDTO beerDTO) {
        if(beerService.patchBeerById(beerId, beerDTO).isEmpty()) {
            throw new NotFoundException();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(BEER_ID_PATH)
    public ResponseEntity<HttpStatus> deleteById(@PathVariable("beerId") UUID beerId) {
        if(beerService.deleteBeerById(beerId)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        throw new NotFoundException();
    }

    @PutMapping(BEER_ID_PATH)
    public ResponseEntity<HttpStatus> updateById(@PathVariable("beerId") UUID beerId, @Validated @RequestBody BeerDTO beerDTO) {
        if(beerService.updateBeerById(beerId, beerDTO).isEmpty()) {
            throw new NotFoundException();
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(BEER_PATH)
    public ResponseEntity<HttpStatus> handlePost(@Validated @RequestBody BeerDTO beerDTO) {
        BeerDTO savedBeerDTO = beerService.saveNewBeer(beerDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/v1/beer/" + savedBeerDTO.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping(BEER_PATH)
    public List<BeerDTO> listBeers() {
        return beerService.listBeers();
    }

    @GetMapping(BEER_ID_PATH)
    public BeerDTO getBeerById(@PathVariable UUID beerId) {
        return beerService.getBeerById(beerId).orElseThrow(NotFoundException::new);
    }
}
