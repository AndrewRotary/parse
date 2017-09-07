package com.currencyparser.controller;

import com.currencyparser.domain.Currency;
import com.currencyparser.pojo.CommonCurrency;
import com.currencyparser.service.CurrencyService;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;


/**
 * Controller for parsing data
 */
@RestController
public class ParserController {

    @Autowired
    private CurrencyService currencyService;


    @RequestMapping("/saxo")
    public void getCurrencyFormSaxoWeb() throws IOException, InterruptedException {
         currencyService.saveCurrenciesFormWebSites();
    }



}
