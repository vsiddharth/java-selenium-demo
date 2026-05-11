package com.example.shop.order;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class OrderRepository {

    private final ConcurrentMap<String, Order> store = new ConcurrentHashMap<>();

    public Collection<Order> findAll() {
        return store.values();
    }

    public Optional<Order> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public Order save(Order order) {
        store.put(order.getId(), order);
        return order;
    }
}
