package idv.example.goodposture.user.shopping;

public class CartDetail {
    private String id;
    private String uid;
    private String productId;
    private double productPrice;
    private int productAmount;

    public CartDetail() {
    }

    public CartDetail(String id, String uid, String productId, double productPrice, int productAmount) {
        this.id = id;
        this.uid = uid;
        this.productId = productId;
        this.productPrice = productPrice;
        this.productAmount = productAmount;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductAmount() {
        return productAmount;
    }

    public void setProductAmount(int productAmount) {
        this.productAmount = productAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
