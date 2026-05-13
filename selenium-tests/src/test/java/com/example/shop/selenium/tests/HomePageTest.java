package com.example.shop.selenium.tests;

import com.example.shop.selenium.base.BaseTest;
import com.example.shop.selenium.pages.HomePage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HomePageTest extends BaseTest {

    @Test(description = "Home page renders heading, logo, and product grid")
    public void homePageRendersStorefront() {
        HomePage home = new HomePage(driver);

        Assert.assertTrue(home.isLogoVisible(), "shop.io logo should be visible");
        Assert.assertEquals(home.heading(), "Today's Picks");
        Assert.assertEquals(home.productCount(), 5, "Expected 5 seeded products");
    }

    @Test(description = "Cart badge starts at zero")
    public void cartBadgeStartsAtZero() {
        HomePage home = new HomePage(driver);
        Assert.assertEquals(home.cartBadgeCount(), 0);
    }
}
