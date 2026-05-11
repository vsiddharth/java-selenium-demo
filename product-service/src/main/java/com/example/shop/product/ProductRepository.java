package com.example.shop.product;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class ProductRepository {

    private final ConcurrentMap<String, Product> store = new ConcurrentHashMap<>();

    public ProductRepository() {
        seed();
    }

    private void seed() {
        save(new Product("p-1001", "Echo Dot (5th Gen)",
                "Smart speaker with Alexa, charcoal", new BigDecimal("49.99"),
                "https://images.unsplash.com/photo-1543512214-318c7553f230?w=400&h=400&fit=crop", 120));
        save(new Product("p-1002", "Kindle Paperwhite",
                "16 GB, 6.8\" display, adjustable warm light", new BigDecimal("139.99"),
                "https://images.unsplash.com/photo-1592434134753-a70baf7979d5?w=400&h=400&fit=crop", 75));
        save(new Product("p-1003", "Fire TV Stick 4K Max",
                "Streaming device with Wi-Fi 6E support", new BigDecimal("59.99"),
                "https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=400&h=400&fit=crop", 200));
        save(new Product("p-1004", "AmazonBasics USB-C Cable",
                "6-foot, 60W charging cable", new BigDecimal("9.49"),
                "https://images.unsplash.com/photo-1583863788434-e58a36330cf0?w=400&h=400&fit=crop", 500));
        save(new Product("p-1005", "Ring Video Doorbell",
                "1080p HD video, motion detection", new BigDecimal("99.99"),
                "https://images.unsplash.com/photo-1558002038-1055907df827?w=400&h=400&fit=crop", 60));
    }

    public Collection<Product> findAll() {
        return store.values();
    }

    public Optional<Product> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public Product save(Product product) {
        store.put(product.getId(), product);
        return product;
    }

    public synchronized boolean decrementStock(String id, int quantity) {
        Product p = store.get(id);
        if (p == null || p.getStock() < quantity) {
            return false;
        }
        p.setStock(p.getStock() - quantity);
        return true;
    }
}
