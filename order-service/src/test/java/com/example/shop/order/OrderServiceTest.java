package com.example.shop.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class OrderServiceTest {

    private static final String PRODUCT_URL = "http://product-service:8081";
    private static final String USER_URL = "http://user-service:8082";

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private OrderRepository repository;
    private OrderService service;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        repository = new OrderRepository();
        service = new OrderService(restTemplate, repository, PRODUCT_URL, USER_URL);
    }

    @Test
    void placeOrderHappyPath() {
        mockServer.expect(requestTo(USER_URL + "/users/u-1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        "{\"id\":\"u-1\",\"name\":\"Jeff\",\"email\":\"j@x\",\"shippingAddress\":\"410 Terry\"}",
                        MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo(PRODUCT_URL + "/products/p-1001"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        "{\"id\":\"p-1001\",\"name\":\"Echo Dot\",\"price\":49.99,\"stock\":120}",
                        MediaType.APPLICATION_JSON));

        mockServer.expect(requestTo(PRODUCT_URL + "/products/p-1001/reserve?quantity=2"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"reserved\":true}", MediaType.APPLICATION_JSON));

        OrderRequest req = new OrderRequest();
        req.setUserId("u-1");
        OrderRequest.Item item = new OrderRequest.Item();
        item.setProductId("p-1001");
        item.setQuantity(2);
        req.setItems(List.of(item));

        Order order = service.placeOrder(req);

        assertThat(order.getUserId()).isEqualTo("u-1");
        assertThat(order.getUserName()).isEqualTo("Jeff");
        assertThat(order.getStatus()).isEqualTo("CONFIRMED");
        assertThat(order.getLines()).hasSize(1);
        assertThat(order.getTotal()).hasToString("99.98");
        mockServer.verify();
    }

    @Test
    void placeOrderRejectsMissingUserId() {
        OrderRequest req = new OrderRequest();
        req.setItems(List.of(new OrderRequest.Item()));

        assertThatThrownBy(() -> service.placeOrder(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("userId");
    }

    @Test
    void placeOrderRejectsEmptyItems() {
        OrderRequest req = new OrderRequest();
        req.setUserId("u-1");
        req.setItems(List.of());

        assertThatThrownBy(() -> service.placeOrder(req))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void placeOrderTranslates404FromUserServiceToBadRequest() {
        mockServer.expect(requestTo(USER_URL + "/users/u-missing"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        OrderRequest req = new OrderRequest();
        req.setUserId("u-missing");
        OrderRequest.Item item = new OrderRequest.Item();
        item.setProductId("p-1001");
        item.setQuantity(1);
        req.setItems(List.of(item));

        assertThatThrownBy(() -> service.placeOrder(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown user");
    }

    @Test
    void placeOrderTranslatesReserveFailureToIllegalState() {
        mockServer.expect(requestTo(USER_URL + "/users/u-1"))
                .andRespond(withSuccess("{\"id\":\"u-1\",\"name\":\"Jeff\"}", MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(PRODUCT_URL + "/products/p-1001"))
                .andRespond(withSuccess(
                        "{\"id\":\"p-1001\",\"name\":\"Echo Dot\",\"price\":49.99}",
                        MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(PRODUCT_URL + "/products/p-1001/reserve?quantity=99999"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        OrderRequest req = new OrderRequest();
        req.setUserId("u-1");
        OrderRequest.Item item = new OrderRequest.Item();
        item.setProductId("p-1001");
        item.setQuantity(99999);
        req.setItems(List.of(item));

        assertThatThrownBy(() -> service.placeOrder(req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Could not reserve");
    }

    @Test
    void findByUserReturnsOnlyMatchingOrders() {
        repository.save(new Order("o-1", "u-1", "Jeff", List.of(),
                new java.math.BigDecimal("10"), "CONFIRMED", java.time.Instant.now()));
        repository.save(new Order("o-2", "u-2", "Ada", List.of(),
                new java.math.BigDecimal("20"), "CONFIRMED", java.time.Instant.now()));

        List<Order> orders = service.findByUser("u-1");

        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getId()).isEqualTo("o-1");
    }
}
