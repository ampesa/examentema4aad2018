package com.apps.apene.quicktrade.model;

import java.io.Serializable;

public class Product implements Serializable {

    // Atributos del Product
    private String title;
    private String description;
    private String category;
    private String image;
    private String price;
    private String country;
    private String zip;
    private String sellerUID;
    private String time;
    private String key;


    // Constructores de Product
    // Constructor vacío
    public Product () {
    }

    // Constructor para AddProduct
    public Product (String title, String description, String category, String image, String  price, String country, String zip,
                    String sellerUID, String time) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.image = image;
        this.price = price;
        this.country = country;
        this.zip = zip;
        this.sellerUID = sellerUID;
        this.time = time;
    }

    // Constructor para añadir la clave
    public Product (String title, String description, String category, String image, String  price, String country, String zip,
                    String sellerUID, String time, String key) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.image = image;
        this.price = price;
        this.country = country;
        this.zip = zip;
        this.sellerUID = sellerUID;
        this.time = time;
        this.key = key;
    }

    // Getters y Setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getSellerUID() {
        return sellerUID;
    }

    public void setSellerUID(String sellerUID) {
        this.sellerUID = sellerUID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    // Método toString()
    @Override
    public String toString() {
        return "Product{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price='" + price + '\'' +
                ", country='" + country + '\'' +
                ", zip='" + zip + '\'' +
                ", sellerUID='" + sellerUID + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
