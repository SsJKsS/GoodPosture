package idv.example.goodposture.admin.commodity;

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
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import idv.example.goodposture.R;
import idv.example.goodposture.user.shopping.Product;

public class AdminCommodityFragment extends Fragment {
    private static final String TAG = "TAG_AdminCommodityFragment";
    private AppCompatActivity activity;
    private List<Product> products;

    private Toolbar toolbar;
    private SearchView svCommodity;
    private RecyclerView rvCommodity;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private ListenerRegistration registration;  //全程監控db的資料，所以db一變動就會更新資料

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (AppCompatActivity) getActivity();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        products = new ArrayList<>();
        // 加上異動監聽器
        listenToProducts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_commodity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleToolbar();
        handleSearchView();
    }

    //希望重新回到這個頁面可以重新更新資料
    @Override
    public void onStart() {
        super.onStart();
        //showAllProducts();
        showProducts();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
//        // 解除異動監聽器
//        if (registration != null) {
//            registration.remove();
//            registration = null;
//        }
    }

    private void findViews(View view) {
        toolbar = view.findViewById(R.id.tb_admin_commodity_context);
        svCommodity = view.findViewById(R.id.sv_com);
        //tvInsert = view.findViewById(R.id.tv_com_insert);
        rvCommodity = view.findViewById(R.id.rv_com);
        rvCommodity.setLayoutManager(new LinearLayoutManager(activity));
    }

    private void handleToolbar() {
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle("");
        ActionBar actionBar = activity.getSupportActionBar();
    }

    private void handleSearchView() {
        svCommodity.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                showProducts();
                return true;
            }
        });
    }

    //建立ToolBar的menu選單
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //載入menu
        inflater.inflate(R.menu.admin_commodity_list_menu, menu);
    }

    //覆寫menu選項的監聽 //返回鑑被視為功能選單的選項
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(toolbar);
        int itemId = item.getItemId();
        if (itemId == R.id.menu_toolbar_add) {
            navController.navigate(R.id.action_adminCommodityFragment_to_adminCommodityAddFragment);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    // 顯示所有景點資訊
    private void showAllProducts() {
        //get()會抓db所有資料
        db.collection("product").get()
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
                        //依照日期排序products
                        Collections.sort(products, (p1, p2) ->
                                -1 * p1.getDate().compareTo(p2.getDate()));
                        showProducts();
                    } else {
                        String message = task.getException() == null ?
                                "No Product Found":
                                task.getException().getMessage();
                        Log.e(TAG, "exception message: " + message);
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //拿到Products list並轉成object去解析
    private void showProducts() {
        ProductAdapter productAdapter = (ProductAdapter) rvCommodity.getAdapter();
        if (productAdapter == null) {
            productAdapter = new ProductAdapter();
            rvCommodity.setAdapter(productAdapter);
        }
        // 處理搜尋結果
        String queryStr = svCommodity.getQuery().toString();
        List<Product> searchProducts = new ArrayList<>();
        // 搜尋商品名字(不區別大小寫)
        for (Product product : products) {
            if (product.getName().toUpperCase().contains(queryStr.toUpperCase())) {
                searchProducts.add(product);
            }
        }
        productAdapter.setProducts(searchProducts);
        productAdapter.notifyDataSetChanged();
    }

    private class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
        List<Product> products;

        ProductAdapter() {
        }

        public void setProducts(List<Product> products) {
            this.products = products;
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            ImageView ivProduct;
            TextView tvId, tvName, tvPrice, tvStock, tvSellAmount;

            ProductViewHolder(View itemView) {
                super(itemView);
                ivProduct = itemView.findViewById(R.id.iv_com_product);
                tvId = itemView.findViewById(R.id.tv_com_id);
                tvName = itemView.findViewById(R.id.tv_com_name);
                tvPrice = itemView.findViewById(R.id.tv_com_price);
                tvStock = itemView.findViewById(R.id.tv_com_stock);
                tvSellAmount = itemView.findViewById(R.id.tv_com_sell_amount);
            }
        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(activity);
            View itemView = layoutInflater.inflate(R.layout.admin_product_item, parent, false);
            return new ProductViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            final Product product = products.get(position);
            //景點圖片路徑為空值 -> 給default圖片
            if (product.getPicturePath() == null) {
                holder.ivProduct.setImageResource(R.drawable.no_product_image);
            } else {
                showImage(holder.ivProduct, product.getPicturePath());
            }
            holder.tvId.setText(product.getId());
            holder.tvName.setText(product.getName());
            holder.tvPrice.setText("$"+product.getPrice());
            holder.tvStock.setText(product.getStock() + "");
            holder.tvSellAmount.setText(product.getSellAmount() + "");

            // 點選會開啟修改頁面
            holder.itemView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", product);
                Navigation.findNavController(v)
                        .navigate(R.id.action_adminCommodityFragment_to_adminCommodityContextFragment, bundle);
            });

        }
    }

    // 下載Firebase storage的照片並顯示在ImageView上
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
                                getString(R.string.textImageDownloadFail) + ": " + path :
                                task.getException().getMessage() + ": " + path;
                        Log.e(TAG, message);
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });

    }

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
                                    Log.d(TAG, "Added product: " + product.getName());
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified product: " + product.getName());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed product: " + product.getName());
                                    break;
                                default:
                                    break;
                            }
                        }

                        for (DocumentSnapshot document : snapshots.getDocuments()) {
                            products.add(document.toObject(Product.class));
                        }
                        //依照日期排序products
                        this.products = products;
                        Collections.sort(products, (p1, p2) ->
                                -1 * p1.getDate().compareTo(p2.getDate()));
                        showProducts();
                    }
                } else {
                    Log.e(TAG, e.getMessage(), e);
                }
            });
        }
    }
}