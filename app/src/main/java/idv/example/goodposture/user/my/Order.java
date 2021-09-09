package idv.example.goodposture.user.my;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

public class Order implements Serializable {
    private String id;
    private String uid;
    private Date orderTime;
    private int orderState;
    static final int ORDER_STATE_READY = 1;    //待出貨
    static final int ORDER_STATE_SHIPPED = 2;  //已出貨
    static final int ORDER_STATE_RECEIVED = 3;  //已完成
    static final int ORDER_STATE_CANCEL = 4;   //已取消
    private String receiverName;
    private String receiverPhone;
    private String cancel;
    private double orderAmount;

    public Order() {
    }

    public Order(String id, String uid, Date orderTime, int orderState, String receiverName, String receiverPhone, String cancel, double orderAmount) {
        this.id = id;
        this.uid = uid;
        this.orderTime = orderTime;
        this.orderState = orderState;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.cancel = cancel;
        this.orderAmount = orderAmount;
    }

    public Order(int orderState, int order_amount){
        this(new Date(), orderState, order_amount);
    }

    //做測試假資料而開的建構子
    public Order(Date orderTime, int orderState, int orderAmount) {
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //sdf.format(orderTime) ->會轉成string
        this.orderTime = orderTime;
        this.orderState = orderState;
        this.orderAmount = orderAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public int getOrderState() {
        return orderState;
    }

    public void setOrderState(int orderState) {
        this.orderState = orderState;
    }

    public static int getOrderStateReady() {
        return ORDER_STATE_READY;
    }

    public static int getOrderStateShipped() {
        return ORDER_STATE_SHIPPED;
    }

    public static int getOrderStateReceived() {
        return ORDER_STATE_RECEIVED;
    }

    public static int getOrderStateCancel() {
        return ORDER_STATE_CANCEL;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getCancel() {
        return cancel;
    }

    public void setCancel(String cancel) {
        this.cancel = cancel;
    }

    public double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public static String getOrderStateName(int orderState) {
        switch (orderState){
            case ORDER_STATE_READY:
                return "待出貨";
            case ORDER_STATE_SHIPPED:
                return "已出貨";
            case ORDER_STATE_RECEIVED:
                return "已完成";
            case ORDER_STATE_CANCEL:
                return "已取消";
            default:
                return "無此訂單狀態";
        }
    }
}
