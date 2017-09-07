package com.currencyparser.controller;

import com.currencyparser.pojo.CommonCurrency;
import com.currencyparser.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

/**
 * Created by Root on 26.07.2017.
 */
@Controller
public class HomeController {

    @Autowired
    private CurrencyService currencyService;

    @RequestMapping("/")
    public String home(Model model) throws IOException, InterruptedException {
        List<CommonCurrency> commonCurrencyList = currencyService.commonResultForView();
        model.addAttribute("currency", commonCurrencyList);
        return "home";
    }
}
