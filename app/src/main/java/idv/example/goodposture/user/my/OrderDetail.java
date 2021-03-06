package idv.example.goodposture.user.my;

public class OrderDetail {
    private String id;
    private String orderId;
    private String productId;
    private String productName;
    private String productPicturePath;
    private int productNumber;
    private double productPrice;   //不是商品現在的售價，是當初購買的價錢

    public OrderDetail() {
    }

    public OrderDetail(String id, String orderId, String productId, String productName, String productPicturePath, int productNumber, double productPrice) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.productPicturePath = productPicturePath;
        this.productNumber = productNumber;
        this.productPrice = productPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPicturePath() {
        return productPicturePath;
    }

    public void setProductPicturePath(String productPicturePath) {
        this.productPicturePath = productPicturePath;
    }

    public int getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(int productNumber) {
        this.productNumber = productNumber;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }
}
