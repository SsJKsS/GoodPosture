package idv.example.goodposture.user.shopping;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import com.google.android.material.bottomsheet.BottomSheetDialog;

import javax.crypto.spec.IvParameterSpec;

import idv.example.goodposture.R;


public class ShoppingProductFragment extends Fragment {
    private static final String TAG = "ShoppingProductFragment";
    private AppCompatActivity activity;
    private Toolbar toolbar;
    private SearchView searchView;
    private Product product;
    private ImageView ivProduct;
    private TextView tvProductName;
    private TextView tvProductPrice;
    private TextView tvProductDesc;
    private Button btAddToCart;
    private Button btBuyProduct;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        setHasOptionsMenu(true);
        product = (Product) (getArguments() != null ? getArguments().getSerializable("product") : null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shopping_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleToolbar();
        showProduct(product);
        addToCart();
        buyProduct();
    }

    private void findViews(View view) {
        toolbar = view.findViewById(R.id.tb_shopping);
        ivProduct = view.findViewById(R.id.iv_product);
        tvProductName = view.findViewById(R.id.tv_product_name);
        tvProductPrice = view.findViewById(R.id.tv_product_price);
        tvProductDesc = view.findViewById(R.id.tv_product_description);
        btAddToCart = view.findViewById(R.id.bt_add_to_Cart);
        btBuyProduct = view.findViewById(R.id.bt_buy_product);
    }

    private void handleToolbar() {
        activity.setSupportActionBar(toolbar);
        //ToolBar的標題預設是AndroidManifest檔案中<Application/>標籤下屬性label設定的值
        toolbar.setTitle("");
        //設定toolbar為狀態列
        activity.setSupportActionBar(toolbar);
        //取用返回鑑的方法在actionBar
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
        inflater.inflate(R.menu.shopping_toolbar_menu, menu);
        //Log.d("onCreateOptionsMenu","success");
    }
    //返回鑑被視為功能選單的選項
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        final int itemId = item.getItemId();
        if(itemId == android.R.id.home){
            Navigation.findNavController(ivProduct).popBackStack();
        }else if(itemId == R.id.menu_toolbar_cart){
            NavController navController = Navigation.findNavController(toolbar);
            navController.navigate(R.id.action_shoppingProductFragment_to_shoppingCartFragment);
            return true;
        }else if(itemId == R.id.menu_toolbar_search){
            searchView = (SearchView) item.getActionView();
            handleSearchView();
            return  true;
        }else{
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
    //搜尋結果
    private void handleSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // // 提交文字時呼叫
            @Override
            public boolean onQueryTextSubmit(String query) {
                //搜尋文字
                Bundle bundle = new Bundle();
                bundle.putString("searchText",query);
                //建立navController
                NavController navController = Navigation.findNavController(toolbar);
                // 跳至頁面
                navController.navigate(R.id.action_fragmentShopping_to_shoppingListFragment,bundle);
                return false;
            }
            // 文字搜尋框發生變化時呼叫
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    //顯示商品畫面
    private void showProduct(Product product) {
        int imageId = product.getImageId();
        String productName = product.getProductName();
        int productPrice = product.getProductPrice();
        ivProduct.setImageResource(imageId);
        tvProductName.setText(productName);
        tvProductPrice.setText("$" + productPrice);
    }

    //加入購物車，顯示加入的動畫|改變購物車icon的圖案
    private void addToCart() {
        btAddToCart.setOnClickListener(v -> {
            //todo
            //顯示加入購物車的動畫|改變購物車icon的圖案
            Toast.makeText(activity, "加入購物車", Toast.LENGTH_SHORT).show();
        });
    }

    //購買商品，跳出bottomSheet確認數量並結帳
    private void buyProduct() {
        btBuyProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });
    }
    //todo
    private void showBottomSheetDialog(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetDialog.setContentView(R.layout.shopping_bottom_sheet_checkout);

        ImageView ivProduct = bottomSheetDialog.findViewById(R.id.iv_product);
        TextView tvProductPrice = bottomSheetDialog.findViewById(R.id.tv_product_price);
        TextView tvProductStock = bottomSheetDialog.findViewById(R.id.tv_product_stock);
        AmountView amountView = bottomSheetDialog.findViewById(R.id.amountView_bottomSheet);
        Button btCheckout = bottomSheetDialog.findViewById(R.id.bt_checkout);

        ivProduct.setImageResource(R.drawable.shopping_cat);

        amountView.setGoods_storage(50);
        amountView.setOnAmountChangeListener(new AmountView.OnAmountChangeListener() {
            @Override
            public void onAmountChange(View view, int amount) {

            }
        });

        //Log.d(TAG,String.valueOf(bottomSheetDialog));
        btCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(btBuyProduct);
                navController.navigate(R.id.action_shoppingProductFragment_to_shoppingOrderFragment);
                //Toast.makeText(activity.getApplicationContext(), "Copy is Clicked ", Toast.LENGTH_LONG).show();
                bottomSheetDialog.dismiss();
                //Log.d(TAG,"inner:"+String.valueOf(bottomSheetDialog));
            }
        });
        bottomSheetDialog.show();

    }
}


