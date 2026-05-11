package com.example.shop.frontend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class ShopControllerTest {

    @Autowired MockMvc mvc;
    @MockBean BackendClient backend;

    @Test
    void homeRendersProductsFromBackend() throws Exception {
        when(backend.listProducts()).thenReturn(List.of(
                Map.of("id", "p-1001", "name", "Echo Dot", "price", new BigDecimal("49.99"),
                        "imageUrl", "img.jpg", "stock", 10)));

        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("products"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Echo Dot")));
    }

    @Test
    void productDetailRedirectsHomeWhenUnknown() throws Exception {
        when(backend.getProduct("missing")).thenReturn(java.util.Optional.empty());

        mvc.perform(get("/products/missing"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void productDetailRendersWhenFound() throws Exception {
        when(backend.getProduct("p-1001")).thenReturn(java.util.Optional.of(
                Map.of("id", "p-1001", "name", "Echo Dot", "price", new BigDecimal("49.99"),
                        "imageUrl", "img.jpg", "description", "Smart speaker", "stock", 10)));

        mvc.perform(get("/products/p-1001"))
                .andExpect(status().isOk())
                .andExpect(view().name("product"))
                .andExpect(model().attribute("product",
                        org.hamcrest.Matchers.hasEntry("id", "p-1001")));
    }

    @Test
    void addToCartRedirectsToCartPage() throws Exception {
        mvc.perform(post("/cart/add")
                        .param("productId", "p-1001")
                        .param("quantity", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }

    @Test
    void checkoutRedirectsBackToCartWhenEmpty() throws Exception {
        mvc.perform(post("/checkout").param("userId", "u-1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }

    @Test
    void ordersPageOnlyLoadsOrdersWhenUserSelected() throws Exception {
        when(backend.listUsers()).thenReturn(List.of(
                Map.of("id", "u-1", "name", "Jeff", "shippingAddress", "Seattle")));
        when(backend.listOrdersByUser(eq("u-1"))).thenReturn(List.of());

        mvc.perform(get("/orders").param("userId", "u-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attribute("selectedUserId", "u-1"));
    }
}
