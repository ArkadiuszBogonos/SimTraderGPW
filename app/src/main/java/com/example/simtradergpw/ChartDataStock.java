package com.example.simtradergpw;

import com.github.mikephil.charting.interfaces.datasets.IDataSet;

public class ChartDataStock {

    private String ticker;
    private Double price;
    private String date;

    public ChartDataStock(Double price, String date) {
        this.ticker = null;
        this.price = price;
        this.date = date;
    }

    public ChartDataStock(String ticker, Double price) {
        this.ticker = ticker;
        this.price = price;
        this.date = null;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
