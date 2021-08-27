package idv.example.goodposture.user.my;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

public class Order implements Serializable {
    private int orderId;
    private int memberId;
    private Date orderTime;
    private int orderState;
    static final int ORDER_STATE_READY = 1;    //待出貨
    static final int ORDER_STATE_SHIPPED = 2;  //已出貨
    static final int ORDER_STATE_RECEIVED = 3;  //已完成
    static final int ORDER_STATE_CANCEL = 4;   //已取消
    private String receiverName;
    private int receiverPhone;
    private String cancel;
    private int orderAmount;

    public Order() {
    }

    public Order(int orderId,
                 int memberId,
                 Date orderTime,
                 int orderState,
                 String receiverName,
                 int receiverPhone,
                 String cancel,
                 int orderAmount) {
        this.orderId = orderId;
        this.memberId = memberId;
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

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
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

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public int getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(int receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getCancel() {
        return cancel;
    }

    public void setCancel(String cancel) {
        this.cancel = cancel;
    }

    public int getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(int orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getOrderStateName(int orderState) {
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
