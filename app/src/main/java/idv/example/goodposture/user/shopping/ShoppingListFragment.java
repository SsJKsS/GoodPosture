package idv.example.goodposture.user.shopping;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
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

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import idv.example.goodposture.R;

public class ShoppingListFragment extends Fragment {
    private static final String TAG = "TAG_ShoppingListFragment";
    private AppCompatActivity activity;
    private int type;  //從首頁傳過來的商品種類int

    private Toolbar toolbar;
    private SearchView searchView;
    private RecyclerView recyclerView;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private ListenerRegistration registration;  //全程監控db的資料，所以db一變動就會更新資料
    private List<Product> products;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(TAG,"onCreate() ");
        setHasOptionsMenu(true);
        activity = (AppCompatActivity) getActivity();
        type = (int)(getArguments() != null? getArguments().get("type") : 0);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        products = new ArrayList<>();
        listenToProducts();    // 加上異動監聽器
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView() " );
        return inflater.inflate(R.layout.fragment_shopping_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);    //取得元件的參考
        handleToolbar();
        handleSearchView();
    }

    //希望重新回到這個頁面可以重新更新資料
    @Override
    public void onStart() {
        super.onStart();
        showAllProducts();
        searchView.requestFocus();
        Log.d(TAG,"onStart() ");
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
        searchView = view.findViewById(R.id.searchView_shopping_list);
        recyclerView = view.findViewById(R.id.rv_shopping_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
    }

    private void handleToolbar() {
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle("商品列表");
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //建立ToolBar的menu選單
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //載入menu
        inflater.inflate(R.menu.shopping_cart_toolbar_menu, menu);
    }

    //覆寫menu選項的監聽 //返回鑑被視為功能選單的選項
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_toolbar_cart) {
            //建立navController// 跳至shoppingCart頁面
            NavController navController = Navigation.findNavController(toolbar);
            navController.navigate(R.id.action_shoppingListFragment_to_shoppingCartFragment);
            return true;
        } else if (itemId == android.R.id.home) {
            Navigation.findNavController(toolbar).popBackStack();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    private void handleSearchView() {
        if(type == 1){
            searchView.setQuery("食品",true);
        }else if(type == 2){
            searchView.setQuery("器材",true);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //提交文字時呼叫
            @Override
            public boolean onQueryTextSubmit(String query) {
                showProducts();
                Log.d(TAG, "handleSearchView");
                return false;
            }
            // 文字搜尋框發生變化時呼叫
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
    // 顯示所有商品資訊
    private void showAllProducts() {
        //get()會抓db所有資料
        db.collection("product")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // 先清除舊資料後再儲存新資料
                        if (!products.isEmpty()) {
                            products.clear();
                        }
                        //把每個document轉成object物件
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            products.add(document.toObject(Product.class));
                        }
                        showProducts();
                    } else {
                        String message = task.getException() == null ?
                                "No Product found" :
                                task.getException().getMessage();
                        Log.e(TAG, "exception message: " + message);
                        //Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    //拿到spots list並轉成object去解析
    private void showProducts() {
        ProductRvAdapter productRvAdapter = (ProductRvAdapter) recyclerView.getAdapter();
        if (productRvAdapter == null) {
            productRvAdapter = new ProductRvAdapter();
            recyclerView.setAdapter(productRvAdapter);
        }
        // 如果搜尋條件為空字串，就顯示原始資料；否則就顯示搜尋後結果
        //將符合條件的spot(Spot)放到spots(List)裡
        String queryStr = searchView.getQuery().toString();
        if (queryStr.isEmpty()) {
            productRvAdapter.setProducts(products);
            //Log.d(TAG,"query.isEmpty()");
        } else {
            List<Product> searchProducts = new ArrayList<>();
            if(queryStr.equals("食品")){
                for (Product product : products) {
                    if (product.getType() == 1) {
                        searchProducts.add(product);
                    }
                }
            }else if(queryStr.equals("器材")){
                for (Product product : products) {
                    if (product.getType() == 2) {
                        searchProducts.add(product);
                    }
                }
            }else{
                // 搜尋原始資料內有無包含關鍵字(不區別大小寫)
                for (Product product : products) {
                    if (product.getName().toUpperCase().contains(queryStr.toUpperCase())) {
                        searchProducts.add(product);
                    }
                }
            }
            //setProducts是在Adapter類別內自定義的一個方法，用來設定顯示的清單
            productRvAdapter.setProducts(searchProducts);
        }

        productRvAdapter.notifyDataSetChanged();
    }

    private class ProductRvAdapter extends RecyclerView.Adapter<ProductRvAdapter.ProductViewHolder> {
        private List<Product> products;

        public ProductRvAdapter() {
        }

        public void setProducts(List<Product> products) {
            this.products = products;
        }

        private class ProductViewHolder extends RecyclerView.ViewHolder {
            ImageView ivProduct;
            TextView tvProductName;
            TextView tvProductPrice;

            public ProductViewHolder(@NonNull View itemView) {
                super(itemView);
                ivProduct = itemView.findViewById(R.id.iv_product);
                tvProductName = itemView.findViewById(R.id.tv_product_name);
                tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            }
        }

        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(activity);
            View itemView = layoutInflater.inflate(R.layout.item_view_shopping_list, parent, false);
            return new ProductViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull ProductViewHolder holder, int position) {
            final Product product = products.get(position);
            //product圖片路徑為空值 -> 給default圖片
            if (product.getPicturePath() == null) {
                holder.ivProduct.setImageResource(R.drawable.no_product_image);
            } else {
                showImage(holder.ivProduct, product.getPicturePath());
            }
            //holder.ivProduct.setImageResource(R.drawable.shopping_cat3);
            holder.tvProductName.setText(product.getName());
            holder.tvProductPrice.setText("$" + product.getPrice());
            holder.itemView.setOnClickListener(v -> {
                //寫帶過去的資料
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", product);
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_shoppingListFragment_to_shoppingProductFragment, bundle);
            });
        }

        @Override
        public int getItemCount() {
            return products == null ? 0 : products.size();
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
    /**
     * 監聽資料是否發生異動，有則同步更新。
     * 開啟2台模擬器，一台新增/修改/刪除；另一台畫面會同步更新
     * 但自己做資料異動也會觸發監聽器
     */
    private void listenToProducts() {
        if (registration == null) {
            //監聽到這個表有異動
            registration = db.collection("product").addSnapshotListener((snapshots, e) -> {
                Log.d(TAG, "event happened");
                if (e == null) {
                    List<Product> products = new ArrayList<>();
                    if (snapshots != null) {
                        //取得發生異動的資料
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            Product product = dc.getDocument().toObject(Product.class);
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "Added products: " + product.getName());
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified products: " + product.getName());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed products: " + product.getName());
                                    break;
                                default:
                                    break;
                            }
                        }

                        for (DocumentSnapshot document : snapshots.getDocuments()) {
                            products.add(document.toObject(Product.class));
                        }
                        this.products = products;
                        showProducts();
                    }
                } else {
                    Log.e(TAG, e.getMessage(), e);
                }
            });
        }
    }
}