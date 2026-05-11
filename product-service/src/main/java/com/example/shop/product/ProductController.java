package com.example.shop.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository repository;

    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Collection<Product> list() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> get(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/reserve")
    public ResponseEntity<Map<String, Object>> reserve(@PathVariable String id,
                                                       @RequestParam int quantity) {
        boolean ok = repository.decrementStock(id, quantity);
        if (!ok) {
            return ResponseEntity.badRequest().body(Map.of(
                    "reserved", false,
                    "reason", "insufficient_stock_or_unknown_product"));
        }
        return ResponseEntity.ok(Map.of("reserved", true, "productId", id, "quantity", quantity));
    }
}
