package com.example.simtradergpw;

import java.util.Comparator;

public class UserRecord implements Comparable<UserRecord>{

    private Integer userId;
    private String login;
    private Double money;
    private Double ownedStocksValue;
    private Double loans;

    public UserRecord(Integer userId, String login, Double money, Double loans) {
        this.userId = userId;
        this.login = login;
        this.money = money;
        this.loans = loans;
        this.ownedStocksValue = null;
    }

    public UserRecord(Integer userId, String login, Double money, Double loans, Double ownedStocksValue) {
        this.userId = userId;
        this.login = login;
        this.money = money;
        this.loans = loans;
        this.ownedStocksValue = ownedStocksValue;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public Double getOwnedStocksValue() {
        return ownedStocksValue;
    }

    public void setOwnedStocksValue(Double ownedStocksValue) {
        this.ownedStocksValue = ownedStocksValue;
    }

    public Double getLoans() {
        return loans;
    }

    public void setLoans(Double loans) {
        this.loans = loans;
    }

    @Override
    public int compareTo(UserRecord o) {
        Double o1WalletValue = this.getMoney() + this.getOwnedStocksValue() - this.getLoans();
        Double o2WalletValue = o.getMoney() + o.getOwnedStocksValue() - o.getLoans();

        if (o1WalletValue > o2WalletValue) return -1;
        else if (o1WalletValue.equals(o2WalletValue)) return 0;
        else return 1;

    }
}
