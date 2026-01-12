package com.automation.pages;

import com.automation.utils.WaitUtils;
import com.automation.utils.ReportManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ProductDetailPage {
    private final WebDriver driver;

    private final By productName = By.xpath("//div[@class='product-information']//h2|//h2[@itemprop='name']");
    private final By productCategory = By.xpath("//div[@class='product-information']//p[contains(.,'Category')]|//p[contains(.,'Category')]");
    private final By productPrice = By.cssSelector(".product-information .price");
    private final By availability = By.xpath("//b[contains(.,'Availability')]/following-sibling::text()|//p[contains(.,'Availability')]");
    private final By condition = By.xpath("//b[contains(.,'Condition')]/following-sibling::text()|//p[contains(.,'Condition')]");
    private final By brand = By.xpath("//b[contains(.,'Brand')]/following-sibling::text()|//p[contains(.,'Brand')]");

    public ProductDetailPage(WebDriver driver) {
        this.driver = driver;
    }

    public String getProductName() {
        WebElement el = WaitUtils.waitForVisibility(driver, productName);
        String txt = el.getText();
        ReportManager.step("Product name: " + txt);
        return txt;
    }

    public String getProductCategory() {
        try {
            WebElement el = WaitUtils.waitForVisibility(driver, productCategory);
            String txt = el.getText();
            ReportManager.step("Product category: " + txt);
            return txt;
        } catch (Exception e) {
            return "";
        }
    }

    public String getProductPrice() {
        try {
            WebElement el = WaitUtils.waitForVisibility(driver, productPrice);
            String txt = el.getText();
            ReportManager.step("Product price: " + txt);
            return txt;
        } catch (Exception e) {
            return "";
        }
    }

    public String getAvailability() {
        try {
            WebElement el = WaitUtils.waitForVisibility(driver, availability);
            String txt = el.getText();
            ReportManager.step("Product availability: " + txt);
            return txt;
        } catch (Exception e) {
            return "";
        }
    }

    public String getCondition() {
        try {
            WebElement el = WaitUtils.waitForVisibility(driver, condition);
            String txt = el.getText();
            ReportManager.step("Product condition: " + txt);
            return txt;
        } catch (Exception e) {
            return "";
        }
    }

    public String getBrand() {
        try {
            WebElement el = WaitUtils.waitForVisibility(driver, brand);
            String txt = el.getText();
            ReportManager.step("Product brand: " + txt);
            return txt;
        } catch (Exception e) {
            return "";
        }
    }
}

