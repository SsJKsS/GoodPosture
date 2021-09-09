package idv.example.goodposture.user.shopping;

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
import android.widget.CheckBox;
import android.widget.ImageView;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import idv.example.goodposture.R;
import idv.example.goodposture.user.my.Myinfo;

public class ShoppingCartFragment extends Fragment {
    private static final String TAG = "TAG_ShoppingCartFragment";
    private AppCompatActivity activity;
    //資料
    private List<CartDetail> cartDetailList;
    private List<Product> productList;
    //元件
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private CartDetailRvAdapter cartDetailRvAdapter;
    private CheckBox cbSelectAll;
    private TextView tvTotalPrice;
    private Button btCartCheckout;
    //firebase
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private ListenerRegistration registration;  //全程監控db的資料，所以db一變動就會更新資料

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (AppCompatActivity) getActivity();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        cartDetailList = new ArrayList<>();
        productList = new ArrayList<>();
        listenToCartDetail();   //todo 是否需要做全程監聽
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shopping_cart, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleToolbar();
        showCartDetails();  //  顯示在rv
        checkout();
    }

    //希望重新回到這個頁面可以重新更新資料

    @Override
    public void onStart() {
        super.onStart();
        showMyCartDetails();
        Log.d(TAG, "onStart() ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 解除異動監聽器
        if (registration != null) {
            registration.remove();
            registration = null;
        }
    }

    private void findViews(View view) {
        toolbar = view.findViewById(R.id.tb_shopping);
        recyclerView = view.findViewById(R.id.rv_shopping_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        cbSelectAll = view.findViewById(R.id.cb_selectAll);
        tvTotalPrice = view.findViewById(R.id.tv_total_price);
        btCartCheckout = view.findViewById(R.id.bt_cart_checkout);
    }

    private void handleToolbar() {
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle("購物車");
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //返回鑑被視為功能選單的選項
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            Navigation.findNavController(toolbar).popBackStack();
        }
        return true;
    }

    //顯示當前user的所有cartDetail
    private void showMyCartDetails() {
        //get()會抓db所有資料
        db.collection("cartDetail").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // 先清除舊資料後再儲存新資料
                        if (!cartDetailList.isEmpty()) {
                            //Log.d(TAG, "cartDetailLis有東西，先清空");
                            cartDetailList.clear();
                            productList.clear();
                        }
                        //把每個document轉成object物件
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //字串 == 字串 => 比對兩者的記憶體位址
                            CartDetail cartDetail = document.toObject(CartDetail.class);
                            if (cartDetail.getUid().equals(auth.getCurrentUser().getUid())) {
                                cartDetailList.add(cartDetail);
                            }
                        }
                        showCartDetails();
                    } else {
                        String message = task.getException() == null ?
                                "No cartDetail found" :
                                task.getException().getMessage();
                        Log.e(TAG, "exception message: " + message);
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showCartDetails() {
        //CartDetailRvAdapter cartDetailRvAdapter = (CartDetailRvAdapter) recyclerView.getAdapter();
        cartDetailRvAdapter = (CartDetailRvAdapter) recyclerView.getAdapter();
        if (cartDetailRvAdapter == null) {
            Log.d(TAG, "cartDetailRvAdapter is null");
            cartDetailRvAdapter = new CartDetailRvAdapter();
            recyclerView.setAdapter(cartDetailRvAdapter);
        }

        //點擊最下面的checkbox，會全選所有itemView的checkbox
        cbSelectAll.setOnClickListener(v -> {
            cartDetailRvAdapter.selectAllItemView();
        });
        cartDetailRvAdapter.setCartDetailList(cartDetailList);
        cartDetailRvAdapter.setProductList(productList);
        cartDetailRvAdapter.notifyDataSetChanged();
    }

    private class CartDetailRvAdapter extends RecyclerView.Adapter<CartDetailRvAdapter.CartDetailViewHolder> {
        private List<CartDetail> cartDetailList;
        private List<Product> productList;
        //checkbox的Hashmap集合，存放每個位置的itemView的checkbox是否被選取的資料
        private HashMap<Integer, Boolean> cbMap;
        //amountView的Hashmap集合，存放每個位置的itemView的amount數量
        private HashMap<Integer, Integer> amMap;
        //productMap，存放每個位置的product
        private HashMap<Integer, Product> productMap;

        public CartDetailRvAdapter() {
        }

        public void setCartDetailList(List<CartDetail> cartDetailList) {
            this.cartDetailList = cartDetailList;
            this.cbMap = new HashMap<>();
            this.amMap = new HashMap<>();
            this.productMap = new HashMap<>();
            //list有多少條資料就增加多少個checlbox的Hashmap集合
            for (int i = 0; i < cartDetailList.size(); i++) {
                this.cbMap.put(i, false);
                this.amMap.put(i, 1);
            }
            //商品資訊
            //buyProduct = new ArrayList<>();
        }

        public void setProductList(List<Product> productList) {
            this.productList = productList;
        }

        //全選所有itemView的checkBox
        public void selectAllItemView() {
            Set<Map.Entry<Integer, Boolean>> entries = cbMap.entrySet();
            for (Map.Entry<Integer, Boolean> entry : entries) {
                entry.setValue(cbSelectAll.isChecked());
            }
            showTotal();
            notifyDataSetChanged();
        }

        //計算被勾選的itemView的金額
        public void showTotal() {
            Set<Map.Entry<Integer, Boolean>> cbEntries = cbMap.entrySet();
            Set<Integer> positions = amMap.keySet();
            double total = 0;
            for (Map.Entry<Integer, Boolean> entry : cbEntries) {
                int amount = 0;
                double price = 0;
                if (entry.getValue()) {
                    CartDetail cartDetail = cartDetailList.get(entry.getKey());
                    price = cartDetail.getProductPrice();
                    for (Integer position : positions) {
                        if (entry.getKey() == position) {
                            amount = amMap.get(position);
                            break;
                        }
                    }
                }
                total += (double) amount * price;
            }
            tvTotalPrice.setText("$" + total);
        }

        //被選取的item的product存在一個陣列
        public List<Product> storeOrderData(){
            //cbMap:<index, checkBox is checked?>
            //amMap:<index, amount of amountView>
            Set<Map.Entry<Integer, Boolean>> cbEntries = cbMap.entrySet();
            Set<Integer> positions = amMap.keySet();
            List<Product> orderProductList = new ArrayList<>();
            for (Map.Entry<Integer, Boolean> entry : cbEntries) {
                if (entry.getValue()) {
                    //bug-productlist不一定會對齊 =>解決
                    //Product product = productList.get(entry.getKey());
                    Product product = productMap.get(entry.getKey());
                    for (Integer position : positions) {
                        if (entry.getKey() == position) {
                            product.setStock(amMap.get(position));
                            break;
                        }
                    }
                    orderProductList.add(product);
                }
            }
            return orderProductList;
        }


        private class CartDetailViewHolder extends RecyclerView.ViewHolder {
            CheckBox cbCartItem;
            ImageView ivProduct;
            TextView tvProductName;
            TextView tvProductPrice;
            AmountView amountViewProduct;
            ImageView ivItemDelete;


            public CartDetailViewHolder(@NonNull View itemView) {
                super(itemView);
                cbCartItem = itemView.findViewById(R.id.cb_cart_item);
                ivProduct = itemView.findViewById(R.id.iv_cart_item_product);
                tvProductName = itemView.findViewById(R.id.tv_product_name);
                tvProductPrice = itemView.findViewById(R.id.tv_product_price);
                amountViewProduct = itemView.findViewById(R.id.amountview_product);
                ivItemDelete = itemView.findViewById(R.id.iv_cart_item_delete);
            }
        }

        @Override
        public CartDetailRvAdapter.CartDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(activity);
            View itemView = layoutInflater.inflate(R.layout.item_view_shopping_cart, parent, false);
            return new CartDetailRvAdapter.CartDetailViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull CartDetailRvAdapter.CartDetailViewHolder holder, int position) {
            final CartDetail cartDetail = cartDetailList.get(position);

            db.collection("product")
                    .document(Objects.requireNonNull(cartDetail.getProductId()))
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // 將獲取的資料存成自定義類別
                            DocumentSnapshot documentSnapshot = task.getResult();
                            Product product = documentSnapshot.toObject(Product.class);
                            productList.add(product);
                            productMap.put(position,product);
                            holder.tvProductName.setText(product.getName());
                            holder.tvProductPrice.setText("$" + product.getPrice());
                            holder.amountViewProduct.setGoods_storage(product.getStock());
                            holder.amountViewProduct.setAmount(cartDetail.getProductAmount());
                            //點擊itemView會跳轉到商品頁
                            holder.itemView.setOnClickListener(v -> {
                                //寫帶過去的資料
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("product", product);
                                NavController navController = Navigation.findNavController(v);
                                navController.navigate(R.id.action_shoppingCartFragment_to_shoppingProductFragment, bundle);
                            });

                            //product圖片路徑為空值 -> 給default圖片
                            if (product.getPicturePath() == null) {
                                holder.ivProduct.setImageResource(R.drawable.no_product_image);
                            } else {
                                showImage(holder.ivProduct, product.getPicturePath());
                            }

                        } else {
                            String message = task.getException() == null ?
                                    "No cartDetail found" :
                                    task.getException().getMessage();
                            Log.e(TAG, "exception message: " + message);
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    });
            //amountViewProduct的數量
            holder.amountViewProduct.setOnAmountChangeListener((view, amount) -> {
                //拿到AmountView的顯示數字
                amMap.put(position, amount);
                showTotal();
            });
            //checkbox要先設定checked狀態!!!!
            holder.cbCartItem.setChecked(cbMap.get(position));
            //itemView的checkBox被點擊後，更新cbMap資料
            holder.cbCartItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cbMap.put(position, !cbMap.get(position));
                    //tvTotalPrice.setText(position+"");
                    showTotal();
                    //重新整理recyclerView
                    notifyDataSetChanged();
                }
            });
            //刪除購物車的一筆資料
            holder.ivItemDelete.setOnClickListener(v -> {
                db.collection("cartDetail").document(cartDetail.getId()).delete()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                showCartDetails();
                                //Toast.makeText(activity, "cartDetail is delete ", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(activity, "cartDetail is delete fail", Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        }

        @Override
        public int getItemCount() {
            return cartDetailList == null ? 0 : cartDetailList.size();
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

    //點擊結帳後跳轉到訂單詳情&結帳頁面
    private void checkout() {
        btCartCheckout.setOnClickListener(v -> {

            //將List存到靜態變數裡面
            ShoppingOrderData.orderProductListFromCart = cartDetailRvAdapter.storeOrderData();
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_shoppingCartFragment_to_shoppingOrderFragment);
        });
    }


    /**
     * 監聽資料是否發生異動，有則同步更新。
     * 開啟2台模擬器，一台新增/修改/刪除；另一台畫面會同步更新
     * 但自己做資料異動也會觸發監聽器
     */
    private void listenToCartDetail() {
        if (registration == null) {
            //監聽到這個表有異動
            registration = db.collection("cartDetail").addSnapshotListener((snapshots, e) -> {
                Log.d(TAG, "event happened");
                if (e == null) {
                    List<CartDetail> cartDetailList = new ArrayList<>();
                    if (snapshots != null) {
                        //取得發生異動的資料
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            CartDetail cartDetail = dc.getDocument().toObject(CartDetail.class);
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "Added CartDetail: " + cartDetail.getId());
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified CartDetail: " + cartDetail.getId());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed CartDetail: " + cartDetail.getId());
                                    break;
                                default:
                                    break;
                            }
                        }

                        for (DocumentSnapshot document : snapshots.getDocuments()) {
                            cartDetailList.add(document.toObject(CartDetail.class));
                        }
                        this.cartDetailList = cartDetailList;
//                        if (cartDetailList.size() <= 0) {
//                            Log.d(TAG, "cartDetailList is empty");
//                        } else {
//                            Log.d(TAG, "cartDetailList is not empty");
//                        }

                        showMyCartDetails();
                    }
                } else {
                    Log.e(TAG, e.getMessage(), e);
                }
            });
        }
    }


}