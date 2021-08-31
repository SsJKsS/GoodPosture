package idv.example.goodposture.user.my;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import idv.example.goodposture.R;

public class MyPersonalInfoFragmentEdit extends Fragment {
    private AppCompatActivity activity;
    private Toolbar toolbar;
    private TextView tvAccountInfo, tvAccountPassword, tvAccountLogout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_personal_info_edit, container, false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleToolbar();
        //handleTv();
    }

    private void findViews(View view) {
        toolbar = view.findViewById(R.id.tb_my);
//        tvAccountInfo = view.findViewById(R.id.tv_account_info);
//        tvAccountPassword = view.findViewById(R.id.tv_account_password);
//        tvAccountLogout = view.findViewById(R.id.tv_account_logout);
    }

    //顯示返回鍵
    private void handleToolbar() {
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle("修改基本資料");
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //覆寫menu選項的監聽
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.my_personal_info_edit){
            NavController navController = Navigation.findNavController(toolbar);
            navController.navigate(R.id.action_fragmentShopping_to_shoppingCartFragment);
            return true;
        }
        else if(itemId == android.R.id.home){
            Navigation.findNavController(toolbar).popBackStack();
        }else{
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void handleTv() {
        tvAccountInfo.setOnClickListener(v -> {

        });
        tvAccountPassword.setOnClickListener( v -> {
            NavController navController = Navigation.findNavController(tvAccountPassword);
            navController.navigate(R.id.action_myAccountFragment_to_myResetPasswordFragment);
        });
        tvAccountLogout.setOnClickListener(v -> {

        });
    }

}