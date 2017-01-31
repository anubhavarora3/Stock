package com.example.anubharora.stock.model;

/**
 * Created by anubharora on 1/18/17.
 */

public class CurrentStock {

    private String symbol, name, ask, Open, previousClose;

    public CurrentStock(String name, String symbol, String ask, String previousClose){
        this.name = name;
        this.symbol = symbol;
        this.ask = ask;
        this.previousClose = previousClose;
    }
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getASK() {
        return ask;
    }

    public void setASK(String ASK) {
        this.ask = ASK;
    }

    public String getOpen() {
        return Open;
    }

    public void setOpen(String open) {
        Open = open;
    }

    public String getPreviousClose() {
        return previousClose;
    }

    public void setPreviousClose(String previousClose) {
        previousClose = previousClose;
    }
}
