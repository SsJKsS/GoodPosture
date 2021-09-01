package idv.example.goodposture.user.shopping;

import android.app.SearchManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.ViewFlipper;

import idv.example.goodposture.R;

public class ShoppingFragment extends Fragment {
    private AppCompatActivity activity;
    private Toolbar toolbar;
    private ViewFlipper vfAd;
    private SearchView searchView;
    int images[] = {R.drawable.shopping_cat, R.drawable.shopping_cat2, R.drawable.shopping_cat3};
    private ImageButton ibFood, ibEquipment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取得activity參考
        activity = (AppCompatActivity) getActivity();
        // 設定允許Fragment有功能選單
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shopping, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleToolbar();
        handleViewFlipper(images);
        handleIbFood();
        handleIbEquipment();
    }

    private void findViews(View view) {
        vfAd = view.findViewById(R.id.vf_ad);
        toolbar = view.findViewById(R.id.tb_shopping);
        ibFood = view.findViewById(R.id.ib_food);
        ibEquipment = view.findViewById(R.id.ib_equipment);
    }

    private void handleToolbar() {
        activity.setSupportActionBar(toolbar);
        //ToolBar的標題預設是AndroidManifest檔案中<Application/>標籤下屬性label設定的值
        toolbar.setTitle("Shopping");
//        ////todo studying the attri!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        activity.getSupportActionBar().setHomeButtonEnabled(false);
    }

    //建立ToolBar的menu選單
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //載入menu
        inflater.inflate(R.menu.shopping_toolbar_menu, menu);
//        MenuItem searchActionMenuItem = menu.findItem(R.id.menu_toolbar_search);
//        searchView = (SearchView) searchActionMenuItem.getActionView();
//        searchView.setIconified(false);
    }

    //覆寫menu選項的監聽
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.menu_toolbar_cart){
            //建立navController
            NavController navController = Navigation.findNavController(toolbar);
            // 跳至shoppingCart頁面
            navController.navigate(R.id.action_fragmentShopping_to_shoppingCartFragment);
            return true;
        }else if(itemId == R.id.menu_toolbar_search){

            //通過MenuItem.getActionView()方法獲取SearchView
            searchView = (SearchView) item.getActionView();
//            searchView.setIconified(false);
//            searchView.setQueryHint("search");
//            searchView.setIconifiedByDefault(true);
//            // Get the SearchView and set the searchable configuration
//            SearchManager searchManager = (SearchManager) activity.getSystemService(getContext().SEARCH_SERVICE);
//            // Assumes current activity is the searchable activity
//            searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
//            // true:icon會顯示在searchview背景
//            searchView.setIconifiedByDefault(true);
            handleSearchView();
            return  true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

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


    private void handleViewFlipper(int[] images) {
        for(int img :images) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(img);
            vfAd.addView(imageView);
        }
        vfAd.setFlipInterval(2000);
        vfAd.setAutoStart(true);
        //animation
        vfAd.setInAnimation(getContext(), android.R.anim.slide_in_left );
        vfAd.setOutAnimation(getContext(),android.R.anim.slide_out_right);

    }
    //顯示食物類型商品
    private void handleIbFood() {
        ibFood.setOnClickListener(v -> {
            Bundle bundle =new Bundle();
            bundle.putString("searchText","a");
            NavController navController = Navigation.findNavController(ibFood);
            // 跳至頁面
            navController.navigate(R.id.action_fragmentShopping_to_shoppingListFragment,bundle);
        });
    }
    //顯示器材類型商品
    private void handleIbEquipment() {
        ibEquipment.setOnClickListener(v -> {

            Bundle bundle =new Bundle();
            bundle.putString("searchText","b");
            NavController navController = Navigation.findNavController(ibEquipment);
            // 跳至頁面
            navController.navigate(R.id.action_fragmentShopping_to_shoppingListFragment,bundle);
        });
    }

}