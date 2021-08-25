package idv.example.goodposture.user.shopping;

import java.io.Serializable;
import java.util.Date;

public class Product implements Serializable {
    private Integer imageId;    //假資料抓imageId
    //private int productId;
    private String productName;
    //private String productDescription;
    private int productPrice;
    //private Date productDate;
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
