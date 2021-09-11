package idv.example.goodposture.user.my;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import idv.example.goodposture.R;
import idv.example.goodposture.user.shopping.Product;

public class MyOrderDetailFragment extends Fragment {
    private static final String TAG = "TAG_MyOrderDetailFragment";
    private AppCompatActivity activity;
    //資料
    private Order order;     //從MyOrderStateFragment傳來的Order物件
    private List<OrderDetail> orderDetails;
    private List<Product> products;
    //元件
    private Toolbar toolbar;
    private TextView tvOrderId;
    private TextView tvOrderDate;
    private TextView tvOrderAmount;
    private TextView tvOrderState;
    private TextView tvOrderCancel;
    private Button btOrderDone;
    private RecyclerView rvOrderDetail;
    //firebase
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate ");
        setHasOptionsMenu(true);
        activity = (AppCompatActivity) getActivity();
        order = (Order) (getArguments() != null ? getArguments().getSerializable("order") : null);
        db = FirebaseFirestore.getInstance();
        orderDetails = new ArrayList<>();
        products = new ArrayList<>();
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
        showOrderData();
        showOrderDetails();
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause  ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop  ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy ");
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

    private void showOrderData() {
        //將訂單顯示在畫面上
        tvOrderId.setText("訂單編號：" + order.getId());
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
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
            order.setOrderState(Order.ORDER_STATE_RECEIVED);
        });
    }

    private void  showOrderDetails() {
        db.collection("orderDetail")
                .whereEqualTo("orderId", order.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            OrderDetail orderDetail = document.toObject(OrderDetail.class);
                            orderDetails.add(orderDetail);
                        }
                        //jfoijsdoijsoijfoidsjfoisjfods
//                        for(OrderDetail orderDetail : orderDetails){
//                            setProducts(orderDetail.getProductId());
//                        }
                        handleRecyclerView();

                        //setsProducts
                    }else{
                        String message = task.getException() == null ?
                                "No Product found" :
                                task.getException().getMessage();
                        Log.e(TAG, "exception message: " + message);
                    }
                });
    }

//
//    //jdsoifjsoijfoidsjfoids
//    //考慮要不要存商品資訊
//    private void setProducts(String productId) {
//        db.collection("product")
//                .document(productId)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        DocumentSnapshot documentSnapshot = task.getResult();
//                        products.add(documentSnapshot.toObject(Product.class));
//                    }else{
//                        String message = task.getException() == null ?
//                                "No product found" :
//                                task.getException().getMessage();
//                        Log.e(TAG, "exception message: " + message);
//                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//    }


    private void handleRecyclerView() {
        rvOrderDetail.setAdapter(new MyOrderDetailRVAdapter(getContext(), orderDetails));
        Log.d(TAG,"orderDetails 的大小：" + orderDetails.size());
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
            holder.tvProductNumber.setText("x" + orderDetail.getProductNumber());
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

    }


//    //假資料
//    private List<OrderDetail> getProductList() {
//        List<OrderDetail> orderDetailList = new ArrayList<>();
//        for(int i =0;i < 10;i++){
//            orderDetailList.add(new OrderDetail());
//        }
//        return orderDetailList;
//    }
}