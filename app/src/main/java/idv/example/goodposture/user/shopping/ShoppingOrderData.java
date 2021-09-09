package idv.example.goodposture.user.shopping;

import java.util.List;


public class ShoppingOrderData {
    public static List<Product> orderProductListFromCart;

    public static List<Product> getOrderProductList() {
        return orderProductListFromCart;
    }

    public static void setOrderProductList(List<Product> orderProductList) {
        ShoppingOrderData.orderProductListFromCart = orderProductList;
    }
}
