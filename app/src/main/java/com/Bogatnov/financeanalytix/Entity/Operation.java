package com.Bogatnov.financeanalytix.Entity;

import java.util.Date;

public class Operation {
    private Integer transactionid;
    private Date date;
    private String direction;
    private Category category;
    private double amount;

    public Operation() {}

    public Operation(Integer nTransactionid,
        Date ndate,
        String ndirection,
        Category ncategory,
        double namount){

        this.transactionid = nTransactionid;
        this.date = ndate;
        this.direction = ndirection;
        this.category = ncategory;
        this.amount = namount;
    }

    public Integer getTransactionid() {
        return transactionid;
    }

    public void setTransactionid(Integer transactionid) {
        this.transactionid = transactionid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
