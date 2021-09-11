package idv.example.goodposture.admin.order;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import idv.example.goodposture.R;
import idv.example.goodposture.user.my.MyOrderDetailFragment;
import idv.example.goodposture.user.my.Order;
import idv.example.goodposture.user.my.OrderDetail;
import idv.example.goodposture.user.shopping.Product;

public class AdminOrderDetailsFragment extends Fragment {
    private static final String TAG = "TAG_AdminOrderDetailsFragment";
    private AppCompatActivity activity;
    private Order order;
    private OrderDetail orderDetail;
    private Product product;
    private FirebaseFirestore db;
    private ListenerRegistration registration;
    private FirebaseAuth auth;
    private List<OrderDetail> orderDetails;
    private List<Product> products;
    private FirebaseStorage storage;
    private int total = 0;

    private TextView tv_admin_order_detail_id;
    private TextView tv_admin_order_detail_date;
    private TextView tv_admin_order_detail_amount;
    private TextView tv_admin_order_detail_state;
    private TextView tv_admin_order_detail_receiver;
    private TextView tv_admin_order_detail_phone;
    private RecyclerView rv_admin_order_details;
    private Button bt_OK;
    private Button bt_Cancel;
    private ImageView iv_order_back;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        order = (Order) (getArguments() != null ? getArguments().getSerializable("order") : null);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        orderDetails = new ArrayList<>();
        products = new ArrayList<>();

        Log.d(TAG,"onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_order_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG,"onViewCreated");
        findViews(view);
        showOrderData(order);
        handleRecyclerView();
        handleOrderBack();
        handleBtCancel();
        handleBtOK();

    }

    private void findViews(View view) {
        tv_admin_order_detail_id = view.findViewById(R.id.tv_admin_order_detail_id);
        tv_admin_order_detail_date = view.findViewById(R.id.tv_admin_order_detail_date);
        tv_admin_order_detail_amount = view.findViewById(R.id.tv_admin_order_detail_amount);
        tv_admin_order_detail_state = view.findViewById(R.id.tv_admin_order_detail_state);
        tv_admin_order_detail_receiver = view.findViewById(R.id.tv_admin_order_detail_receiver);
        tv_admin_order_detail_phone = view.findViewById(R.id.tv_admin_order_detail_phone);
        iv_order_back = view.findViewById(R.id.iv_order_back);
        rv_admin_order_details = view.findViewById(R.id.rv_admin_order_details);
        bt_Cancel = view.findViewById(R.id.bt_Cancel);
        bt_OK = view.findViewById(R.id.bt_OK);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
        reloadOrderDetail();
//        reloadProduct();
    }

    private void reloadOrderDetail() {

        Log.d(TAG,"orderId : " + order.getId());

        db.collection("orderDetail")
                .whereEqualTo("orderId", order.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            OrderDetail orderDetail = document.toObject(OrderDetail.class);
                            orderDetails.add(orderDetail);
                            Log.d(TAG,"orderDetail.getProductId : " + orderDetail.getProductId());
                        }
//                        reloadProduct(orderDetails);
                        handleRecyclerView();
                    }else{
                        String message = task.getException() == null ?
                                "No orderDetail found" :
                                task.getException().getMessage();
                        Log.e(TAG, "exception message: " + message);
                    }
                });
    }

    private void reloadProduct(List<OrderDetail> list) {

//        db.collection("product").whereEqualTo("id",orderDetail.getProductId()).get();
        String product_id = list.get(0).getProductId();
        int product_number = list.get(0).getProductNumber();
        db.collection("product")
                .document(product_id).get().addOnCompleteListener(task -> {
           if (task.isSuccessful() && task.getResult() != null){
               DocumentSnapshot documentSnapshot = task.getResult();
               product = documentSnapshot.toObject(Product.class);
               total = product_number + product.getStock();
               product.setStock(total);
               db.collection("product").document(product_id).set(product);
           }
        });

    }


    private void showOrderData(Order order) {
        tv_admin_order_detail_id.setText(order.getId());
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        tv_admin_order_detail_date.setText(sdf.format(order.getOrderTime()));
        tv_admin_order_detail_amount.setText("$" + order.getOrderAmount());
        tv_admin_order_detail_state.setText(order.getOrderStateName(order.getOrderState()));
        tv_admin_order_detail_receiver.setText(order.getReceiverName());
        tv_admin_order_detail_phone.setText(order.getReceiverPhone());


        //TODO 要再想一下資料庫更新資料的順序
    }

    private void handleBtCancel() {
        db.collection("order").document(order.getId()).set(order).addOnCompleteListener(task -> {});

        if (order.getOrderState() == Order.ORDER_STATE_READY){
            bt_Cancel.setOnClickListener(view->{
                NewDialog();
            });
        } else {
            bt_Cancel.setVisibility(View.GONE);
        }
    }

    private void handleBtOK() {
        if(order.getOrderState() == Order.ORDER_STATE_READY){
            bt_OK.setOnClickListener(view ->{
                AlertDialog.Builder dia = new AlertDialog.Builder(activity);
                dia.setMessage("確認送出?")
                        .setPositiveButton("確認", ((dialog, which) -> {
                            tv_admin_order_detail_state.setText(order.getOrderStateName(order.getOrderState()));
                            order.setOrderState(Order.ORDER_STATE_SHIPPED);
                            db.collection("order").document(order.getId()).set(order).addOnCompleteListener(task -> {});
                            // 取得NavController物件
                            NavController navController = Navigation.findNavController(view);
                            // 跳至頁面
                            navController.navigate(R.id.action_adminOrderDetailsFragment_to_adminOrderFragment);
                        }))
                        .setNegativeButton("取消", (dialog, which) -> { })
                        .show();

                Log.d(TAG,"order.getOrderState : "+ order.getOrderState());
            });
        }else {
            bt_OK.setVisibility(View.GONE);
        }
    }

    private void handleOrderBack() {
        iv_order_back.setOnClickListener(view ->{
            Navigation.findNavController(iv_order_back).popBackStack();
        });
    }

    private void NewDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        EditText editText = new EditText(getActivity());
        alertDialog.setTitle("請輸入原因");
        alertDialog.setView(editText);
        alertDialog.setNegativeButton("送出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String reason = editText.getText().toString().trim();
                if (reason.isEmpty()){
                    editText.setError("請輸入原因");
                }else {
//                    Log.d(TAG,"reason : " + reason);
                    order.setCancel(reason);
//                    Log.d(TAG,"order.getCancel() : " + order.getCancel());
                    db.collection("orderDetail")
                            .whereEqualTo("orderId", order.getId())
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        OrderDetail orderDetail = document.toObject(OrderDetail.class);
                                        orderDetails.add(orderDetail);
                                        Log.d(TAG,"orderDetail.getProductId : " + orderDetail.getProductId());
                                    }
                                    reloadProduct(orderDetails);
                                    handleRecyclerView();
                                }else{
                                    String message = task.getException() == null ?
                                            "No orderDetail found" :
                                            task.getException().getMessage();
                                    Log.e(TAG, "exception message: " + message);
                                }
                            });

                }
                order.setOrderState(Order.ORDER_STATE_CANCEL);
//                Log.d(TAG,"order.getOrderState : "+ order.getOrderState());

                db.collection("order").document(order.getId()).set(order).addOnCompleteListener(task -> {});


                Toast.makeText(getActivity().getBaseContext(), "已送出", Toast.LENGTH_SHORT).show();
                // 取得NavController物件
                NavController navController = Navigation.findNavController(bt_OK);
                // 跳至頁面
                navController.navigate(R.id.action_adminOrderDetailsFragment_to_adminOrderFragment);
            }
        });
        alertDialog.show();
    }

    private void handleRecyclerView() {
        AdminOrderDetailRVAdapter adapter = (AdminOrderDetailRVAdapter) rv_admin_order_details.getAdapter();
        if (adapter == null){
            adapter = new AdminOrderDetailRVAdapter();
            rv_admin_order_details.setAdapter(adapter);
        }
        rv_admin_order_details.setLayoutManager(new LinearLayoutManager(activity));
        adapter.setAdminOrderDetailList(orderDetails);
    }

    private class AdminOrderDetailRVAdapter extends RecyclerView.Adapter<AdminOrderDetailRVAdapter.AdminOderDetailViewHolder>{
        private List<OrderDetail> list;
        AdminOrderDetailRVAdapter(){
        }

        public void setAdminOrderDetailList(List<OrderDetail> list){
            this.list = list;
            notifyDataSetChanged();
        }

        class AdminOderDetailViewHolder extends RecyclerView.ViewHolder {
            ImageView iv_order_product;    //Thought:從OrderDetail物件的productId去資料庫抓product圖片
            TextView tv_admin_order_detail_product_name;     //上
            TextView tv_admin_order_detail_price;    //上
            TextView tv_admin_order_detail_product_number;

            public AdminOderDetailViewHolder(@NonNull View itemView) {
                super(itemView);
                iv_order_product = itemView.findViewById(R.id.iv_order_product);
                tv_admin_order_detail_product_name = itemView.findViewById(R.id.tv_admin_order_detail_product_name);
                tv_admin_order_detail_price = itemView.findViewById(R.id.tv_admin_order_detail_price);
                tv_admin_order_detail_product_number = itemView.findViewById(R.id.tv_admin_order_detail_product_number);
            }
        }

        @Override
        public AdminOderDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_order_details_item, parent, false);
            return new AdminOderDetailViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(AdminOderDetailViewHolder holder, int position) {
            OrderDetail orderDetail = list.get(position);

            if (orderDetail.getProductPicturePath() == null) {
                holder.iv_order_product.setImageResource(R.drawable.no_product_image);
            } else {
                showImage(holder.iv_order_product, orderDetail.getProductPicturePath());
            }
            Log.d(TAG,"orderDetail.getProductId : "+ orderDetail.getProductId());
            holder.tv_admin_order_detail_product_name.setText(orderDetail.getProductName());
            holder.tv_admin_order_detail_price.setText("$" + orderDetail.getProductPrice());
            holder.tv_admin_order_detail_product_number.setText("x" + orderDetail.getProductNumber());
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }
        private void showImage(ImageView iv_order_product, String imagePath) {
            final int ONE_MEGABYTE = 1024 * 1024;
            StorageReference imageRef = storage.getReference().child(imagePath);
            imageRef.getBytes(ONE_MEGABYTE)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            byte[] bytes = task.getResult();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            iv_order_product.setImageBitmap(bitmap);
                        } else {
                            String message = task.getException() == null ?
                                    "Download fail" + " : " + imagePath :
                                    task.getException().getMessage() + " : " + imagePath;
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}