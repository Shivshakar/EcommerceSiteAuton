package com.automation.tests;

import com.automation.base.TestBase;
import com.automation.pages.ProductDetailPage;
import com.automation.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SearchProductTest extends TestBase {

    @Test
    public void testSearchProduct() {
        ProductsPage products = new ProductsPage(driver);

        // Navigate to home
        driver.get(baseUrl);

        // Click products
        products.clickProductsLink();

        // Verify All Products page
        Assert.assertTrue(products.isAllProductsVisible(), "Expected All Products page to be visible");
        Assert.assertTrue(products.isProductListVisible(), "Expected product list to be visible");

        // Search for a product (use 'Dress' as a generic example)
        String query = "Dress";
        products.enterSearchQuery(query);
        products.clickSearchButton();

        // Verify searched products header and results
        Assert.assertTrue(products.isSearchedProductsVisible(), "Expected 'Searched Products' header after search");
        Assert.assertTrue(products.areSearchResultsVisible(), "Expected search results to be visible");
    }
}

