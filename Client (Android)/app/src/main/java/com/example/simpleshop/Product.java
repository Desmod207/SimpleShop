package com.example.simpleshop;

import androidx.annotation.NonNull;


public class Product {

    private int     id;
    private int     price;
    private String  name;
    private String  description;
    private String  imageName;
    private String  section;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getImageName() {
        return imageName;
    }

    public String getSection() {
        return section;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setSection(String section) {
        this.section = section;
    }

    @NonNull
    public String toString() {
        return name;
    }
}