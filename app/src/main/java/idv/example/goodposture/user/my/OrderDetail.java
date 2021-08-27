package idv.example.goodposture.user.my;

public class OrderDetail {
    private int orderDetailId;
    //private int orderId;
    private int productId;
    private int productNumber;
    private int productPrice;   //不是商品現在的售價，是當初購買的價錢

    public OrderDetail() {
        this.orderDetailId = 1234;
        this.productId = 1234;
        this.productNumber = 10;
        this.productPrice = 100;
    }

    public OrderDetail(int orderDetailId, int productId, int productNumber, int productPrice) {
        this.orderDetailId = orderDetailId;
        this.productId = productId;
        this.productNumber = productNumber;
        this.productPrice = productPrice;
    }

    public int getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(int orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(int productNumber) {
        this.productNumber = productNumber;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }
}
