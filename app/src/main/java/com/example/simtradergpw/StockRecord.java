package com.example.simtradergpw;

import java.util.ArrayList;

public class StockRecord {
    private String name;
    private String ticker;
    private Double last;
    private Double percentageChange;
    private Integer ownedQuantity;
    private String timeStamp;
    private Boolean isBuy;


    public StockRecord(String name, String ticker, Double last, Double percentageChange) {
        this.name = name;
        this.ticker = ticker;
        this.last = last;
        this.percentageChange = percentageChange;
        this.ownedQuantity = null;
        this.timeStamp = null;
        this.isBuy = null;
    }

    public StockRecord(String name, String ticker, Double last, Double percentageChange, Integer ownedQuantity) {
        this.name = name;
        this.ticker = ticker;
        this.last = last;
        this.percentageChange = percentageChange;
        this.ownedQuantity = ownedQuantity;
        this.timeStamp = null;
        this.isBuy = null;
    }

    public StockRecord(String name, String ticker, Double last, Double percentageChange, Integer ownedQuantity, String timeStamp, Boolean isBuy) {
        this.name = name;
        this.ticker = ticker;
        this.last = last;
        this.percentageChange = percentageChange;
        this.ownedQuantity = ownedQuantity;
        this.timeStamp = timeStamp;
        this.isBuy = isBuy;
    }



    public String getName() {
        return name;
    }

    public String getTicker() {
        return ticker;
    }

    public Double getLast() {
        return last;
    }

    public Double getPercentageChange() {
        return percentageChange;
    }

    public Integer getOwnedQuantity() { return ownedQuantity; }

    public String getTimeStamp() {
        return timeStamp;
    }

    public Boolean getIsBuy() {
        return isBuy;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setLast(Double last) {
        this.last = last;
    }

    public void setPercentageChange(Double percentageChange) {
        this.percentageChange = percentageChange;
    }
    public void setOwnedQuantity(Integer ownedQuantity) { this.ownedQuantity = ownedQuantity; }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setIsBuy(Boolean isBuy) {
        this.isBuy = isBuy;
    }
}
