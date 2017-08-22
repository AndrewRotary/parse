package com.currencyparser.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

/**
 * Use one custom webDriver
 */
public class CustomWebDriver {
    private static final String CHROMEDRIVER_LOCATION = "static/chromedriver.exe";


    public static WebDriver webDriver;

    public CustomWebDriver() {
        System.setProperty("webdriver.chrome.driver", CHROMEDRIVER_LOCATION);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        options.addArguments("window-size=1900x3000");
        webDriver = new ChromeDriver(options);
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public void setWebDriver(WebDriver webDriver) {
        this.webDriver = webDriver;
    }
}
