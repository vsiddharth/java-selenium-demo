package com.example.shop.frontend;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CartTest {

    @Test
    void addAccumulatesQuantityForSameProduct() {
        Cart cart = new Cart();
        cart.add("p-1", 2);
        cart.add("p-1", 3);

        assertThat(cart.getItems()).containsEntry("p-1", 5);
        assertThat(cart.totalQuantity()).isEqualTo(5);
    }

    @Test
    void addKeepsItemsInInsertionOrder() {
        Cart cart = new Cart();
        cart.add("p-2", 1);
        cart.add("p-1", 1);
        cart.add("p-3", 1);

        assertThat(cart.getItems().keySet()).containsExactly("p-2", "p-1", "p-3");
    }

    @Test
    void removeDeletesItem() {
        Cart cart = new Cart();
        cart.add("p-1", 1);
        cart.add("p-2", 2);

        cart.remove("p-1");

        assertThat(cart.getItems()).doesNotContainKey("p-1");
        assertThat(cart.totalQuantity()).isEqualTo(2);
    }

    @Test
    void clearEmptiesCart() {
        Cart cart = new Cart();
        cart.add("p-1", 1);

        cart.clear();

        assertThat(cart.isEmpty()).isTrue();
        assertThat(cart.totalQuantity()).isZero();
    }

    @Test
    void newCartIsEmpty() {
        assertThat(new Cart().isEmpty()).isTrue();
    }
}
