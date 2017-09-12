package com.currencyparser.config;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Use one custom webDriver
 */
public class CustomWebDriver {


    public static WebDriver webDriver;

    public CustomWebDriver() throws MalformedURLException {
//
//        System.setProperty("webdriver.chrome.driver","/etc/alternatives/google-chrome");
//        ChromeDriverManager.getInstance().setup();
//        ChromeOptions options = new ChromeOptions();
////        options.setBinary("/usr/local/bin/chromedriver");
//        options.addArguments("headless");
//        options.addArguments("window-size=1900x3000");
//        webDriver = new ChromeDriver(options);
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setBrowserName("firefox");
        capabilities.setPlatform(Platform.LINUX);
        webDriver = new RemoteWebDriver(new URL("http://188.225.84.108:4444/wd/hub/"), capabilities);

    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public void setWebDriver(WebDriver webDriver) {
        this.webDriver = webDriver;
    }
}
