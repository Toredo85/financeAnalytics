package com.Bogatnov.financeanalytix.Entity;

public class Budget {
    private Integer id;
    private String date;
    private String direction;
    private Category category;
    private double amount;

    public Budget() {}

    public Budget(Integer id,
                  String ndate,
                  String ndirection,
                  Category ncategory,
                  double namount){

        this.id = id;
        this.date = ndate;
        this.direction = ndirection;
        this.category = ncategory;
        this.amount = namount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer transactionid) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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
