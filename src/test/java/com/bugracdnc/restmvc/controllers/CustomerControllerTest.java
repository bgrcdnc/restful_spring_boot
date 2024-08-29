package com.bugracdnc.restmvc.controllers;

import com.bugracdnc.restmvc.models.CustomerDTO;
import com.bugracdnc.restmvc.services.CustomerService;
import com.bugracdnc.restmvc.services.CustomerServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    private static final String BASE_PATH = CustomerController.CUSTOMER_PATH;
    private static final String BASE_ID_PATH = CustomerController.CUSTOMER_ID_PATH;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CustomerService customerService;

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    ArgumentCaptor<CustomerDTO> customerArgumentCaptor;

    CustomerServiceImpl customerServiceImpl = new CustomerServiceImpl();

    @Test
    void testDeleteById() throws Exception {
        CustomerDTO customerDTO = customerServiceImpl.listCustomers().getFirst();

        given(customerService.deleteCustomerById(any())).willReturn(true);

        mockMvc.perform(delete(BASE_ID_PATH, customerDTO.getId())
                                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNoContent());
        verify(customerService).deleteCustomerById(uuidArgumentCaptor.capture());

        assert customerDTO.getId().equals(uuidArgumentCaptor.getValue());
    }

    @Test
    void testPatchById() throws Exception {
        CustomerDTO customerDTO = customerServiceImpl.listCustomers().getFirst();
        Map<String, Object> customerMap = new HashMap<>();
        customerMap.put("customerName", "Testing");

        given(customerService.patchCustomerById(any(), any())).willReturn(Optional.of(customerDTO));

        mockMvc.perform(patch(BASE_ID_PATH, customerDTO.getId().toString())
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customerMap)))
               .andExpect(status().isNoContent());

        verify(customerService).patchCustomerById(uuidArgumentCaptor.capture(), customerArgumentCaptor.capture());

        assert customerDTO.getId().equals(uuidArgumentCaptor.getValue());
        assert customerMap.get("customerName").equals(customerArgumentCaptor.getValue().getCustomerName());
    }

    @Test
    void testUpdateById() throws Exception {
        CustomerDTO customerDTO = customerServiceImpl.listCustomers().getFirst();

        given(customerService.updateCustomerById(any(), any())).willReturn(Optional.of(customerDTO));

        mockMvc.perform(put(BASE_ID_PATH, customerDTO.getId().toString())
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customerDTO)))
               .andExpect(status().isNoContent());

        verify(customerService).updateCustomerById(any(UUID.class), any(CustomerDTO.class));
    }

    @Test
    void testListCustomers() throws Exception {
        given(customerService.listCustomers()).willReturn(customerServiceImpl.listCustomers());
        mockMvc.perform(get(BASE_PATH)
                                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.length()", is(3)));
    }

    @Test
    void testGetCustomerByIdNotFound() throws Exception {
        given(customerService.getCustomerById(any(UUID.class))).willReturn(Optional.empty());

        mockMvc.perform(get(BASE_ID_PATH, UUID.randomUUID()))
               .andExpect(status().isNotFound());
    }

    @Test
    void testGetCustomerById() throws Exception {
        CustomerDTO testCustomerDTO = customerServiceImpl.listCustomers().getFirst();
        given(customerService.getCustomerById(testCustomerDTO.getId())).willReturn(Optional.of(testCustomerDTO));
        mockMvc.perform(get(BASE_ID_PATH, testCustomerDTO.getId().toString())
                                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.id", is(testCustomerDTO.getId().toString())));
    }

    @Test
    void testHandlePost() throws Exception {
        CustomerDTO customerDTO = CustomerDTO.builder()
                                             .customerName("Neslihan")
                                             .build();

        given(customerService.saveNewCustomer(any(CustomerDTO.class))).willReturn(customerServiceImpl.listCustomers().getFirst());

        mockMvc.perform(post(BASE_PATH)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customerDTO)))
               .andExpect(status().isCreated())
               .andExpect(header().exists("Location"));
    }
}