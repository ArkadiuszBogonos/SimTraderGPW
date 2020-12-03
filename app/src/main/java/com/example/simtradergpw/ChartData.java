package com.example.simtradergpw;

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
