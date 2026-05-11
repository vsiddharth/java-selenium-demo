package com.example.shop.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @LocalServerPort int port;
    @Autowired TestRestTemplate rest;

    @Test
    void listReturnsSeededUsers() {
        ResponseEntity<List> resp = rest.getForEntity(url("/users"), List.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).hasSize(3);
    }

    @Test
    void getByIdReturnsUser() {
        ResponseEntity<Map> resp = rest.getForEntity(url("/users/u-1"), Map.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).containsEntry("id", "u-1");
    }

    @Test
    void getByIdReturns404ForUnknown() {
        ResponseEntity<Map> resp = rest.getForEntity(url("/users/nope"), Map.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createPersistsUser() {
        User newUser = new User("u-99", "Test Person", "test@example.com", "1 Demo Ave");

        ResponseEntity<User> resp = rest.postForEntity(url("/users"), newUser, User.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getId()).isEqualTo("u-99");

        ResponseEntity<Map> follow = rest.getForEntity(url("/users/u-99"), Map.class);
        assertThat(follow.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
