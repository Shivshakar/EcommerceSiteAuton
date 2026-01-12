package com.automation.tests;

import com.automation.base.TestBase;
import com.automation.pages.ProductDetailPage;
import com.automation.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProductsTest extends TestBase {

    @Test
    public void testViewProductDetails() {
        ProductsPage products = new ProductsPage(driver);
        ProductDetailPage detail = new ProductDetailPage(driver);

        // Navigate to home
        driver.get(baseUrl);

        // Click products
        products.clickProductsLink();
        Assert.assertTrue(products.isAllProductsVisible(), "All Products header should be visible");
        Assert.assertTrue(products.isProductListVisible(), "Product list should be visible");

        // Click View Product on first product
        products.clickViewProduct(0);

        // Validate details (presence checks)
        String name = detail.getProductName();
        Assert.assertNotNull(name);
        Assert.assertFalse(name.trim().isEmpty(), "Product name should be visible");

        String category = detail.getProductCategory();
        Assert.assertNotNull(category);

        String price = detail.getProductPrice();
        Assert.assertNotNull(price);

        String avail = detail.getAvailability();
        Assert.assertNotNull(avail);

        String condition = detail.getCondition();
        Assert.assertNotNull(condition);

        String brand = detail.getBrand();
        Assert.assertNotNull(brand);
    }
}

