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
import android.view.Menu;
import android.view.MenuInflater;
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

public class ShoppingListFragment extends Fragment {
    private AppCompatActivity activity;
    private Bundle bundle;  //  從shoppingFragment傳過來的searchText字串

    private Toolbar toolbar;
    private SearchView searchView;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        setHasOptionsMenu(true);
        bundle = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shopping_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);    //取得元件的參考
        handleToolbar();
        handleRecyclerView(bundle.getString("searchText"));
    }

    private void findViews(View view) {
        toolbar = view.findViewById(R.id.tb_shopping);
        recyclerView = view.findViewById(R.id.rv_shopping_list);
    }

    private void handleToolbar() {
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle("");
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
    }

    //覆寫menu選項的監聽
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.menu_toolbar_cart){
            //建立navController
            NavController navController = Navigation.findNavController(toolbar);
            // 跳至shoppingCart頁面
            navController.navigate(R.id.action_shoppingListFragment_to_shoppingCartFragment);
            return true;
        }else if(itemId == R.id.menu_toolbar_search){
            //通過MenuItem.getActionView()方法獲取SearchView
            searchView = (SearchView) item.getActionView();
            handleSearchView();
            return  true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    private void handleSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //提交文字時呼叫
            @Override
            public boolean onQueryTextSubmit(String query) {
                handleRecyclerView(query);
                return false;
            }
            // 文字搜尋框發生變化時呼叫
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void handleRecyclerView(String searchText) {
        List<Product> newListS = changedProductList(searchText);
        if(newListS.size() != 0 ){
            recyclerView.setAdapter(new SearchRecyclerViewAdapter(getContext(), newListS));
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }else{
            NavController navController = Navigation.findNavController(toolbar);
            navController.navigate(R.id.action_shoppingListFragment_to_shoppingNoResultFragment);
        }
    }

    private List<Product> changedProductList(String newText){
        List<Product> listS = getProductList(); //所有資料
        List<Product> newListS = new ArrayList<>();
        for(Product product : listS){
            String name = product.getProductName();
            if(name.contains(newText)){
                newListS.add(product);
            }
        }
        return newListS;
    }

    private static class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.SearchResultListAdapterViewHolder>{

        private Context context;
        private List<Product> list;

        public SearchRecyclerViewAdapter(Context context, List<Product> list) {
            this.context = context;
            this.list = list;
        }
        private static class SearchResultListAdapterViewHolder extends RecyclerView.ViewHolder {
            ImageView ivProduct;
            TextView tvProductName;
            TextView tvProductPrice;

            public SearchResultListAdapterViewHolder(@NonNull View itemView) {
                super(itemView);
                ivProduct = itemView.findViewById(R.id.iv_product);
                tvProductName = itemView.findViewById(R.id.tv_product_name);
                tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            }
        }

        @Override
        public SearchResultListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_shopping_list, parent, false);
            return new SearchResultListAdapterViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull SearchResultListAdapterViewHolder holder, int position) {
            final Product product = list.get(position);
            holder.ivProduct.setImageResource(product.getImageId());
            holder.tvProductName.setText(product.getProductName());
            holder.tvProductPrice.setText("$" + product.getProductPrice());
            holder.itemView.setOnClickListener( v -> {
                //寫帶過去的資料
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", product);
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_shoppingListFragment_to_shoppingProductFragment, bundle);
            });
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
        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號", 400));
        return productsList;
    }


}