package com.example.shop.selenium.tests;

import com.example.shop.selenium.base.BaseTest;
import com.example.shop.selenium.config.Config;
import com.example.shop.selenium.pages.OrdersPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class OrdersPageTest extends BaseTest {

    @Test(description = "Orders page renders and shows 'no orders' for unselected user")
    public void ordersPageRenders() {
        driver.get(Config.baseUrl() + "/orders");
        OrdersPage orders = new OrdersPage(driver);

        Assert.assertEquals(orders.heading(), "Orders");
        Assert.assertEquals(orders.orderCount(), 0,
                "No user selected => no order cards expected");
    }
}
