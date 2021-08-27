package idv.example.goodposture.user.shopping;

import java.io.Serializable;
import java.util.Date;

public class Product implements Serializable {
    private Integer imageId;    //假資料抓imageId

    //private int productId;    //商品編號
    private String productName;     //商品名稱
    //private String productDescription;    //商品描述
    private int productPrice;   //目前價錢
    //private int productStock;   //庫存
    //private Date productDate;     //上架日期
    //private String productPicture;
    //private int productType;

    public Product() {
    }

    public Product(Integer imageId, String productName, int productPrice) {
        this.imageId = imageId;
        this.productName = productName;
        this.productPrice = productPrice;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }
}

