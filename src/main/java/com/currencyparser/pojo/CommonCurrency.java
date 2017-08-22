package com.currencyparser.pojo;

import com.currencyparser.domain.Currency;

import java.time.LocalTime;

/**
 * Pojo for common table with currencies
 */
public class CommonCurrency {

    private String Name;
    private Currency currencieOanda;
    private Currency currencieDukascopy;
    private Currency currencieSaxoBank;
    private Currency currencieForexFactory;
    private Currency currencieMyFxBook;
    private Currency currencieAlpari;
    private Double buyers;
    private Double sellers;
    private LocalTime lastRefresh;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Currency getCurrencieOanda() {
        return currencieOanda;
    }

    public void setCurrencieOanda(Currency currencieOanda) {
        this.currencieOanda = currencieOanda;
    }

    public Currency getCurrencieDukascopy() {
        return currencieDukascopy;
    }

    public void setCurrencieDukascopy(Currency currencieDukascopy) {
        this.currencieDukascopy = currencieDukascopy;
    }

    public Double getBuyers() {
        return buyers;
    }

    public void setBuyers(Double buyers) {
        this.buyers = buyers;
    }

    public Double getSellers() {
        return sellers;
    }

    public void setSellers(Double sellers) {
        this.sellers = sellers;
    }

    public Currency getCurrencieSaxoBank() {
        return currencieSaxoBank;
    }

    public void setCurrencieSaxoBank(Currency currencieSaxoBank) {
        this.currencieSaxoBank = currencieSaxoBank;
    }

    public Currency getCurrencieForexFactory() {
        return currencieForexFactory;
    }

    public void setCurrencieForexFactory(Currency currencieForexFactory) {
        this.currencieForexFactory = currencieForexFactory;
    }

    public Currency getCurrencieMyFxBook() {
        return currencieMyFxBook;
    }

    public void setCurrencieMyFxBook(Currency currencieMyFxBook) {
        this.currencieMyFxBook = currencieMyFxBook;
    }

    public Currency getCurrencieAlpari() {
        return currencieAlpari;
    }

    public void setCurrencieAlpari(Currency currencieAlpari) {
        this.currencieAlpari = currencieAlpari;
    }

    public LocalTime getLastRefresh() {
        return lastRefresh;
    }

    public void setLastRefresh(LocalTime lastRefresh) {
        this.lastRefresh = lastRefresh;
    }
}
