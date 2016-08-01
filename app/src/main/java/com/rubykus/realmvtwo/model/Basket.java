package com.rubykus.realmvtwo.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Rubykus on 31.07.2016.
 */
public class Basket extends RealmObject {
    @PrimaryKey
    private int idGood;
    private String name;
    private int count;
    private double price;
    private double sum;
    private String img;

    public int getIdGood() {
        return idGood;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setIdGood(int idGood) {
        this.idGood = idGood;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
