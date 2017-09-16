package com.currencyparser.service;

import com.currencyparser.config.CustomWebDriver;
import com.currencyparser.domain.Company;
import com.currencyparser.domain.Currency;
import com.currencyparser.domain.CurrencyPair;
import com.currencyparser.pojo.AlpariCurrencyDataPojo;
import com.currencyparser.pojo.CommonCurrency;
import com.currencyparser.repository.CompanyRepository;
import com.currencyparser.repository.CurrencyPairRepository;
import com.currencyparser.repository.CurrencyRepository;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlListItem;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for Currency.class
 */
@Service
public class CurrencyService {

    private static final String OANDA_WEBSITE = "https://www.oanda.com/lang/ru/forex-trading/analysis/open-position-ratios";
    private static final String DUKASCOPY_WEBSITE = "https://www.dukascopy.com/swiss/english/marketwatch/sentiment/";
    private static final String SAXOBANK_WEBSITE = "https://www.tradingfloor.com/tools/fx-open-positions";
    private static final String FOREX_FACTORY = "https://www.forexfactory.com/";
    private static final String MYFXBOOK = "http://www.myfxbook.com/community/outlook/";
    private static final String ALPARI = "https://alpari.com/activity?id=954c306f076d2959a5f7c3b54870205b";

    private static final String CHROMEDRIVER_LOCATION = "E:\\IT\\currencyParser\\src\\main\\resources\\static\\chromedriver.exe";

    @Autowired
    private CurrencyPairRepository currencyPairRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private CustomWebDriver customWebDriver = new CustomWebDriver();

    /**
     * Get currency from Oanda website
     * @return list of {@link Currency}
     * @throws IOException
     */
    public List<Currency> getCurrencyListFromOandaWebSite() throws IOException {
        System.out.println("Get from:" + OANDA_WEBSITE);
        Company oanda = companyRepository.findByName("Oanda");
        List<Currency> currencies = new ArrayList<>();
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);

        HtmlPage page = client.getPage(OANDA_WEBSITE);
        //Get main graph element with all data
        HtmlElement autocompleteList = (HtmlElement) page.getElementById("long-short-ratio-graph");
        //Save <li> with currency name and position in list
        List<?> courencyList = autocompleteList.getByXPath("li");

        for (int j = 0; j < courencyList.size(); j++) {
            Currency currency = new Currency();
            //get currency name
            HtmlListItem liCurrency = (HtmlListItem) courencyList.get(j);
            HtmlSpan spanCurrencyName = liCurrency.getFirstByXPath("span");
            String courseName = spanCurrencyName.asText();
            //get currency long and short postion
            HtmlSpan longPositionSpan = liCurrency.getFirstByXPath("div/div/span[@class='long-position']");
            HtmlSpan shortPositionSpan = liCurrency.getFirstByXPath("div/div/span[@class='short-position']");
            Double longPos = getDoubleFromSubString(longPositionSpan.asText());
            Double shortPos = getDoubleFromSubString(shortPositionSpan.asText());
            currency.setName(courseName);
            currency.setBuyers(longPos);
            currency.setSellers(shortPos);
            currency.setCompany(oanda);
            currency.setDate(LocalDate.now());
            currencies.add(currency);

        }
        client.close();
        return currencies;
    }

    /**
     * Get currency from Dukaskopy website
     * @return list of {@link Currency}
     * @throws IOException
     */
    public List<Currency> getCurrencyFormDukaskopyWebSite() throws IOException, InterruptedException {
        System.out.println("Get from:" + DUKASCOPY_WEBSITE);
        Company dukaskopy = companyRepository.findByName("DukasCopy");

        WebDriver driver = customWebDriver.getWebDriver();
        driver.get(DUKASCOPY_WEBSITE);
        Integer size = driver.findElements(By.tagName("iframe")).size();
        List<WebElement> searchBox = null;
        Integer elements;
        List<Currency> currencyList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            driver.switchTo().frame(i);
            elements = driver.findElements(By.className("F-qb-Ab")).size();
            int contor = 0;
            if (elements != 0) {
                searchBox = driver.findElements(By.className("F-qb-Ab"));
                for (int j = 0; j < searchBox.size(); j++) {
                    Currency currency = new Currency();
                    WebElement currencyName = searchBox.get(j).findElement(new By.ByClassName("F-qb-Db-hb"));
                    WebElement currencyBuyers = searchBox.get(j).findElement(new By.ByClassName("F-qb-Gb")).findElement(By.tagName("span"));
                    WebElement currencySeller = searchBox.get(j).findElement(new By.ByClassName("F-qb-Fb")).findElement(By.tagName("span"));
                    if (currencyName.getText().length() > 1) {
                        currency.setName(currencyName.getText());
                        contor++;
                        currency.setSellers(getDoubleFromSubString(currencySeller.getText()));
                        currency.setBuyers(getDoubleFromSubString(currencyBuyers.getText()));
                        currency.setCompany(dukaskopy);
                        currency.setDate(LocalDate.now());
                        currencyList.add(currency);
                    }
                }
            }
            driver.switchTo().defaultContent();
        }

        return currencyList;
    }

    /**
     * Get currency from SaxoBank website
     * @return list of {@link Currency}
     * @throws IOException
     */
    public List<Currency> getCurrencyFormSaxoBankWebsite() throws IOException, InterruptedException {
        System.out.println("Get from:" + SAXOBANK_WEBSITE);
        Company saxoBank = companyRepository.findByName("Saxo Bank");

        WebDriver driver = customWebDriver.getWebDriver();
        driver.get(SAXOBANK_WEBSITE);
        Integer size = driver.findElements(By.tagName("iframe")).size();
        List<WebElement> searchBox = null;
        Integer elements;
        List<Currency> currencyList = new ArrayList<>();
        List<CurrencyPair> currencyPairs = (List<CurrencyPair>) currencyPairRepository.findAll();
        for (int i = 0; i < size; i++) {
            driver.switchTo().frame(i);
            elements = driver.findElements(By.className("CurrencyButton")).size();
            int contor = 0;
            if (elements != 0) {
                WebElement elementToBeSearchIn = driver.findElement(By.id("ctl00_MainContent_PositionRatios1_canvasContainer"));
                List<WebElement> inputToBeHovered = elementToBeSearchIn.findElements(By.tagName("input"));
                Boolean waitUntill;
                Thread.sleep(3000);
                for (int j = 0; j < inputToBeHovered.size(); j++) {
                    waitUntill = false;
                    WebDriverWait wait = new WebDriverWait(driver, 10);
                    wait.equals(waitUntill);
                    Actions builder = new Actions(driver);
                    builder.moveToElement(inputToBeHovered.get(j)).build().perform();
                    Thread.sleep(1000);
                    List<WebElement> tableWithPositions = elementToBeSearchIn.findElements(new By.ByTagName("td"));
                    Currency currency = new Currency();
                    WebElement currencyName = inputToBeHovered.get(j);
                    WebElement currencySeller = tableWithPositions.get(0);
                    WebElement currencyBuyers = tableWithPositions.get(2);
                    currency.setName(divideCurrencyPair(currencyName.getAttribute("value")));
                    contor++;
                    currency.setSellers(getDoubleFromSubString(currencySeller.getText()));
                    currency.setBuyers(getDoubleFromSubString(currencyBuyers.getText()));
                    currency.setCompany(saxoBank);
                    currency.setDate(LocalDate.now());
                    currencyList.add(currency);
                    waitUntill = true;
                }
            }
            driver.switchTo().defaultContent();
        }

        return currencyList;
    }

    /**
     * Get currency from ForexFactory website
     * @return list of {@link Currency}
     * @throws IOException
     */
    public List<Currency> getCurrencyFromForexFactory() throws InterruptedException {
        Company forexFactory = companyRepository.findByName("Forex Factory");

        System.out.println("Get from:" + FOREX_FACTORY);
        WebDriver driver = customWebDriver.getWebDriver();
        driver.get(FOREX_FACTORY);
        System.out.println("page opened");
        List<Currency> currencyList = new ArrayList<>();
        Boolean waitUntill = false;
        WebElement widgetWrapper = driver.findElement(By.id("flexBox_flex_trades/positions_tradesPositionsCopy1"));
        WebElement btnExpand = widgetWrapper.findElement(new By.ByClassName("flexMore"));
        WebDriverWait wait = new WebDriverWait(driver, 10);
        Actions builder = new Actions(driver);
        builder.click(btnExpand).build().perform();
        waitUntill = true;
        Thread.sleep(3000);
        List<WebElement> wrPostions = driver.findElements(By.className("trades_position"));
        for (int i = 0; i < wrPostions.size(); i++) {
            Currency currency = new Currency();
            WebElement currencyName = wrPostions.get(i).findElement(new By.ByClassName("currency"));
            WebElement currencyBuyers = wrPostions.get(i).findElement(By.className("long"));
            currencyBuyers = currencyBuyers.findElement(By.tagName("strong"));
            WebElement currencySeller = wrPostions.get(i).findElement(By.className("short"));
            currencySeller = currencySeller.findElement(By.tagName("strong"));
            currency.setName(currencyName.getText());
            currency.setSellers(getDoubleFromSubString(currencySeller.getText()));
            currency.setBuyers(getDoubleFromSubString(currencyBuyers.getText()));
            currency.setCompany(forexFactory);
            currency.setDate(LocalDate.now());
            currencyList.add(currency);
        }


        return currencyList;
    }

    /**
     * Get currency from MyFxBook website
     * @return list of {@link Currency}
     * @throws IOException
     */
    public List<Currency> getCurrencyFromMyFxBook() {
        System.out.println("Get from:" + MYFXBOOK);
        Company myFxBook = companyRepository.findByName("MyFxBook");

        WebDriver driver = customWebDriver.getWebDriver();
        List<CurrencyPair> currencyPairs = (List<CurrencyPair>) currencyPairRepository.findAll();
        List<Currency> currencyList = new ArrayList<>();
        for (int i = 0; i < currencyPairs.size(); i++) {
            driver.get(MYFXBOOK + deleteDividerCurrencyPair(currencyPairs.get(i).getName()));
            WebElement wr = driver.findElement(By.xpath("//table[contains(@class, 'center paddTD10 dataTable maxWidth')]"));
            Currency currency = new Currency();
            currency.setName(currencyPairs.get(i).getName());
            WebElement shortTr = wr.findElement(By.xpath("//tr [3]"));
            String shrotString = shortTr.findElement(By.xpath("td [2]")).getText();
            currency.setSellers(getDoubleFromSubString(shrotString));
            WebElement longTr = wr.findElement(By.xpath("//tr [4]"));
            String longString = longTr.findElement(By.xpath("td [2]")).getText();
            currency.setBuyers(getDoubleFromSubString((longString)));
            currency.setCompany(myFxBook);
            currency.setDate(LocalDate.now());
            currencyList.add(currency);
        }

        return currencyList;

    }

    /**
     * Get currency from Alpari website
     * @return list of {@link Currency}
     * @throws IOException
     */
    public List<Currency> getCurrencyFromAlparyJSON() throws IOException {
        System.out.println("Get from:" + ALPARI);
        Company alpari = companyRepository.findByName("Alpari");
        List<CurrencyPair> currencyPairs = (List<CurrencyPair>) currencyPairRepository.findAll();
        List<Currency> currencyList = new ArrayList<>();
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        Page page = client.getPage(ALPARI);
        WebResponse response = page.getWebResponse();
        Map<String, AlpariCurrencyDataPojo> alpariPairs = new HashMap<>();
        if (response.getContentType().equals("text/plain")) {
            String json = response.getContentAsString();
            Gson g = new Gson();
            alpariPairs = g.fromJson(json, new TypeToken<Map<String, AlpariCurrencyDataPojo>>(){}.getType());
        }
        for (int i = 0; i < currencyPairs.size(); i++) {
            for (Map.Entry<String, AlpariCurrencyDataPojo> entry : alpariPairs.entrySet())
            {
                if(currencyPairs.get(i).getName().equals(divideCurrencyPair(entry.getKey()))){
                    Currency currency = new Currency();
                    currency.setName(currencyPairs.get(i).getName());
                    currency.setBuyers(entry.getValue().getBuy());
                    currency.setSellers(entry.getValue().getSell());
                    currency.setCompany(alpari);
                    currency.setDate(LocalDate.now());
                    currencyList.add(currency);
                }
            }
        }
        client.close();
        return currencyList;
    }

    public List<Currency> getCurrencyFromAlpary(){
        System.out.println("Get from:" + ALPARI);
        Company alpari = companyRepository.findByName("Alpari");
        List<CurrencyPair> currencyPairs = (List<CurrencyPair>) currencyPairRepository.findAll();
        List<Currency> currencyList = new ArrayList<>();
        WebDriver driver = customWebDriver.getWebDriver();
        driver.get("https://alpari.com/en/analytics/informers/forex_open_positions/");
        WebElement btnOpen = driver.findElement(By.xpath("//button[contains(@class, 'js-form-el-holder js-form-el__input button s_float_l focus')]"));
        WebDriverWait wait = new WebDriverWait(driver, 10);
        Actions builder = new Actions(driver);
        builder.click(btnOpen).build().perform();
        WebElement popup = driver.findElement(By.className("popup__content"));
        for (int i = 0; i < currencyPairs.size(); i++) {
            if ( !popup.findElement(By.name(deleteDividerCurrencyPair(currencyPairs.get(i).getName()))).isSelected() )
            {
                popup.findElement(By.name(deleteDividerCurrencyPair(currencyPairs.get(i).getName()))).click();
            }
        }

        return currencyList;
    }
    /**
     * Get all List<currency> from 6 websites filter by currency name and return in a {@link CommonCurrency}
     * @return {@link CommonCurrency} all currencies.
     * @throws IOException
     * @throws InterruptedException
     */
    public void saveCurrenciesFormWebSites() throws IOException, InterruptedException {
        Company oanda = companyRepository.findByName("Oanda");
        Company dukaskopy = companyRepository.findByName("DukasCopy");
        Company saxoBank = companyRepository.findByName("Saxo Bank");
        Company forexFactory = companyRepository.findByName("Forex Factory");
        Company myFxBook = companyRepository.findByName("MyFxBook");
        try {
            List<Currency> list = getCurrencyFromForexFactory();
            if(currencyRepository.findAllByCompany(forexFactory).isPresent()){
                List<Currency> toDelete = currencyRepository.findAllByCompany(forexFactory).get();
                for (int i = 0; i < toDelete.size(); i++) {
                    currencyRepository.delete(toDelete.get(i).getId());
                }
            }
            Thread.sleep(3000);
            List<Currency> currenciesForexFactory = saveCurrenciesByPairName(list);
        }catch (NullPointerException e){
            System.out.println("Some null data in ForexFactory");
        }
        try{
            List<Currency> list = getCurrencyListFromOandaWebSite();
            if(currencyRepository.findAllByCompany(oanda).isPresent()) {
                List<Currency> toDelete = currencyRepository.findAllByCompany(oanda).get();
                for (int i = 0; i < toDelete.size(); i++) {
                    currencyRepository.delete(toDelete.get(i).getId());
                }
            }
            Thread.sleep(3000);
            List<Currency> currenciesOanda = saveCurrenciesByPairName(list);
        }catch (NullPointerException e){
            System.out.println("Some null data in Oanda");
        }
        try{
            List<Currency> list = getCurrencyFormDukaskopyWebSite();
            if(currencyRepository.findAllByCompany(dukaskopy).isPresent()) {
                List<Currency> toDelete = currencyRepository.findAllByCompany(dukaskopy).get();
                for (int i = 0; i < toDelete.size(); i++) {
                    currencyRepository.delete(toDelete.get(i).getId());
                }
            }
            Thread.sleep(3000);
            List<Currency> currenciesDukascopy = saveCurrenciesByPairName(list);
        }catch (NullPointerException e){
            System.out.println("Some null data in Dukaskopy");
        }
        try{
            List<Currency> list = getCurrencyFormSaxoBankWebsite();
            if(currencyRepository.findAllByCompany(saxoBank).isPresent()) {
                List<Currency> toDelete = currencyRepository.findAllByCompany(saxoBank).get();
                for (int i = 0; i < toDelete.size(); i++) {
                    currencyRepository.delete(toDelete.get(i).getId());
                };
            }
            Thread.sleep(3000);
            List<Currency> currenciesSaxobank = saveCurrenciesByPairName(list);
        }catch (NullPointerException e){
            System.out.println("Some null data in Saxobank");
        }
        try{
            List<Currency> list = getCurrencyFromMyFxBook();
            if(currencyRepository.findAllByCompany(myFxBook).isPresent()) {
                List<Currency> toDelete = currencyRepository.findAllByCompany(myFxBook).get();
                for (int i = 0; i < toDelete.size(); i++) {
                    currencyRepository.delete(toDelete.get(i).getId());
                };
            }
            Thread.sleep(3000);
            List<Currency> currenciesMyFxBook = saveCurrenciesByPairName(list);
        }catch (NullPointerException e){
            System.out.println("Some null data in MyFxBook");
        }
    }

    public List<Currency> calculateCurrencies(List<Currency> currentList, List<Currency> addList){
        for(Currency currency: currentList){
            Currency toAdd = addList.stream()
                    .filter(c -> c.getName().equals(currency.getName()))
                    .findFirst()
                    .get();
            Double buyers = (currency.getBuyers() + toAdd.getBuyers()) / 2;
            Double sellers = (currency.getSellers() + toAdd.getSellers()) / 2;
            Double truncatedbByers = BigDecimal.valueOf(buyers)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
            Double truncatedSellers = BigDecimal.valueOf(sellers)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
            currency.setBuyers(truncatedbByers);
            currency.setSellers(truncatedSellers);
        }

        return currentList;
    }

    public Double calculateCurrencies(Double a, Double b){
        a = (a + b) / 2;
        return BigDecimal.valueOf(a)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public List<CommonCurrency> commonResultForView () throws IOException, InterruptedException {
         Company oanda = companyRepository.findByName("Oanda");
         Company dukaskopy = companyRepository.findByName("DukasCopy");
         Company saxoBank = companyRepository.findByName("Saxo Bank");
         Company forexFactory = companyRepository.findByName("Forex Factory");
         Company myFxBook = companyRepository.findByName("MyFxBook");
        List<CommonCurrency> commonCurrencies = new ArrayList<>();

        List<CurrencyPair> currencyPairs = currencyPairRepository.findAll();
        for(CurrencyPair currencyPair: currencyPairs){
            CommonCurrency commonCurrency = new CommonCurrency();
            commonCurrency.setLastRefresh(LocalTime.now());
            commonCurrency.setName(currencyPair.getName());
            Currency dukaskopyCurrency = currencyRepository.findByCompanyAndNameAndDate(dukaskopy, currencyPair.getName(), LocalDate.now());
            commonCurrency.setCurrencieDukascopy(dukaskopyCurrency);
            Currency oandaCurrency = currencyRepository.findByCompanyAndNameAndDate(oanda, currencyPair.getName(), LocalDate.now());
            commonCurrency.setCurrencieOanda(oandaCurrency);
            if(oandaCurrency != null && dukaskopyCurrency != null){
                commonCurrency.setBuyers(calculateCurrencies(oandaCurrency.getBuyers(), dukaskopyCurrency.getBuyers()));
                commonCurrency.setSellers(calculateCurrencies(oandaCurrency.getSellers(), dukaskopyCurrency.getSellers()));
            }else {
                try {
                    commonCurrency.setBuyers(oandaCurrency.getBuyers());
                    commonCurrency.setSellers(oandaCurrency.getSellers());
                }catch (NullPointerException e){
                    //If no data for today search for it again
                    System.out.println("data deleted = " + LocalDateTime.now());
                    saveCurrenciesFormWebSites();
                    System.out.println("Succesful shceduled operation");
                }
            }
            Currency forexFactoryCurrency = currencyRepository.findByCompanyAndNameAndDate(forexFactory, currencyPair.getName(), LocalDate.now());
            commonCurrency.setCurrencieForexFactory(forexFactoryCurrency);
            if(forexFactoryCurrency != null){
                commonCurrency.setBuyers(calculateCurrencies(commonCurrency.getBuyers(), forexFactoryCurrency.getBuyers()));
                commonCurrency.setSellers(calculateCurrencies(commonCurrency.getSellers(), forexFactoryCurrency.getSellers()));
            }
            Currency myFxBookCurrency = currencyRepository.findByCompanyAndNameAndDate(myFxBook, currencyPair.getName(), LocalDate.now());
            commonCurrency.setCurrencieMyFxBook(myFxBookCurrency);
            if(myFxBookCurrency != null){
                commonCurrency.setBuyers(calculateCurrencies(commonCurrency.getBuyers(), myFxBookCurrency.getBuyers()));
                commonCurrency.setSellers(calculateCurrencies(commonCurrency.getSellers(), myFxBookCurrency.getSellers()));
            }
            Currency saxoBankCurrency = currencyRepository.findByCompanyAndNameAndDate(saxoBank, currencyPair.getName(), LocalDate.now());
            commonCurrency.setCurrencieSaxoBank(saxoBankCurrency);
            if(saxoBankCurrency != null){
                commonCurrency.setBuyers(calculateCurrencies(commonCurrency.getBuyers(), saxoBankCurrency.getBuyers()));
                commonCurrency.setSellers(calculateCurrencies(commonCurrency.getSellers(), saxoBankCurrency.getSellers()));
            }

            commonCurrencies.add(commonCurrency);
        }

        return commonCurrencies;
    }

    /**
     * Get Double value from string and remove '%' o ' ' character from it
     * @param string with double and other character
     * @return double value
     */
    private Double getDoubleFromSubString(String string) {
        String currencyPostionString = string.replace("%", "");
        if (currencyPostionString.contains(" ")) {
            currencyPostionString = currencyPostionString.replaceAll(" ", "");
        }
        return Double.valueOf(currencyPostionString);
    }
    /**
     *
     * @param name divide curerncy ex EURUSD
     * @return string divided EUR/USD
     */
    public String divideCurrencyPair(String name) {
        name = new StringBuilder(name).insert(name.length() - 3, "/").toString();
        return name;
    }

    /**
     * Delete '/' from pair
     * @param name String with '/' ex EUR/USD
     * @return pair name ex EURUSD
     */
    public String deleteDividerCurrencyPair(String name) {
        name = name.replace("/", "");
        return name;
    }

    /**
     * Settings for webdriver headerless
     * @return {@link WebDriver}
     */
    public WebDriver getWebDriverHeaderless() {
        System.setProperty("webdriver.chrome.driver", CHROMEDRIVER_LOCATION);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        options.addArguments("window-size=1900x3000");
        return new ChromeDriver(options);
    }

    public List<Currency> saveCurrenciesByPairName(List<Currency> currencies){
        List<CurrencyPair> neededCurrenci = currencyPairRepository.findAll();
        Set<String> curencyName = neededCurrenci.stream()
                        .map(CurrencyPair::getName)
                        .collect(Collectors.toSet());
        currencies =  currencies
                .stream()
                .filter(c -> curencyName.contains(c.getName()))
                .collect(Collectors.toList());
        int size = currencies.size();
        for(Currency currency: currencies){
            currencyRepository.save(currency);
        }
        return currencies;
    }

    public void deleteAllCurrency() throws InterruptedException {
        currencyRepository.deleteAll();
        Thread.sleep(3000);
    }
}
