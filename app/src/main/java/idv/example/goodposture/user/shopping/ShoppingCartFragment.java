package idv.example.goodposture.user.shopping;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import idv.example.goodposture.R;

public class ShoppingCartFragment extends Fragment {
    private AppCompatActivity activity;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private CheckBox cbSelectAll;
    private TextView tvTotalPrice;
    private Button btCartCheckout;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shopping_cart, container, false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleToolbar();
        //handleRecyclerView();
        checkout();
    }



    private void findViews(View view) {
        toolbar = view.findViewById(R.id.tb_shopping);
        recyclerView = view.findViewById(R.id.rv_shopping_cart);
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
    public boolean onOptionsItemSelected(MenuItem item){
        int itemId = item.getItemId();
        if(itemId == android.R.id.home){
            Navigation.findNavController(toolbar).popBackStack();
        }
        return true;
    }

//    private void handleRecyclerView() {
//        CartRecyclerViewAdapter adapter = new CartRecyclerViewAdapter(getContext(), getProductList());
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        cbSelectAll.setOnClickListener(v -> {
//            adapter.selectAllItemView();
//        });
//    }

    private static class CartRecyclerViewAdapter extends RecyclerView.Adapter<CartRecyclerViewAdapter.CartViewHolder>{

        private Context context;
        private List<Product> list;
        //checkbox的Hashmap集合，存放每個位置的itemView的checkbox是否被選取的資料
        private  HashMap<Integer, Boolean> cbMap;

        public CartRecyclerViewAdapter(Context context, List<Product> list) {
            this.context = context;
            this.list = list;
            this.cbMap = new HashMap<>();
            //list有多少條資料就增加多少個checlbox的Hashmap集合
            for (int i = 0; i < list.size(); i++) {
                this.cbMap.put(i, false);
            }
        }

        /**
         * 全選
         */
        public void selectAllItemView() {
            Set<Map.Entry<Integer, Boolean>> entries = cbMap.entrySet();
            boolean ckAll = false;
            for (Map.Entry<Integer, Boolean> entry : entries) {
                Boolean value = entry.getValue();
                if (!value) {
                    ckAll = true;
                    break;
                }
            }
            for (Map.Entry<Integer, Boolean> entry : entries) {
                entry.setValue(ckAll);
            }
            notifyDataSetChanged();
        }
        //定義ViewHolder
        private static class CartViewHolder extends RecyclerView.ViewHolder {
            CheckBox cbCartItem;
            ImageView ivCartItemProduct;
            TextView tvProductName;
            TextView tvProductPrice;
            AmountView amountProduct;

            public CartViewHolder(View itemView) {
                super(itemView);
                cbCartItem = itemView.findViewById(R.id.cb_cart_item);
                ivCartItemProduct = itemView.findViewById(R.id.iv_cart_item_product);
                tvProductName = itemView.findViewById(R.id.tv_product_name);
                tvProductPrice = itemView.findViewById(R.id.tv_product_price);
                amountProduct = itemView.findViewById(R.id.amountview_product);
            }
        }

        @Override
        public CartRecyclerViewAdapter.CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //初始化佈局檔案
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_shopping_cart, parent, false);
            return new CartRecyclerViewAdapter.CartViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(CartRecyclerViewAdapter.CartViewHolder holder, int position) {
            Product product = list.get(position);
//            holder.ivCartItemProduct.setImageResource(product.getImageId());
//            holder.tvProductName.setText(product.getProductName());
//            holder.tvProductPrice.setText("$" + product.getProductPrice());
            holder.ivCartItemProduct.setOnClickListener( v -> {
                //寫帶過去的資料
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", product);
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_shoppingCartFragment_to_shoppingProductFragment, bundle);
            });
            holder.amountProduct.setGoods_storage(50);  //product.getStorage()
            holder.amountProduct.setOnAmountChangeListener(new AmountView.OnAmountChangeListener() {
                @Override
                public void onAmountChange(View view, int amount) {
                    // amount：這筆訂單清單的商品數量
                }
            });
            //checkbox要先設定checked狀態!!!!
            holder.cbCartItem.setChecked(cbMap.get(position));
            //itemView的checkBox被點擊後，更新cbMap資料
            holder.cbCartItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cbMap.put(position, !cbMap.get(position));
                    //重新整理recyclerView
                    notifyDataSetChanged();
                }
            });
        }


        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

    }

    private void checkout() {
        btCartCheckout.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_shoppingCartFragment_to_shoppingOrderFragment);
        });
    }

//    private List<Product> getProductList() {
//        List<Product> productsList = new ArrayList<>();
//        productsList.add(new Product(R.drawable.shopping_cat, "a貓咪一號", 200));
//        productsList.add(new Product(R.drawable.shopping_cat2, "b貓咪二號", 300));
//        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
//        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
//        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
//        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
//        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
//        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
//        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
//        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
//        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
//        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
//        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
//        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
//        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
//        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
//        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
//        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
//        return productsList;
//    }

}