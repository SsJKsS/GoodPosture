package idv.example.goodposture.admin.commodity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import idv.example.goodposture.R;

public class AdminCommodityContextFragment extends Fragment {
    private static final String TAG = "TAG_AdminCommodityContextFragment";
    private AppCompatActivity activity;

    private Toolbar toolbar;
    private ImageView ivProduct;
    private TextView tvProductName;
    private TextView tvProductPrice;
    private TextView tvProductDesc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (AppCompatActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_commodity_context, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleToolbar();
    }

    private void findViews(View view) {
        toolbar = view.findViewById(R.id.tb_admin_context);
        ivProduct= view.findViewById(R.id.iv_product);
        tvProductName= view.findViewById(R.id.tv_product_name);
        tvProductPrice= view.findViewById(R.id.tv_product_price);
        tvProductDesc= view.findViewById(R.id.tv_product_desc);
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
        inflater.inflate(R.menu.admin_commodity_context_menu, menu);
    }

    //覆寫menu選項的監聽 //返回鑑被視為功能選單的選項
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(toolbar);
        int itemId = item.getItemId();
        if (itemId == R.id.menu_toolbar_edit) {
            //建立navController// 跳至shoppingCart頁面
            navController.navigate(R.id.action_adminCommodityContextFragment_to_adminCommodityAddFragment);
            return true;
        } else if (itemId == android.R.id.home) {
            Navigation.findNavController(toolbar).popBackStack();
            return true;
        }else if(itemId == R.id.menu_toolbar_delete){
            //todo 去資料庫刪除商品
            Navigation.findNavController(toolbar).popBackStack();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}