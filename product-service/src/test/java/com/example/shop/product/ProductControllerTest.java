package com.example.shop.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerTest {

    @LocalServerPort int port;
    @Autowired TestRestTemplate rest;

    @Test
    void listReturnsSeededProducts() {
        ResponseEntity<List> resp = rest.getForEntity(url("/products"), List.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).hasSize(5);
    }

    @Test
    void getByIdReturnsProduct() {
        ResponseEntity<Map> resp = rest.getForEntity(url("/products/p-1001"), Map.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).containsEntry("id", "p-1001");
    }

    @Test
    void getByIdReturns404ForUnknown() {
        ResponseEntity<Map> resp = rest.getForEntity(url("/products/nope"), Map.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void reserveSucceedsAndDecrementsStock() {
        ResponseEntity<Map> resp = rest.postForEntity(
                url("/products/p-1003/reserve?quantity=2"), null, Map.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).containsEntry("reserved", true);
    }

    @Test
    void reserveReturns400WhenStockInsufficient() {
        ResponseEntity<Map> resp = rest.postForEntity(
                url("/products/p-1003/reserve?quantity=99999"), null, Map.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody()).containsEntry("reserved", false);
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
