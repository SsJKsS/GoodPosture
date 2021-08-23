package idv.example.goodposture.user.shopping;

import android.content.Context;
import android.os.Bundle;

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

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import idv.example.goodposture.R;

public class ShoppingOrderFragment extends Fragment {
    private AppCompatActivity activity;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        setHasOptionsMenu(true);
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
    }

    private void findViews(View view) {
        toolbar = view.findViewById(R.id.tb_shopping);
        recyclerView = view.findViewById(R.id.rv_shopping_order_detail);
    }

    private void handleToolbar() {
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle("");
        activity.setSupportActionBar(toolbar);
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
        recyclerView.setAdapter(new shoppingOrderRVAdapter(getContext(), getProductList()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private static class shoppingOrderRVAdapter extends RecyclerView.Adapter<shoppingOrderRVAdapter.shoppingOrderViewHolder>{

        private Context context;
        private List<Product> list;

        public shoppingOrderRVAdapter(Context context, List<Product> list) {
            this.context = context;
            this.list = list;
        }
        private static class shoppingOrderViewHolder extends RecyclerView.ViewHolder {
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
            holder.ivProduct.setImageResource(product.getImageId());
            holder.tvProductName.setText(product.getProductName());
            holder.tvProductPrice.setText("$" + product.getProductPrice());
            //holder.tvProductNumber.setText("x" + );
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }
    }

    private List<Product> getProductList() {
        List<Product> productsList = new ArrayList<>();
        productsList.add(new Product(R.drawable.shopping_cat, "a貓咪一號", 200));
        productsList.add(new Product(R.drawable.shopping_cat2, "b貓咪二號", 300));
        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
        return productsList;
    }
}