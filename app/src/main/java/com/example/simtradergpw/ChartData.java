package com.example.simtradergpw;

import com.github.mikephil.charting.interfaces.datasets.IDataSet;

public class ChartData {
    private Double price;
    private String date;

    public ChartData(Double price, String date) {
        this.price = price;
        this.date = date;
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
