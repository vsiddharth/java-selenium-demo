package com.example.shop.frontend;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@SessionScope
public class Cart {

    private final Map<String, Integer> items = new LinkedHashMap<>();

    public void add(String productId, int quantity) {
        items.merge(productId, quantity, Integer::sum);
    }

    public void remove(String productId) {
        items.remove(productId);
    }

    public void clear() {
        items.clear();
    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int totalQuantity() {
        return items.values().stream().mapToInt(Integer::intValue).sum();
    }
}
