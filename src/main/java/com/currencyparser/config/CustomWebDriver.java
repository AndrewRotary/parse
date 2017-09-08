package com.currencyparser.config;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

/**
 * Use one custom webDriver
 */
public class CustomWebDriver {


    public static WebDriver webDriver;

    public CustomWebDriver() {

//        System.setProperty("webdriver.chrome.driver","/app/.apt/usr/bin/google-chrome");
        ChromeDriverManager.getInstance().setup();
        ChromeOptions options = new ChromeOptions();
        options.setBinary("/usr/local/bin/chromedrive");
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
