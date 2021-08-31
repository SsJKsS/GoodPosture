package idv.example.goodposture.admin.commodity;

public class AdminCommodityList {
    private Integer iv_com;
    private String tv_com_name;
    private String tv_com_price;
    private String tv_com_goods;
    private String tv_com_sold;

    public AdminCommodityList() {
    }

    public AdminCommodityList(Integer iv_com, String tv_com_name, String tv_com_price, String tv_com_goods, String tv_com_sold) {
        this.iv_com = iv_com;
        this.tv_com_name = tv_com_name;
        this.tv_com_price = tv_com_price;
        this.tv_com_goods = tv_com_goods;
        this.tv_com_sold = tv_com_sold;
    }

    public Integer getIv_com() {
        return iv_com;
    }

    public void setIv_com(Integer iv_com) {
        this.iv_com = iv_com;
    }

    public String getTv_com_name() {
        return tv_com_name;
    }

    public void setTv_com_name(String tv_com_name) {
        this.tv_com_name = tv_com_name;
    }

    public String getTv_com_price() {
        return tv_com_price;
    }

    public void setTv_com_price(String tv_com_price) {
        this.tv_com_price = tv_com_price;
    }

    public String getTv_com_goods() {
        return tv_com_goods;
    }

    public void setTv_com_goods(String tv_com_goods) {
        this.tv_com_goods = tv_com_goods;
    }

    public String getTv_com_sold() {
        return tv_com_sold;
    }

    public void setTv_com_sold(String tv_com_sold) {
        this.tv_com_sold = tv_com_sold;
    }
}
