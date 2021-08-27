package idv.example.goodposture.user.my;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import idv.example.goodposture.R;
import idv.example.goodposture.user.shopping.Product;
import idv.example.goodposture.user.shopping.ShoppingOrderFragment;

public class MyOrderDetailFragment extends Fragment {
    private static final String TAG = "MyOrderDetailFragment";
    private AppCompatActivity activity;
    private Order order;     //從MyOrderStateFragment傳來的Order物件

    private Toolbar toolbar;
    private TextView tvOrderId;
    private TextView tvOrderDate;
    private TextView tvOrderAmount;
    private TextView tvOrderState;
    private TextView tvOrderCancel;
    private Button btOrderDone;
    private RecyclerView rvOrderDetail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        setHasOptionsMenu(true);
        order = (Order) (getArguments() != null ? getArguments().getSerializable("order") : null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_order_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);    //取得元件的參考
        handleToolbar();
        showOrderData(order);
        handleRecyclerView();
    }

    private void findViews(View view) {
        toolbar = view.findViewById(R.id.tb_my_order);
        tvOrderId = view.findViewById(R.id.tv_order_id);
        tvOrderDate = view.findViewById(R.id.tv_order_date);
        tvOrderAmount = view.findViewById(R.id.tv_order_amount);
        tvOrderState = view.findViewById(R.id.tv_order_state);
        tvOrderCancel = view.findViewById(R.id.tv_order_cancel);
        btOrderDone = view.findViewById(R.id.bt_order_done);
        rvOrderDetail = view.findViewById(R.id.rv_my_order_detail);
    }

    private void handleToolbar() {
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle("訂單詳情");
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Navigation.findNavController(toolbar).popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showOrderData(Order order) {
        //還沒定義訂單編號所以先不顯示
        tvOrderId.setText("訂單編號：" + "order.getOrderId()");
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        tvOrderDate.setText("訂購日期：" + sdf.format(order.getOrderTime()));
        tvOrderAmount.setText("訂購總額：$" + order.getOrderAmount());
        tvOrderState.setText("訂單狀態：" + order.getOrderStateName(order.getOrderState()));
        //訂單狀態是"已取消"顯示取消原因
        if(order.getOrderState() == Order.ORDER_STATE_CANCEL){
            tvOrderCancel.setVisibility(View.VISIBLE);
            tvOrderCancel.setText("取消原因：" + "order.getCancel()");
        }else{
            tvOrderCancel.setVisibility(View.GONE);
        }
        //訂單狀態為"已配送"顯示完成訂單按鈕
        if(order.getOrderState() == Order.ORDER_STATE_SHIPPED){
            btOrderDone.setVisibility(View.VISIBLE);
        }else{
            btOrderDone.setVisibility(View.GONE);
        }
        //點擊完成訂單按鈕
        btOrderDone.setOnClickListener(v -> {
            order.setOrderState(Order.ORDER_STATE_RECEIVED);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("確定收貨?")
                    .setPositiveButton("確認", (dialog, which) -> {
                        tvOrderState.setText("訂單狀態：" + order.getOrderStateName(order.getOrderState()));
                        btOrderDone.setVisibility(View.GONE);
                    })
                    .setNegativeButton("取消", (dialog, which) -> { })
                    .show();
        });
        //TODO 要再想一下資料庫更新資料的順序
    }

    private void handleRecyclerView() {
        rvOrderDetail.setAdapter(new MyOrderDetailRVAdapter(getContext(), getProductList()));
        rvOrderDetail.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private static class MyOrderDetailRVAdapter extends RecyclerView.Adapter<MyOrderDetailRVAdapter.MyOderDetailViewHolder>{

        private Context context;
        private List<OrderDetail> list;

        public MyOrderDetailRVAdapter(Context context, List<OrderDetail> list) {
            this.context = context;
            this.list = list;
        }
        private static class MyOderDetailViewHolder extends RecyclerView.ViewHolder {
            ImageView ivProduct;    //Thought:從OrderDetail物件的productId去資料庫抓product圖片
            TextView tvProductName;     //上
            TextView tvProductPrice;    //上
            TextView tvProductNumber;

            public MyOderDetailViewHolder(@NonNull View itemView) {
                super(itemView);
                ivProduct = itemView.findViewById(R.id.iv_product);
                tvProductName = itemView.findViewById(R.id.tv_product_name);
                tvProductPrice = itemView.findViewById(R.id.tv_product_price);
                tvProductNumber = itemView.findViewById(R.id.tv_product_number);
            }
        }

        @Override
        public MyOrderDetailRVAdapter.MyOderDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_shopping_order, parent, false);
            return new MyOrderDetailRVAdapter.MyOderDetailViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyOrderDetailRVAdapter.MyOderDetailViewHolder holder, int position) {
            OrderDetail orderDetail = list.get(position);
            //Product product = new Product(orderDetail.getProductId());
            holder.ivProduct.setImageResource(R.drawable.shopping_cat3);
            holder.tvProductName.setText("product.getProductName()");
            holder.tvProductPrice.setText("$" + orderDetail.getProductPrice());
            holder.tvProductNumber.setText("x" + orderDetail.getProductPrice());
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }
    }

    //假資料
    private List<OrderDetail> getProductList() {
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for(int i =0;i < 10;i++){
            orderDetailList.add(new OrderDetail());
        }
        return orderDetailList;
    }

}