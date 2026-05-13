package com.example.shop.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ConfirmationPage extends BasePage {

    private static final By HEADING = By.tagName("h1");
    private static final By ORDER_ID = By.xpath("//main//p/strong");
    private static final By LINES = By.cssSelector("table.cart-table tbody tr");
    private static final By TOTAL = By.cssSelector("table.cart-table tfoot strong");

    public ConfirmationPage(WebDriver driver) {
        super(driver);
    }

    public String thanksHeading() {
        return text(HEADING);
    }

    public String orderId() {
        return text(ORDER_ID);
    }

    public int lineCount() {
        waitForVisible(LINES);
        return driver.findElements(LINES).size();
    }

    public String orderTotalText() {
        // tfoot has two <strong>: "Total" label and the amount.
        return driver.findElements(TOTAL).get(1).getText();
    }
}
