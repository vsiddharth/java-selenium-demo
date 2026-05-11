package com.example.shop.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @MockBean OrderService service;

    @Test
    void createReturns200WhenServiceSucceeds() throws Exception {
        Order order = new Order("o-abc", "u-1", "Jeff",
                List.of(new OrderLine("p-1001", "Echo Dot", 1,
                        new BigDecimal("49.99"), new BigDecimal("49.99"))),
                new BigDecimal("49.99"), "CONFIRMED", Instant.now());
        when(service.placeOrder(any())).thenReturn(order);

        mvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(Map.of(
                                "userId", "u-1",
                                "items", List.of(Map.of("productId", "p-1001", "quantity", 1))))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("o-abc"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void createReturns400OnIllegalArgument() throws Exception {
        when(service.placeOrder(any()))
                .thenThrow(new IllegalArgumentException("Unknown user: u-x"));

        mvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"u-x\",\"items\":[{\"productId\":\"p\",\"quantity\":1}]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Unknown user: u-x"));
    }

    @Test
    void createReturns409OnIllegalState() throws Exception {
        when(service.placeOrder(any()))
                .thenThrow(new IllegalStateException("Could not reserve stock"));

        mvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"u-1\",\"items\":[{\"productId\":\"p\",\"quantity\":1}]}"))
                .andExpect(status().isConflict());
    }

    @Test
    void getByIdReturns404WhenAbsent() throws Exception {
        when(service.findById("missing")).thenReturn(java.util.Optional.empty());

        mvc.perform(get("/orders/missing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void byUserDelegatesToService() throws Exception {
        when(service.findByUser("u-1")).thenReturn(List.of());

        mvc.perform(get("/orders").param("userId", "u-1"))
                .andExpect(status().isOk());
    }
}
