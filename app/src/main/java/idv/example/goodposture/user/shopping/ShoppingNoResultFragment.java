package idv.example.goodposture.user.shopping;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import idv.example.goodposture.R;

public class ShoppingNoResultFragment extends Fragment {
    private AppCompatActivity activity;
    private Toolbar toolbar;
    private SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (AppCompatActivity) getActivity();   //取得activity參考
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_shooping_no_result, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleToolbar();
    }
    private void findViews(View view) {
        toolbar = view.findViewById(R.id.tb_shopping);
    }

    private void handleToolbar() {
        activity.setSupportActionBar(toolbar);
        //ToolBar的標題預設是AndroidManifest檔案中<Application/>標籤下屬性label設定的值
        toolbar.setTitle("");
    }
    //建立ToolBar的menu選單
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
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
            // // 提交文字時呼叫
            @Override
            public boolean onQueryTextSubmit(String query) {
                //搜尋文字
                Bundle bundle = new Bundle();
                bundle.putString("searchText",query);
                //建立navController
                NavController navController = Navigation.findNavController(toolbar);
                // 跳至頁面
                navController.navigate(R.id.action_shoppingNoResultFragment_to_shoppingListFragment,bundle);
                return false;
            }
            // 文字搜尋框發生變化時呼叫
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}