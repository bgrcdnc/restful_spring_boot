package com.bugracdnc.restmvc.controllers;

import com.bugracdnc.restmvc.entities.Beer;
import com.bugracdnc.restmvc.mappers.BeerMapper;
import com.bugracdnc.restmvc.models.BeerDTO;
import com.bugracdnc.restmvc.repos.BeerRepo;
import com.bugracdnc.restmvc.services.BeerServiceJPA;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BeerControllerIT {
    @Autowired
    BeerController beerController;

    @Autowired
    BeerRepo beerRepo;

    @Autowired
    BeerMapper beerMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    Integer pageSize = 32;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void testListBeersByNameAndBeerStyleShowInventoryTruePage2() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                                .queryParam("beerStyle", "ALE")
                                .queryParam("beerName", "ALE")
                                .queryParam("showInv", "TRUE")
                                .queryParam("pageNumber", "2")
                                .queryParam("pageSize", String.valueOf(pageSize)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content.size()", is(pageSize)))
               .andExpect(jsonPath("$.content.[0].quantityOnHand").isNotEmpty());
    }

    @Test
    void testListBeersByNameAndBeerStyleShowInventoryTruePage1() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                                .queryParam("beerStyle", "ALE")
                                .queryParam("beerName", "ALE")
                                .queryParam("showInv", "TRUE")
                                .queryParam("pageNumber", "1")
                                .queryParam("pageSize", String.valueOf(pageSize)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content.size()", is(pageSize)))
               .andExpect(jsonPath("$.content.[0].quantityOnHand").isNotEmpty());
    }

    @Test
    void testListBeersByNameAndBeerStyleShowInventoryFalse() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                                .queryParam("beerStyle", "ALE")
                                .queryParam("beerName", "ALE")
                                .queryParam("showInv", "FALSE"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content.size()").isNotEmpty())
               .andExpect(jsonPath("$.content.[0].quantityOnHand").value(IsNull.nullValue()));
    }

    @Test
    void testListBeersByNameAndBeerStyleShowInventoryTrue() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                                .queryParam("beerStyle", "ALE")
                                .queryParam("beerName", "ALE")
                                .queryParam("showInv", "TRUE"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content.size()").isNotEmpty())
               .andExpect(jsonPath("$.content.[0].quantityOnHand").isNotEmpty());
    }

    @Test
    void testListBeersByNameAndBeerStyle() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                                .queryParam("beerStyle", "ALE")
                                .queryParam("beerName", "ALE")
                                .queryParam("pageSize", String.valueOf(BeerServiceJPA.MAX_PAGE_SIZE)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content.size()", is(251)));
    }

    @Test
    void testListBeersByBeerStyle() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                                .queryParam("beerStyle", "ALE")
                                .queryParam("pageSize", String.valueOf(BeerServiceJPA.MAX_PAGE_SIZE)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content.size()", is(400)));
    }

    @Test
    void testListBeersByName() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                                .queryParam("beerName", "IPA")
                                .queryParam("pageSize", String.valueOf(BeerServiceJPA.MAX_PAGE_SIZE)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content.size()", is(336)));
    }

    @Test
    void testPatchBeerBadName() throws Exception {
        Beer beer = beerRepo.findAll().getFirst();

        Map<String, Object> beerMap = new HashMap<>();
        beerMap.put("beerName",
                    "9832109382109898321093821098983210938210989832109382109898321093821098983210938210989832109382109898321093821098");

        mockMvc.perform(patch(BeerController.BEER_ID_PATH, beer.getId())
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beerMap)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void testPatchNotFound() {
        assertThrows(NotFoundException.class,
                     () -> beerController.patchById(UUID.randomUUID(), BeerDTO.builder().build()));
    }

    @SuppressWarnings("null")
    @Rollback
    @Transactional
    @Test
    void testPatchById() {
        Beer beer = beerRepo.findAll().getFirst();
        BeerDTO beerDTO = beerMapper.beerToBeerDto(beer);
        beerDTO.setVersion(null);
        beerDTO.setId(null);
        final String customerName = "Integration Test";
        beerDTO.setBeerName(customerName);

        ResponseEntity<HttpStatus> responseEntity = beerController.patchById(beer.getId(), beerDTO);

        Beer patchedBeer = beerRepo.findById(beer.getId()).isPresent() ? beerRepo.findById(beer.getId()).get() : null;

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        assertThat(patchedBeer).isNotNull();
        assertThat(patchedBeer.getBeerName()).isEqualTo(customerName);
    }

    @Test
    void testDeleteNotFound() {
        assertThrows(NotFoundException.class, () -> beerController.deleteById(UUID.randomUUID()));
    }

    @Rollback
    @Transactional
    @Test
    void testDeleteById() {
        Beer beer = beerRepo.findAll().getFirst();

        ResponseEntity<HttpStatus> responseEntity = beerController.deleteById(beer.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        assertThat(beerRepo.findById(beer.getId())).isEmpty();
    }

    @Test
    void testUpdateNotFound() {
        assertThrows(NotFoundException.class,
                     () -> beerController.updateById(UUID.randomUUID(), BeerDTO.builder().build()));
    }

    @SuppressWarnings("null")
    @Rollback
    @Transactional
    @Test
    void testUpdateBeerById() {
        Beer beer = beerRepo.findAll().getFirst();
        BeerDTO beerDTO = beerMapper.beerToBeerDto(beer);
        beerDTO.setId(null);
        beerDTO.setVersion(null);
        final String beerName = "Integration Testing";
        beerDTO.setBeerName(beerName);

        ResponseEntity<HttpStatus> responseEntity = beerController.updateById(beer.getId(), beerDTO);

        Beer updatedBeer = beerRepo.findById(beer.getId()).isPresent() ? beerRepo.findById(beer.getId()).get() : null;

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        assertThat(updatedBeer).isNotNull();
        assertThat(updatedBeer.getBeerName()).isEqualTo(beerName);
    }

    @Rollback
    @Transactional
    @Test
    void testSaveNewBeer() {
        BeerDTO beerDTO = BeerDTO.builder()
                                 .beerName("Integration Tests")
                                 .build();

        ResponseEntity<HttpStatus> responseEntity = beerController.handlePost(beerDTO);

        String[] locationUUID = Objects.requireNonNull(responseEntity.getHeaders().getLocation()).getPath().split("/");
        UUID savedUUID = UUID.fromString(locationUUID[4]);
        Beer beer = beerRepo.findById(savedUUID).isPresent() ? beerRepo.findById(savedUUID).get() : null;

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();
        assertThat(beer).isNotNull();
    }

    @Test
    void testBeerIdNotFound() {
        assertThrows(NotFoundException.class, () -> beerController.getBeerById(UUID.randomUUID()));
    }

    @Test
    void testGetById() {
        Beer beer = beerRepo.findAll().getFirst();

        BeerDTO dto = beerController.getBeerById(beer.getId());

        assertThat(dto).isNotNull();
    }

    @Test
    void testListBeers() {
        Page<BeerDTO> dto = beerController.listBeers(null, null, null, 1, 2410);

        assertThat(dto.getContent().size()).isEqualTo(BeerServiceJPA.MAX_PAGE_SIZE);
    }

    @Rollback
    @Transactional
    @Test
    void testEmptyList() {
        beerRepo.deleteAll();
        Page<BeerDTO> beers = beerController.listBeers(null, null, null, 1, 25);

        assertThat(beers.isEmpty()).isEqualTo(true);
    }
}