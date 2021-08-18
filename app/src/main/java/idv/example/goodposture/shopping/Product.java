package idv.example.goodposture.shopping;

public class Product {
    private Integer imageId;
    private String productName;

    public Product() {
    }

    public Product(Integer imageId, String productName) {
        this.imageId = imageId;
        this.productName = productName;
    }

    public  Integer getImageId() {
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
}

