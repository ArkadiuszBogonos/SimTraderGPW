package com.example.simtradergpw;

public class StockRecord {
    private String name;
    private String ticker;
    private String last;
    private String percentageChange;
    private Integer ownedQuantity;



    public StockRecord(String name, String ticker, String last, String percentageChange) {
        this.name = name;
        this.ticker = ticker;
        this.last = last;
        this.percentageChange = percentageChange;
        this.ownedQuantity = null;
    }

    public StockRecord(String name, String ticker, String last, String percentageChange, Integer ownedQuantity) {
        this.name = name;
        this.ticker = ticker;
        this.last = last;
        this.percentageChange = percentageChange;
        this.ownedQuantity = ownedQuantity;
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

    public Integer getOwnedQuantity() { return ownedQuantity; }

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
    public void setOwnedQuantity(Integer ownedQuantity) { this.ownedQuantity = ownedQuantity; }
}
