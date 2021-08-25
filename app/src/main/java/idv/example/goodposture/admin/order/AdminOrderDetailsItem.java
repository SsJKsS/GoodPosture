package idv.example.goodposture.admin.order;

public class AdminOrderDetailsItem {
    private Integer image;
    private String product;
    private String context;
    private String price;

    public AdminOrderDetailsItem() {
    }

    public AdminOrderDetailsItem(Integer image, String product, String context, String price) {
        this.image = image;
        this.product = product;
        this.context = context;
        this.price = price;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
