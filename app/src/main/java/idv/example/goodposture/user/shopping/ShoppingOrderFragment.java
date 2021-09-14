package idv.example.goodposture.user.shopping;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import idv.example.goodposture.R;
import idv.example.goodposture.user.my.Order;
import idv.example.goodposture.user.my.OrderDetail;

public class ShoppingOrderFragment extends Fragment {
    private static final String TAG = "TAG_ShoppingOrderFragment";
    private AppCompatActivity activity;
    private Toolbar toolbar;
    //資料
    private Bundle bundle;
    private List<Product> products;     //訂單內容內的商品，這裡的stock欄位是購買數量非庫存數量
    private Order order;
    double orderAmount = 0;        //訂單總金額
    //元件
    private RecyclerView recyclerView;
    private TextView tvOrderAmount;
    private TextView tvReceiveInfo;
    private EditText etReceiverName;
    private EditText etReceiverAddress;
    private EditText etReceiverPhone;
    private EditText etCardholder;
    private EditText etCreditCard1;
    private EditText etCreditCard2;
    private EditText etCreditCard3;
    private EditText etCreditCard4;
    private EditText etCreditCardCsv;
    private Button btOrderSend;
    //firebase
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (AppCompatActivity) getActivity();
        products = new ArrayList<>();
        order = new Order();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shopping_order, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);    //取得元件的參考
        handleToolbar();
        handleRecyclerView();
        fillOutData();
        sendOrder();
    }

    private void findViews(View view) {
        toolbar = view.findViewById(R.id.tb_shopping);
        recyclerView = view.findViewById(R.id.rv_shopping_order_detail);
        tvOrderAmount = view.findViewById(R.id.tv_order_amount);
        tvReceiveInfo = view.findViewById(R.id.tv_receive_info);
        etReceiverName = view.findViewById(R.id.et_receiver_name);
        etReceiverAddress = view.findViewById(R.id.et_receiver_address);
        etReceiverPhone = view.findViewById(R.id.et_receiver_phone);
        etCardholder = view.findViewById(R.id.et_cardholder);
        etCreditCard1 = view.findViewById(R.id.et_credit_card_1);
        etCreditCard2 = view.findViewById(R.id.et_credit_card_2);
        etCreditCard3 = view.findViewById(R.id.et_credit_card_3);
        etCreditCard4 = view.findViewById(R.id.et_credit_card_4);
        etCreditCardCsv = view.findViewById(R.id.et_credit_card_csv);
        btOrderSend = view.findViewById(R.id.bt_order_send);
    }

    private void handleToolbar() {
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle("結帳");
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void fillOutData() {
        tvReceiveInfo.setOnClickListener(v -> {
            etReceiverName.setText("康士坦");
            etReceiverAddress.setText("台北市中山區南京東路三段219號5樓");
            etReceiverPhone.setText("0227120589");
            etCardholder.setText("康士坦");
            etCreditCard1.setText("1234");
            etCreditCard2.setText("1234");
            etCreditCard3.setText("1234");
            etCreditCard4.setText("1234");
            etCreditCardCsv.setText("456");
        });

    }

    //覆寫menu選項的監聽
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            Navigation.findNavController(toolbar).popBackStack();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void handleRecyclerView() {
        //bundle = getArguments() != null? getArguments(): null;
        if (getArguments() != null) {
            Product product = (Product) getArguments().getSerializable("product");
            products.add(product);
        } else {
            products = ShoppingOrderData.orderProductListFromCart;
        }
        recyclerView.setAdapter(new shoppingOrderRVAdapter(getContext(), products));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //計算訂單總額

        for (Product p : products) {
            orderAmount += p.getPrice() * p.getStock();
        }
        tvOrderAmount.setText("$" + orderAmount);

    }

    private void sendOrder() {
        btOrderSend.setOnClickListener(v -> {


            //抓取判斷資料
            String receiverName = String.valueOf(etReceiverName.getText());
            String receiverAddress = String.valueOf(etReceiverAddress.getText());
            String receiverPhone = String.valueOf(etReceiverPhone.getText());
            String cardholder = String.valueOf(etCardholder.getText());
            StringBuilder creditCard = new StringBuilder(String.valueOf(etCreditCard1.getText()))
                    .append(String.valueOf(etCreditCard2.getText()))
                    .append(String.valueOf(etCreditCard3.getText()))
                    .append(String.valueOf(etCreditCard4.getText()));
            String creditCardCsv = String.valueOf(etCreditCardCsv.getText());
            //判斷購買區域的資料不為空
            if (receiverName.isEmpty()) {
                etReceiverName.setError("請輸入名字!");
                //return;
            }

            if (receiverAddress.isEmpty()) {
                etReceiverAddress.setError("請輸入地址");
                //return;
            }

            if (receiverPhone.isEmpty()) {
                etReceiverPhone.setError("請輸入電話");
                //return;
            }
            if (cardholder.isEmpty()) {
                etCardholder.setError("請輸入持卡者");
                //return;
            }
            //bug-如果有打其中一格就會判定通過
            if (creditCard.toString().isEmpty()) {
                etCreditCard4.setError("請輸入卡號");
                //return;
            }
            if (creditCardCsv.isEmpty()) {
                etCreditCardCsv.setError("請輸入卡號的csv");
                return;
            }

            //購買資料不為空後，開始設定訂單資訊
            final String id = db.collection("order").document().getId();
            final String uid = auth.getCurrentUser().getUid();
            //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            //String time = dtf.format(LocalDateTime.now());
            Date orderTime = new Date(System.currentTimeMillis());

            order.setId(id);
            order.setUid(uid);
            order.setOrderTime(orderTime);
            order.setOrderState(1);
            order.setReceiverName(receiverName);
            order.setReceiverPhone(receiverPhone);
            order.setOrderAmount(orderAmount);
            //!!沒有給cancel值
            //未來做金流，就判斷金流成功 = > 執行 addOrderToDb
            //加入一筆訂單進入資料庫
            addOrderToDb(order);
            //加入多筆訂單詳情進入資料庫
            for(Product product : products){
                OrderDetail orderDetail = new OrderDetail();
                final  String orderDetailId = db.collection("orderDetail").document().getId();
                orderDetail.setId(orderDetailId);
                orderDetail.setOrderId(order.getId());
                orderDetail.setProductId(product.getId());
                orderDetail.setProductName(product.getName());
                orderDetail.setProductPicturePath(product.getPicturePath());
                orderDetail.setProductNumber(product.getStock());   //這邊product的stock指的是這個商品的購買數量
                orderDetail.setProductPrice(product.getPrice());
                addOrderDetailToDb(orderDetail);
            }
            //刪除購買的購物車明細
            deleteCartDetailList();
            //扣除商品的庫存數量
            deleteProductStock();
            //跳出進度條
            ProgressDialog dialog = new ProgressDialog(getContext());
            /**設置UI形式為轉圈圈*/
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setTitle("結帳資訊確認中");
            dialog.show();
            new Thread(()->{
                for (int i = 0; i <100 ; i++) {
                    /**設置要告訴用戶的話*/
                    dialog.setMessage("結帳中");
                    SystemClock.sleep(30);
                }
                dialog.dismiss();

            }).start();
            //讓主執行緒等一下
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //執行在主執行緒
                    NavController navController = Navigation.findNavController(btOrderSend);
                    navController.navigate(R.id.action_shoppingOrderFragment_to_shoppingPayResultFragment);
                }
            },3000);
        });

    }
    //加入一筆訂單進入資料庫
    private void addOrderToDb(Order order) {
        db.collection("order").document(order.getId()).set(order)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String message = "Order is inserted"
                                + " with ID: " + order.getId();
                        Log.d(TAG, message);
                        //Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        // 訂單新增完畢回訂單成功頁面
                    } else {
                        String message = task.getException() == null ?
                                "Insert order failed" :
                                task.getException().getMessage();
                        Log.e(TAG, "message: " + message);
                        //Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //加入多筆訂單詳情進入資料庫
    private void addOrderDetailToDb(OrderDetail orderDetail) {
        db.collection("orderDetail").document(orderDetail.getId()).set(orderDetail)
                .addOnCompleteListener(task -> {
                   if(task.isSuccessful()){
                       String message = "OrderDetail is inserted"
                               + " with ID: " + orderDetail.getId();
                       Log.d(TAG, message);
                       //Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                   } else{
                       String message = task.getException() == null ?
                               "Insert order failed" :
                               task.getException().getMessage();
                       Log.e(TAG, "message: " + message);
                   }
                });
    }
    //刪除購買的購物車明細
    //抓出當前使用者購買的商品的購物車明細，然後在deleteCartDetail刪除購物車明細
    private void deleteCartDetailList() {
        //List<CartDetail> cartDetailsDelete = new ArrayList<>();
        db.collection("cartDetail")
                .whereEqualTo("uid", auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    List<CartDetail> cartDetailsDelete = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CartDetail cartDetail = document.toObject(CartDetail.class);
                            //Log.d(TAG, cartDetail.getId());
                            for (Product product : products) {
                                if (product.getId().equals(cartDetail.getProductId())) {
                                    cartDetailsDelete.add(cartDetail);
                                }
                            }
                        }
                        for(CartDetail cartDetail : cartDetailsDelete){
                            deleteCartDetail(cartDetail);
                        }
                    } else {
                        //如果錯誤，則印出錯誤訊息
                        String message = task.getException() == null ?
                                "No CartDetail found" :
                                task.getException().getMessage();
                        Log.e(TAG, "exception message: " + message);
                    }
                });
    }
    //刪除購物車明細
    private void deleteCartDetail(CartDetail cartDetail) {
        db.collection("cartDetail").document(cartDetail.getId()).delete()
                .addOnCompleteListener(task ->{
                    if(task.isSuccessful()){
                        Log.d(TAG,"delete cartDetails：" + cartDetail.getId());
                    }else {
                        Log.d(TAG, "delete failed");
                    }
                });
    }
    //扣除商品的庫存數量
    private void deleteProductStock() {
        //products是訂單內容內的商品
        for(Product product : products){
            db.collection("product").document(product.getId()).get()
                    .addOnCompleteListener(task -> {
                       if(task.isSuccessful() && task.getResult() != null){
                           DocumentSnapshot document  = task.getResult();
                           Product p = document.toObject(Product.class);
                           int newStock = p.getStock()-product.getStock();
                           p.setStock(newStock);
                           db.collection("product").document(p.getId()).set(p);
                       } else{
                           //如果錯誤，則印出錯誤訊息
                           String message = task.getException() == null ?
                                   "No product found" :
                                   task.getException().getMessage();
                           Log.e(TAG, "exception message: " + message);
                       }
                    });
        }
    }

    private class shoppingOrderRVAdapter extends RecyclerView.Adapter<shoppingOrderRVAdapter.shoppingOrderViewHolder> {


        private Context context;
        private List<Product> list;

        public shoppingOrderRVAdapter(Context context, List<Product> list) {
            this.context = context;
            this.list = list;
        }

        private class shoppingOrderViewHolder extends RecyclerView.ViewHolder {
            ImageView ivProduct;
            TextView tvProductName;
            TextView tvProductPrice;
            TextView tvProductNumber;

            public shoppingOrderViewHolder(@NonNull View itemView) {
                super(itemView);
                ivProduct = itemView.findViewById(R.id.iv_product);
                tvProductName = itemView.findViewById(R.id.tv_product_name);
                tvProductPrice = itemView.findViewById(R.id.tv_product_price);
                tvProductNumber = itemView.findViewById(R.id.tv_product_number);
            }
        }

        @Override
        public shoppingOrderRVAdapter.shoppingOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_shopping_order, parent, false);
            return new shoppingOrderRVAdapter.shoppingOrderViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(shoppingOrderRVAdapter.shoppingOrderViewHolder holder, int position) {
            Product product = list.get(position);
            // 如果存有圖片路徑，取得圖片後顯示
            if (product.getPicturePath() != null) {
                showImage(holder.ivProduct, product.getPicturePath());
            } else {
                holder.ivProduct.setImageResource(R.drawable.no_product_image);
            }
            holder.tvProductName.setText(product.getName());
            holder.tvProductPrice.setText("$" + product.getPrice());
            holder.tvProductNumber.setText("x" + product.getStock());
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }
    }

    private void showImage(final ImageView imageView, final String path) {
        final int ONE_MEGABYTE = 1024 * 1024;
        StorageReference imageRef = storage.getReference().child(path);
        imageRef.getBytes(ONE_MEGABYTE)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        byte[] bytes = task.getResult();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageView.setImageBitmap(bitmap);
                    } else {
                        String message = task.getException() == null ?
                                getString(R.string.textImageDownloadFail) + ": " + path :
                                task.getException().getMessage() + ": " + path;
                        Log.e(TAG, message);
                        //Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}