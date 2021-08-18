package idv.example.goodposture.user.shopping;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private Bundle bundle;

    private Toolbar toolbar;
    private SearchView searchView;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (AppCompatActivity) getActivity();
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_shopping_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bundle = getArguments();
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
            //navController.navigate(R.id.action_fragmentShopping_to_shoppingListFragment,bundle);
            return true;
        }else if(itemId == R.id.menu_toolbar_search){
            //通過MenuItemCompat.getActionView()方法獲取SearchView
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
            ImageView imageView;
            TextView textView;

            public SearchResultListAdapterViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.iv_product);
                textView = itemView.findViewById(R.id.tv_product_name);
            }
        }

        @Override
        public SearchResultListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_productlist_card, parent, false);
            return new SearchResultListAdapterViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull SearchResultListAdapterViewHolder holder, int position) {
            final Product product = list.get(position);
            holder.imageView.setImageResource(product.getImageId());
            holder.textView.setText(product.getProductName());
        }


        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }


    }

    private List<Product> getProductList() {
        List<Product> productsList = new ArrayList<>();
        productsList.add(new Product(R.drawable.shopping_cat, "a貓咪一號"));
        productsList.add(new Product(R.drawable.shopping_cat2, "b貓咪二號"));
        productsList.add(new Product(R.drawable.shopping_cat3, "b貓咪三號"));
        return productsList;
    }


}