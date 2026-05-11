package com.example.shop.order;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {

    private final RestTemplate restTemplate;
    private final OrderRepository repository;
    private final String productUrl;
    private final String userUrl;

    public OrderService(RestTemplate restTemplate,
                        OrderRepository repository,
                        @Value("${services.product-url}") String productUrl,
                        @Value("${services.user-url}") String userUrl) {
        this.restTemplate = restTemplate;
        this.repository = repository;
        this.productUrl = productUrl;
        this.userUrl = userUrl;
    }

    public Order placeOrder(OrderRequest request) {
        if (request.getUserId() == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("userId and at least one item are required");
        }

        Map<String, Object> user = fetchUser(request.getUserId());

        List<OrderLine> lines = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderRequest.Item item : request.getItems()) {
            Map<String, Object> product = fetchProduct(item.getProductId());
            BigDecimal price = new BigDecimal(product.get("price").toString());
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));

            reserveStock(item.getProductId(), item.getQuantity());

            lines.add(new OrderLine(
                    item.getProductId(),
                    String.valueOf(product.get("name")),
                    item.getQuantity(),
                    price,
                    lineTotal));
            total = total.add(lineTotal);
        }

        Order order = new Order(
                "o-" + UUID.randomUUID().toString().substring(0, 8),
                request.getUserId(),
                String.valueOf(user.get("name")),
                lines,
                total,
                "CONFIRMED",
                Instant.now());
        return repository.save(order);
    }

    public List<Order> findByUser(String userId) {
        return repository.findAll().stream()
                .filter(o -> o.getUserId().equals(userId))
                .toList();
    }

    public java.util.Optional<Order> findById(String id) {
        return repository.findById(id);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> fetchUser(String userId) {
        try {
            ResponseEntity<Map> resp = restTemplate.getForEntity(userUrl + "/users/" + userId, Map.class);
            return resp.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException("Unknown user: " + userId);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> fetchProduct(String productId) {
        try {
            ResponseEntity<Map> resp = restTemplate.getForEntity(productUrl + "/products/" + productId, Map.class);
            return resp.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException("Unknown product: " + productId);
        }
    }

    private void reserveStock(String productId, int quantity) {
        String url = productUrl + "/products/" + productId + "/reserve?quantity=" + quantity;
        try {
            restTemplate.postForEntity(url, null, Map.class);
        } catch (HttpClientErrorException e) {
            throw new IllegalStateException("Could not reserve stock for " + productId + ": " + e.getStatusCode());
        }
    }
}
