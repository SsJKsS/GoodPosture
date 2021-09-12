package idv.example.goodposture.user.my;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    private FirebaseStorage storage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate ");
        setHasOptionsMenu(true);
        activity = (AppCompatActivity) getActivity();
        order = (Order) (getArguments() != null ? getArguments().getSerializable("order") : null);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
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
        //Log.d(TAG, "onStart ");
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.d(TAG, "onResume ");
    }

    @Override
    public void onPause() {
        super.onPause();
        //Log.d(TAG, "onPause  ");
    }

    @Override
    public void onStop() {
        super.onStop();
        //Log.d(TAG, "onStop  ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Log.d(TAG, "onDestroyView ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d(TAG, "onDestroy ");
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
        toolbar.setTitle("訂單內容");
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        tvOrderDate.setText("訂購日期：" + sdf.format(order.getOrderTime()));
        tvOrderAmount.setText("訂購總額：$" + order.getOrderAmount());
        tvOrderState.setText("訂單狀態：" + order.getOrderStateName(order.getOrderState()));
        //訂單狀態是"已取消"顯示取消原因
        if (order.getOrderState() == Order.ORDER_STATE_CANCEL) {
            tvOrderCancel.setVisibility(View.VISIBLE);
            tvOrderCancel.setText("取消原因：" + order.getCancel());
        } else {
            tvOrderCancel.setVisibility(View.GONE);
        }
        //訂單狀態為"已配送"顯示完成訂單按鈕
        if (order.getOrderState() == Order.ORDER_STATE_SHIPPED) {
            btOrderDone.setVisibility(View.VISIBLE);
        } else {
            btOrderDone.setVisibility(View.GONE);
        }
        //點擊完成訂單按鈕
        btOrderDone.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("確定收貨?")
                    .setPositiveButton("確認", (dialog, which) -> {
                        order.setOrderState(Order.ORDER_STATE_RECEIVED);
                        //更新訂單資料庫
                        updateOrderState();
                        //更新Product資料庫-訂單完成，增加賣出數量
                        updateProductSellAmount();
                        Navigation.findNavController(btOrderDone).popBackStack();
                        //tvOrderState.setText("訂單狀態：" + order.getOrderStateName(order.getOrderState()));
                        //btOrderDone.setVisibility(View.GONE);
                    })
                    .setNegativeButton("取消", (dialog, which) -> {
                    })
                    .show();
        });
    }

    private void updateOrderState() {
        db.collection("order").document(order.getId()).set(order)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String message = "Order state is updated"
                                + " with ID: " + order.getId();
                        Log.d(TAG, message);
                        //Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        // 新增完畢回訂單列表

                    } else {
                        String message = task.getException() == null ?
                                "Update failed" :
                                task.getException().getMessage();
                        Log.e(TAG, "message: " + message);
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    //更新Product資料庫，然後更新商品的賣出數量
    private void updateProductSellAmount() {
        for (OrderDetail orderDetail : orderDetails) {
            db.collection("product").document(orderDetail.getProductId()).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            Product product = document.toObject(Product.class);
                            //Log.d(TAG, product.getId());
                            products.add(product);
                            //Log.d(TAG,"products 在getProducts內的大小："+products.size());
                        } else {
                            String message = task.getException() == null ?
                                    "No Product found" :
                                    task.getException().getMessage();
                            Log.e(TAG, "exception message: " + message);
                        }
                        //抓完商品資料後，去更改products的sellAmount
                        updateProductsSellAmount();
                    });

        }
    }

    //修正products清單，然後上傳到資料庫
    private void updateProductsSellAmount() {
        //更改每個product的sellAmount
        for (Product product : products) {
            for (OrderDetail orderDetail : orderDetails) {
                if (product.getId().equals(orderDetail.getProductId())) {
                    product.setSellAmount(product.getSellAmount() + orderDetail.getProductNumber());
                    break;
                }
            }
        }
        updateProduct();
    }

    //更新Product資料庫
    private void updateProduct() {
        for(Product product : products){
            db.collection("product").document(product.getId()).set(product)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String message = "Product is updated"
                                    + " with ID: " + product.getId();
                            Log.d(TAG, message);
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        } else {
                            String message = task.getException() == null ?
                                   "Insert failed" :
                                    task.getException().getMessage();
                            Log.e(TAG, "message: " + message);
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showOrderDetails() {
        db.collection("orderDetail")
                .whereEqualTo("orderId", order.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            OrderDetail orderDetail = document.toObject(OrderDetail.class);
                            orderDetails.add(orderDetail);
                        }
                        handleRecyclerView();
                    } else {
                        String message = task.getException() == null ?
                                "No orderDetail found" :
                                task.getException().getMessage();
                        Log.e(TAG, "exception message: " + message);
                    }
                });
    }

    private void handleRecyclerView() {
        rvOrderDetail.setAdapter(new MyOrderDetailRVAdapter(getContext(), orderDetails));
        rvOrderDetail.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private class MyOrderDetailRVAdapter extends RecyclerView.Adapter<MyOrderDetailRVAdapter.MyOderDetailViewHolder> {

        private Context context;

        private List<OrderDetail> list;

        public MyOrderDetailRVAdapter(Context context, List<OrderDetail> list) {
            this.context = context;
            this.list = list;
        }

        private class MyOderDetailViewHolder extends RecyclerView.ViewHolder {
            ImageView ivProduct;
            TextView tvProductName;
            TextView tvProductPrice;
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
            if (orderDetail.getProductPicturePath() == null) {
                holder.ivProduct.setImageResource(R.drawable.no_product_image);
            } else {
                showImage(holder.ivProduct, orderDetail.getProductPicturePath());
            }
            holder.tvProductName.setText(orderDetail.getProductName());
            holder.tvProductPrice.setText("$" + orderDetail.getProductPrice());
            holder.tvProductNumber.setText("x" + orderDetail.getProductNumber());
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

    }

    // 下載Firebase storage的照片並顯示在ivProduct上
    private void showImage(final ImageView imageView, final String path) {
        final int ONE_MEGABYTE = 1024 * 1024;
        StorageReference imageRef = storage.getReference().child(path);
        //byte[]轉成bitmap，貼上bitmap
        imageRef.getBytes(ONE_MEGABYTE)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        byte[] bytes = task.getResult();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageView.setImageBitmap(bitmap);
                    } else {
                        String message = task.getException() == null ?
                                "Image download Failed" + ": " + path :
                                task.getException().getMessage() + ": " + path;
                        Log.e(TAG, message);
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });

    }
}