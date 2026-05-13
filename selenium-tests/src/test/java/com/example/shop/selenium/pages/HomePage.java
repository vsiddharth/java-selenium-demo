package com.example.shop.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class HomePage extends BasePage {

    private static final By PRODUCT_CARDS = By.cssSelector(".grid .card");
    private static final By PAGE_HEADING = By.tagName("h1");
    private static final By LOGO = By.cssSelector(".logo");
    private static final By CART_LINK = By.cssSelector(".cart-link");
    private static final By CART_BADGE = By.cssSelector(".cart-badge");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public String heading() {
        return text(PAGE_HEADING);
    }

    public boolean isLogoVisible() {
        return waitForVisible(LOGO).isDisplayed();
    }

    public List<WebElement> productCards() {
        waitForVisible(PRODUCT_CARDS);
        return driver.findElements(PRODUCT_CARDS);
    }

    public int productCount() {
        return productCards().size();
    }

    public HomePage addFirstProductToCart() {
        WebElement firstCard = productCards().get(0);
        firstCard.findElement(By.cssSelector("button[type=submit]")).click();
        return this;
    }

    public HomePage addProductToCartByName(String name) {
        for (WebElement card : productCards()) {
            String title = card.findElement(By.cssSelector("a.title")).getText();
            if (title.equalsIgnoreCase(name)) {
                card.findElement(By.cssSelector("button[type=submit]")).click();
                return this;
            }
        }
        throw new IllegalArgumentException("Product not found on home page: " + name);
    }

    public ProductDetailPage openProductByName(String name) {
        for (WebElement card : productCards()) {
            WebElement title = card.findElement(By.cssSelector("a.title"));
            if (title.getText().equalsIgnoreCase(name)) {
                title.click();
                return new ProductDetailPage(driver);
            }
        }
        throw new IllegalArgumentException("Product not found: " + name);
    }

    public CartPage openCart() {
        click(CART_LINK);
        return new CartPage(driver);
    }

    public int cartBadgeCount() {
        String txt = text(CART_BADGE).trim();
        return txt.isEmpty() ? 0 : Integer.parseInt(txt);
    }
}
