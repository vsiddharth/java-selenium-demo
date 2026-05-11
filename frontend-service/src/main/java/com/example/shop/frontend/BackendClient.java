package com.example.shop.frontend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class BackendClient {

    private final RestTemplate restTemplate;
    private final String productUrl;
    private final String userUrl;
    private final String orderUrl;

    public BackendClient(RestTemplate restTemplate,
                         @Value("${services.product-url}") String productUrl,
                         @Value("${services.user-url}") String userUrl,
                         @Value("${services.order-url}") String orderUrl) {
        this.restTemplate = restTemplate;
        this.productUrl = productUrl;
        this.userUrl = userUrl;
        this.orderUrl = orderUrl;
    }

    public List<Map<String, Object>> listProducts() {
        return restTemplate.exchange(
                productUrl + "/products",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}).getBody();
    }

    public Optional<Map<String, Object>> getProduct(String id) {
        try {
            return Optional.ofNullable(restTemplate.getForObject(productUrl + "/products/" + id, Map.class));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    public List<Map<String, Object>> listUsers() {
        return restTemplate.exchange(
                userUrl + "/users",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}).getBody();
    }

    public Map<String, Object> placeOrder(Map<String, Object> orderRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(orderRequest, headers);
        return restTemplate.postForObject(orderUrl + "/orders", entity, Map.class);
    }

    public List<Map<String, Object>> listOrdersByUser(String userId) {
        return restTemplate.exchange(
                orderUrl + "/orders?userId=" + userId,
                HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}).getBody();
    }
}
