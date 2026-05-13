package com.example.shop.selenium.tests;

import com.example.shop.selenium.base.BaseTest;
import com.example.shop.selenium.pages.CartPage;
import com.example.shop.selenium.pages.HomePage;
import com.example.shop.selenium.pages.ProductDetailPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AddToCartTest extends BaseTest {

    @Test(description = "Add a product from the home page grid")
    public void addFromHomePageIncrementsCart() {
        HomePage home = new HomePage(driver);
        home.addFirstProductToCart();

        CartPage cart = new CartPage(driver);
        Assert.assertEquals(cart.heading(), "Your Cart");
        Assert.assertFalse(cart.isEmpty(), "Cart should not be empty after add");
        Assert.assertEquals(cart.rowCount(), 1, "Expected 1 line item");
    }

    @Test(description = "Add 2 of a specific product from the product detail page")
    public void addTwoEchoDotsFromProductDetail() {
        HomePage home = new HomePage(driver);
        ProductDetailPage detail = home.openProductByName("Echo Dot (5th Gen)");
        Assert.assertEquals(detail.productName(), "Echo Dot (5th Gen)");

        CartPage cart = detail.setQuantity(2).addToCart();
        Assert.assertEquals(cart.rowCount(), 1);
        // Echo Dot is $49.99 × 2 = $99.98
        Assert.assertTrue(cart.orderTotalText().contains("99.98"),
                "Expected order total to include 99.98 but was: " + cart.orderTotalText());
    }
}
