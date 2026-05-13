package com.example.shop.selenium.tests;

import com.example.shop.selenium.base.BaseTest;
import com.example.shop.selenium.pages.CartPage;
import com.example.shop.selenium.pages.ConfirmationPage;
import com.example.shop.selenium.pages.HomePage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CheckoutTest extends BaseTest {

    @Test(description = "End-to-end checkout: home -> cart -> order placed")
    public void placeOrderHappyPath() {
        HomePage home = new HomePage(driver);
        home.addProductToCartByName("Kindle Paperwhite");

        CartPage cart = new CartPage(driver);
        Assert.assertEquals(cart.rowCount(), 1);

        ConfirmationPage conf = cart
                .selectShippingUserContaining("Ada Lovelace")
                .placeOrder();

        Assert.assertTrue(conf.thanksHeading().contains("Ada Lovelace"),
                "Thanks heading should greet Ada Lovelace; got: " + conf.thanksHeading());
        Assert.assertTrue(conf.orderId().startsWith("o-"),
                "Order id should be of form o-xxxxxxxx; got: " + conf.orderId());
        Assert.assertEquals(conf.lineCount(), 1);
        Assert.assertTrue(conf.orderTotalText().contains("139.99"),
                "Expected total 139.99 (Kindle); got: " + conf.orderTotalText());
    }
}
