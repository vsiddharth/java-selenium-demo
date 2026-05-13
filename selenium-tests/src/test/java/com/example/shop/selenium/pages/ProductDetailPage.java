package com.example.shop.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductDetailPage extends BasePage {

    private static final By PRODUCT_NAME = By.cssSelector(".detail-body h1");
    private static final By PRICE = By.cssSelector(".detail-body .price");
    private static final By QUANTITY_INPUT = By.cssSelector(".detail-body input[name=quantity]");
    private static final By ADD_TO_CART = By.cssSelector(".detail-body form button[type=submit]");

    public ProductDetailPage(WebDriver driver) {
        super(driver);
    }

    public String productName() {
        return text(PRODUCT_NAME);
    }

    public String price() {
        return text(PRICE);
    }

    public ProductDetailPage setQuantity(int quantity) {
        var input = waitForVisible(QUANTITY_INPUT);
        input.clear();
        input.sendKeys(String.valueOf(quantity));
        return this;
    }

    public CartPage addToCart() {
        click(ADD_TO_CART);
        return new CartPage(driver);
    }
}
