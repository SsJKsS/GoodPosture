package idv.example.goodposture.user.shopping;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import idv.example.goodposture.R;

public class ShoppingOrderFragment extends Fragment {
    private static final String TAG = "TAG_ShoppingOrderFragment";
    private AppCompatActivity activity;
    private Toolbar toolbar;
    //資料
    private Bundle bundle;
    //public static List<Product> orderProductListFromCart = ShoppingOrderData.orderProductListFromCart;
    private List<Product> products;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (AppCompatActivity) getActivity();
        products = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
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

    //覆寫menu選項的監聽
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == android.R.id.home){
            Navigation.findNavController(toolbar).popBackStack();
        }else{
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void handleRecyclerView() {
        //bundle = getArguments() != null? getArguments(): null;
        if(getArguments() != null){
            Product product = (Product) getArguments().getSerializable("product");
            products.add(product);
        }else{
            products = ShoppingOrderData.orderProductListFromCart;
        }
        recyclerView.setAdapter(new shoppingOrderRVAdapter(getContext(), products));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //計算訂單總額
        double orderAmount = 0;
        for(Product p : products){
            orderAmount += p.getPrice() * p.getStock();
        }
        tvOrderAmount.setText("$" + orderAmount);

    }

    private void sendOrder() {
        btOrderSend.setOnClickListener(v -> {
            final String receiverName = String.valueOf(etReceiverName.getText());
            String receiverAddress = String.valueOf(etReceiverAddress.getText());
            String receiverPhone = String.valueOf(etReceiverPhone.getText());
            String cardholder = String.valueOf(etCardholder.getText());
            StringBuilder creditCard = new StringBuilder(String.valueOf(etCreditCard1.getText()))
                    .append(String.valueOf(etCreditCard2.getText()))
                    .append(String.valueOf(etCreditCard3.getText()))
                    .append(String.valueOf(etCreditCard4.getText()));
            //int creditCardCsv = Integer.parseInt(String.valueOf(etCreditCardCsv.getText()));

            if(receiverName.isEmpty()){
                etReceiverName.setError("請輸入名字!");
            }else{
                NavController navController = Navigation.findNavController(btOrderSend);
                navController.navigate(R.id.action_shoppingOrderFragment_to_shoppingPayResultFragment);
            }
        });
    }

    private class shoppingOrderRVAdapter extends RecyclerView.Adapter<shoppingOrderRVAdapter.shoppingOrderViewHolder>{


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
            }else{
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
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}