package idv.example.goodposture.user.shopping;

import java.io.Serializable;
import java.util.Date;

public class Product implements Serializable {
    private String id;    //商品編號
    private String name;     //商品名稱
    private String description;    //商品描述
    private double price;   //目前價錢
    private int stock;   //庫存
    private Date date;     //上架日期
    private String picturePath;  //商品照片路徑
    private int type;     //商品類型    //  todo:FOOD:1, EQUIPMENT:2
    private int sellAmount;   //商品賣出數量    ->可考慮轉成Number應該還行

    public Product() {
    }

    public Product(String id, String name, String description, double price, int stock, Date date, String picturePath, int type, int sellAmount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.date = date;
        this.picturePath = picturePath;
        this.type = type;
        this.sellAmount = sellAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSellAmount() {
        return sellAmount;
    }

    public void setSellAmount(int sellAmount) {
        this.sellAmount = sellAmount;
    }
}

