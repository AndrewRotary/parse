package com.currencyparser.utils;

import com.currencyparser.service.CurrencyService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Class that will run a scheduled task
 */
@Component
public class ScheduledTask {

    private static final LocalDateTime dateFormat =  LocalDateTime.now();

    @Autowired
    private CurrencyService currencyService;

    @Scheduled(cron = "0 0/30 * * * ?")
    public void reportCurrentTime() throws InterruptedException, IOException {
        System.out.println("data deleted = " + LocalDateTime.now());
        currencyService.saveCurrenciesFormWebSites();
        System.out.println("Succesful shceduled operation");
    }
}
