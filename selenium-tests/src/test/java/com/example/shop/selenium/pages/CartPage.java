package com.example.shop.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class CartPage extends BasePage {

    private static final By HEADING = By.tagName("h1");
    private static final By EMPTY_MESSAGE = By.xpath("//main/p[contains(., 'cart is empty')]");
    private static final By ROWS = By.cssSelector("table.cart-table tbody tr");
    private static final By ORDER_TOTAL = By.cssSelector("table.cart-table tfoot strong");
    private static final By USER_DROPDOWN = By.cssSelector("select[name=userId]");
    private static final By PLACE_ORDER = By.cssSelector(".checkout button[type=submit]");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public String heading() {
        return text(HEADING);
    }

    public boolean isEmpty() {
        return !driver.findElements(EMPTY_MESSAGE).isEmpty();
    }

    public List<WebElement> rows() {
        return driver.findElements(ROWS);
    }

    public int rowCount() {
        return rows().size();
    }

    public String orderTotalText() {
        // tfoot has two <strong>: "Order total" and the actual amount.
        return driver.findElements(ORDER_TOTAL).get(1).getText();
    }

    public CartPage selectShippingUserByVisibleText(String text) {
        new Select(waitForVisible(USER_DROPDOWN)).selectByVisibleText(text);
        return this;
    }

    public CartPage selectShippingUserContaining(String namePrefix) {
        Select select = new Select(waitForVisible(USER_DROPDOWN));
        for (WebElement option : select.getOptions()) {
            if (option.getText().toLowerCase().contains(namePrefix.toLowerCase())) {
                select.selectByVisibleText(option.getText());
                return this;
            }
        }
        throw new IllegalArgumentException("No shipping user option matches: " + namePrefix);
    }

    public ConfirmationPage placeOrder() {
        click(PLACE_ORDER);
        return new ConfirmationPage(driver);
    }
}
