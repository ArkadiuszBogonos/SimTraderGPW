package com.example.simtradergpw;

public class StockRecord {
    String name;
    String ticker;
    String last;
    String percentageChange;
    String turnover;



    public StockRecord(String name, String ticker, String last, String percentageChange) {
        this.name = name;
        this.ticker = ticker;
        this.last = last;
        this.percentageChange = percentageChange;
        this.turnover = "0";
    }

    public StockRecord(String name, String ticker, String last, String percentageChange, String turnover) {
        this.name = name;
        this.ticker = ticker;
        this.last = last;
        this.percentageChange = percentageChange;
        this.turnover = turnover;
    }



    public String getName() {
        return name;
    }

    public String getTicker() {
        return ticker;
    }

    public String getLast() {
        return last;
    }

    public String getPercentageChange() {
        return percentageChange;
    }

    public String getTurnover() { return turnover; }

    public void setName(String name) {
        this.name = name;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public void setPercentageChange(String percentageChange) {
        this.percentageChange = percentageChange;
    }
    public void setTurnover(String turnover) { this.turnover = turnover; }
}
