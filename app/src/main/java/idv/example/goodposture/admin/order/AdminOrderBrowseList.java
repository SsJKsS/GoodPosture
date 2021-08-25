package idv.example.goodposture.admin.order;

public class AdminOrderBrowseList {
    private String order;
    private String date;
    private String status;
    private String price;

    public AdminOrderBrowseList() {
    }

    public AdminOrderBrowseList(String order, String date, String status, String price) {
        this.order = order;
        this.date = date;
        this.status = status;
        this.price = price;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
