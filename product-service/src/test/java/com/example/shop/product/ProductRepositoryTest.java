package com.example.shop.product;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductRepositoryTest {

    private final ProductRepository repo = new ProductRepository();

    @Test
    void seedsCatalogOnConstruction() {
        assertThat(repo.findAll()).hasSize(5);
        assertThat(repo.findById("p-1001")).isPresent();
    }

    @Test
    void saveOverwritesExistingProduct() {
        Product replacement = new Product("p-1001", "Echo Dot v2", "updated",
                new BigDecimal("59.99"), "img", 999);
        repo.save(replacement);

        Product stored = repo.findById("p-1001").orElseThrow();
        assertThat(stored.getName()).isEqualTo("Echo Dot v2");
        assertThat(stored.getStock()).isEqualTo(999);
    }

    @Test
    void decrementStockSucceedsWhenSufficient() {
        Product before = repo.findById("p-1001").orElseThrow();
        int initial = before.getStock();

        boolean ok = repo.decrementStock("p-1001", 3);

        assertThat(ok).isTrue();
        assertThat(repo.findById("p-1001").orElseThrow().getStock()).isEqualTo(initial - 3);
    }

    @Test
    void decrementStockFailsWhenInsufficient() {
        boolean ok = repo.decrementStock("p-1001", 10_000);

        assertThat(ok).isFalse();
        assertThat(repo.findById("p-1001").orElseThrow().getStock()).isPositive();
    }

    @Test
    void decrementStockReturnsFalseForUnknownProduct() {
        assertThat(repo.decrementStock("p-does-not-exist", 1)).isFalse();
    }
}
