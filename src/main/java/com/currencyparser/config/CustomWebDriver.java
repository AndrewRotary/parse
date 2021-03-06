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

//        System.setProperty("webdriver.chrome.driver","/etc/alternatives/google-chrome");
        ChromeDriverManager.getInstance().setup();
        ChromeOptions options = new ChromeOptions();
//        options.setBinary("/opt/google/chrome/google-chrome");
        options.addArguments("headless");
        options.addArguments("no-sandbox");
        options.addArguments("disable-gpu");
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
