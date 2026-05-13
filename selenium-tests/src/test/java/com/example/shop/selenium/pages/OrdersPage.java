package com.example.shop.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class OrdersPage extends BasePage {

    private static final By HEADING = By.tagName("h1");
    private static final By USER_DROPDOWN = By.cssSelector("select[name=userId]");
    private static final By ORDER_CARDS = By.cssSelector(".order-card");
    private static final By NO_ORDERS_MESSAGE = By.xpath("//p[contains(., 'No orders yet')]");

    public OrdersPage(WebDriver driver) {
        super(driver);
    }

    public String heading() {
        return text(HEADING);
    }

    public OrdersPage selectUserContaining(String name) {
        Select select = new Select(waitForVisible(USER_DROPDOWN));
        select.getOptions().stream()
                .filter(o -> o.getText().toLowerCase().contains(name.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No option matches " + name))
                .click();
        return this;
    }

    public int orderCount() {
        return driver.findElements(ORDER_CARDS).size();
    }

    public boolean hasNoOrdersMessage() {
        return !driver.findElements(NO_ORDERS_MESSAGE).isEmpty();
    }
}
