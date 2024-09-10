package com.bugracdnc.restmvc.controllers;

import com.bugracdnc.restmvc.models.BeerDTO;
import com.bugracdnc.restmvc.models.BeerStyle;
import com.bugracdnc.restmvc.services.BeerService;
import com.bugracdnc.restmvc.services.BeerServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(BeerController.class)
class BeerControllerTest {

    private static final String BASE_PATH = BeerController.BEER_PATH;
    private static final String BASE_ID_PATH = BeerController.BEER_ID_PATH;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BeerService beerService;

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    ArgumentCaptor<BeerDTO> beerArgumentCaptor;

    BeerServiceImpl beerServiceImpl;

    @BeforeEach
    void setUp() {
        beerServiceImpl = new BeerServiceImpl();
    }

    @Test
    void testUpdateBeerBlankName() throws Exception {
        BeerDTO beerDTO = beerServiceImpl.listBeers(null, null, false).getFirst();
        beerDTO.setBeerName("");

        given(beerService.updateBeerById(any(), any())).willReturn(Optional.of(beerDTO));

        mockMvc.perform(put(BASE_ID_PATH, beerDTO.getId())
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beerDTO)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void testCreateBeerNullBeerName() throws Exception {
        BeerDTO beerDTO = BeerDTO.builder().build();

        given(beerService.saveNewBeer(any(BeerDTO.class))).willReturn(beerServiceImpl.listBeers(null, null, false).get(1));

        MvcResult mvcResult = mockMvc.perform(post(BeerController.BEER_PATH)
                                                      .accept(MediaType.APPLICATION_JSON)
                                                      .contentType(MediaType.APPLICATION_JSON)
                                                      .content(objectMapper.writeValueAsString(beerDTO)))
                                     .andExpect(status().isBadRequest())
                                     .andExpect(jsonPath("$.length()", is(6)))
                                     .andReturn();

        log.debug(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void testGetBeerByIdNotFound() throws Exception {
        given(beerService.getBeerById(any(UUID.class))).willReturn(Optional.empty());

        mockMvc.perform(get(BASE_ID_PATH, UUID.randomUUID()))
               .andExpect(status().isNotFound());
    }

    @Test
    void testGetBeerById() throws Exception {
        BeerDTO testBeerDTO = beerServiceImpl.listBeers(null, null, false).getFirst();
        given(beerService.getBeerById(testBeerDTO.getId())).willReturn(Optional.of(testBeerDTO));

        mockMvc.perform(get(BASE_ID_PATH, testBeerDTO.getId())
                                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.id", is(testBeerDTO.getId().toString())))
               .andExpect(jsonPath("$.beerName", is(testBeerDTO.getBeerName())));
    }

    @Test
    void testListBeers() throws Exception {
        given(beerService.listBeers(any(), any(), any())).willReturn(beerServiceImpl.listBeers(any(), any(), any()));

        mockMvc.perform(get(BASE_PATH)
                                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.length()", is(3)));
    }

    @Test
    void testUpdateById() throws Exception {
        BeerDTO beerDTO = beerServiceImpl.listBeers(any(), any(), any()).getFirst();
        beerDTO.setBeerName("Testing");

        given(beerService.updateBeerById(any(), any())).willReturn(Optional.of(beerDTO));

        mockMvc.perform(put(BASE_ID_PATH, beerDTO.getId())
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beerDTO)))
               .andExpect(status().isNoContent());
        verify(beerService).updateBeerById(uuidArgumentCaptor.capture(), beerArgumentCaptor.capture());

        assert beerDTO.getId().equals(uuidArgumentCaptor.getValue());
        assert beerDTO.getBeerName().equals(beerArgumentCaptor.getValue().getBeerName());
    }

    @Test
    void testPatchById() throws Exception {
        BeerDTO beerDTO = beerServiceImpl.listBeers(null, null, false).getFirst();

        Map<String, Object> beerMap = new HashMap<>();
        beerMap.put("beerName", "Testing");
        beerMap.put("price", new BigDecimal("100.01"));

        given(beerService.patchBeerById(any(), any())).willReturn(Optional.of(beerDTO));

        mockMvc.perform(patch(BASE_ID_PATH, beerDTO.getId())
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beerMap)))
               .andExpect(status().isNoContent());

        verify(beerService).patchBeerById(uuidArgumentCaptor.capture(), beerArgumentCaptor.capture());

        assert beerDTO.getId().equals(uuidArgumentCaptor.getValue());
        assert beerMap.get("beerName").equals(beerArgumentCaptor.getValue().getBeerName());
        assert beerMap.get("price").equals(beerArgumentCaptor.getValue().getPrice());
    }

    @Test
    void testDeleteById() throws Exception {
        BeerDTO beerDTO = beerServiceImpl.listBeers(null, null, false).getFirst();

        given(beerService.deleteBeerById(beerDTO.getId())).willReturn(true);

        mockMvc.perform(delete(BASE_ID_PATH, beerDTO.getId())
                                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNoContent());
        ArgumentCaptor<UUID> uuidArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(beerService).deleteBeerById(uuidArgumentCaptor.capture());

        assert beerDTO.getId().equals(uuidArgumentCaptor.getValue());
    }

    @Test
    void testHandlePost() throws Exception {
        BeerDTO beerDTO = BeerDTO.builder()
                                 .price(new BigDecimal("55.33"))
                                 .quantityOnHand(125)
                                 .beerStyle(BeerStyle.PALE_ALE)
                                 .beerName("Frederick Pale Ale")
                                 .upc("123456")
                                 .build();

        given(beerService.saveNewBeer(any(BeerDTO.class))).willReturn(beerServiceImpl.listBeers(null, null, false).getFirst());

        mockMvc.perform(post(BASE_PATH)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beerDTO)))
               .andExpect(status().isCreated())
               .andExpect(header().exists("Location"));
    }
}
