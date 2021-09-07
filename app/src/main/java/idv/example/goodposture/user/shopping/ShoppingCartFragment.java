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
    private Product product = new Product();
    //元件
    private Toolbar toolbar;
    private RecyclerView recyclerView;
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
        tvTotalPrice = view.findViewById(R.id.tv_total);
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
                        }

                        //把每個document轉成object物件
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            cartDetailList.add(document.toObject(CartDetail.class));
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
        CartDetailRvAdapter cartDetailRvAdapter = (CartDetailRvAdapter) recyclerView.getAdapter();
        if (cartDetailRvAdapter == null) {
            Log.d(TAG,"cartDetailRvAdapter is null");
            cartDetailRvAdapter = new CartDetailRvAdapter();
            recyclerView.setAdapter(cartDetailRvAdapter);
        }

        cartDetailRvAdapter.setCartDetailList(cartDetailList);
        cartDetailRvAdapter.notifyDataSetChanged();
    }

    private class CartDetailRvAdapter extends RecyclerView.Adapter<CartDetailRvAdapter.CartDetailViewHolder> {
        private List<CartDetail> cartDetailList;

        public CartDetailRvAdapter() {
        }

        public void setCartDetailList(List<CartDetail> cartDetailList) {
            this.cartDetailList = cartDetailList;
//            if(cartDetailList == null){
//                Log.d(TAG, "cartDetailList is null");
//            }
        }

        private class CartDetailViewHolder extends RecyclerView.ViewHolder {
            ImageView ivProduct;
            TextView tvProductName;
            TextView tvProductPrice;
            AmountView amountViewProduct;
            ImageView ivItemDelete;

            public CartDetailViewHolder(@NonNull View itemView) {
                super(itemView);
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
            product = getProduct(cartDetail);
            String name = product.getName();
            Log.d(TAG,name);
//            //product圖片路徑為空值 -> 給default圖片
//            if (product.getPicturePath() == null) {
//                holder.ivProduct.setImageResource(R.drawable.no_product_image);
//            } else {
//                showImage(holder.ivProduct, product.getPicturePath());
//            }
//            //holder.ivProduct.setImageResource(R.drawable.shopping_cat3);
//            holder.tvProductName.setText(product.getName());
//            holder.tvProductPrice.setText("$" + product.getPrice());
        }

        @Override
        public int getItemCount() {
            return cartDetailList == null ? 0 : cartDetailList.size();
        }

        private Product getProduct(CartDetail cartDetail) {
            db.collection("product")
                    .document(Objects.requireNonNull(cartDetail.getProductId()))
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // 將獲取的資料存成自定義類別
                            DocumentSnapshot documentSnapshot = task.getResult();
                            product = documentSnapshot.toObject(Product.class);
                            assert  product != null;
                            Log.d(TAG,product.getName());
                        } else {
                            String message = task.getException() == null ?
                                    "No cartDetail found" :
                                    task.getException().getMessage();
                            Log.e(TAG, "exception message: " + message);
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    });
            return product;
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


//    private static class CartRecyclerViewAdapter extends RecyclerView.Adapter<CartRecyclerViewAdapter.CartViewHolder>{
//
//        private Context context;
//        private List<Product> list;
//        //checkbox的Hashmap集合，存放每個位置的itemView的checkbox是否被選取的資料
//        private  HashMap<Integer, Boolean> cbMap;
//
//        public CartRecyclerViewAdapter(Context context, List<Product> list) {
//            this.context = context;
//            this.list = list;
//            this.cbMap = new HashMap<>();
//            //list有多少條資料就增加多少個checlbox的Hashmap集合
//            for (int i = 0; i < list.size(); i++) {
//                this.cbMap.put(i, false);
//            }
//        }
//
//        /**
//         * 全選
//         */
//        public void selectAllItemView() {
//            Set<Map.Entry<Integer, Boolean>> entries = cbMap.entrySet();
//            boolean ckAll = false;
//            for (Map.Entry<Integer, Boolean> entry : entries) {
//                Boolean value = entry.getValue();
//                if (!value) {
//                    ckAll = true;
//                    break;
//                }
//            }
//            for (Map.Entry<Integer, Boolean> entry : entries) {
//                entry.setValue(ckAll);
//            }
//            notifyDataSetChanged();
//        }
//        //定義ViewHolder
//        private static class CartViewHolder extends RecyclerView.ViewHolder {
//            CheckBox cbCartItem;
//            ImageView ivCartItemProduct;
//            TextView tvProductName;
//            TextView tvProductPrice;
//            AmountView amountProduct;
//
//            public CartViewHolder(View itemView) {
//                super(itemView);
//                cbCartItem = itemView.findViewById(R.id.cb_cart_item);
//                ivCartItemProduct = itemView.findViewById(R.id.iv_cart_item_product);
//                tvProductName = itemView.findViewById(R.id.tv_product_name);
//                tvProductPrice = itemView.findViewById(R.id.tv_product_price);
//                amountProduct = itemView.findViewById(R.id.amountview_product);
//            }
//        }
//
//        @Override
//        public CartRecyclerViewAdapter.CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            //初始化佈局檔案
//            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_shopping_cart, parent, false);
//            return new CartRecyclerViewAdapter.CartViewHolder(itemView);
//        }
//
//        @Override
//        public void onBindViewHolder(CartRecyclerViewAdapter.CartViewHolder holder, int position) {
//            Product product = list.get(position);
////            holder.ivCartItemProduct.setImageResource(product.getImageId());
////            holder.tvProductName.setText(product.getProductName());
////            holder.tvProductPrice.setText("$" + product.getProductPrice());
//            holder.ivCartItemProduct.setOnClickListener( v -> {
//                //寫帶過去的資料
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("product", product);
//                NavController navController = Navigation.findNavController(v);
//                navController.navigate(R.id.action_shoppingCartFragment_to_shoppingProductFragment, bundle);
//            });
//            holder.amountProduct.setGoods_storage(50);  //product.getStorage()
//            holder.amountProduct.setOnAmountChangeListener(new AmountView.OnAmountChangeListener() {
//                @Override
//                public void onAmountChange(View view, int amount) {
//                    // amount：這筆訂單清單的商品數量
//                }
//            });
//            //checkbox要先設定checked狀態!!!!
//            holder.cbCartItem.setChecked(cbMap.get(position));
//            //itemView的checkBox被點擊後，更新cbMap資料
//            holder.cbCartItem.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    cbMap.put(position, !cbMap.get(position));
//                    //重新整理recyclerView
//                    notifyDataSetChanged();
//                }
//            });
//        }
//
//
//        @Override
//        public int getItemCount() {
//            return list == null ? 0 : list.size();
//        }
//
//    }

    private void checkout() {
        btCartCheckout.setOnClickListener(v -> {
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
                        if(cartDetailList.size() <= 0){
                            Log.d(TAG, "cartDetailList is empty");
                        }else{
                            Log.d(TAG, "cartDetailList is not empty");
                        }

                        showMyCartDetails();
                    }
                } else {
                    Log.e(TAG, e.getMessage(), e);
                }
            });
        }
    }


}