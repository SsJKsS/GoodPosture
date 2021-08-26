package idv.example.goodposture.admin.setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import idv.example.goodposture.R;
import idv.example.goodposture.user.MainActivity;


public class AdminSettingFragment extends Fragment {
    private Button bt_reset_sales;
    private Button bt_aboutus;
    private Button bt_logout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleBtRestSales();
        handleBtAboutus();
        handleBtLogout();
    }

    private void findViews(View view) {
        bt_reset_sales = view.findViewById(R.id.bt_reset_sales);
        bt_aboutus = view.findViewById(R.id.bt_aboutus);
        bt_logout = view.findViewById(R.id.bt_logout);
    }

    private void handleBtRestSales() {
        bt_reset_sales.setOnClickListener(view->{
            // 取得NavController物件
            NavController navController = Navigation.findNavController(view);
            // 跳至頁面
            navController.navigate(R.id.action_adminSettingFragment_to_adminHomeTypeSalesFragment);
        });
    }

    private void handleBtAboutus() {
        bt_aboutus.setOnClickListener(view->{
            // 取得NavController物件
            NavController navController = Navigation.findNavController(view);
            // 跳至頁面
            navController.navigate(R.id.action_adminSettingFragment_to_adminAboutusFragment);
        });
    }

    private void handleBtLogout() {
        bt_logout.setOnClickListener(view->{
            LogoutDialog();
        });
    }

    private void LogoutDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage("是否確定登出");
        alertDialog.setPositiveButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity().getBaseContext(), "未登出", Toast.LENGTH_SHORT).show();

            }
        });
        alertDialog.setNegativeButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("id",1);
                startActivity(intent);

            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}