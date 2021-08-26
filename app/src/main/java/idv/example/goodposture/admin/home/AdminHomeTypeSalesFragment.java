package idv.example.goodposture.admin.home;

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
import android.widget.EditText;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import idv.example.goodposture.R;

public class AdminHomeTypeSalesFragment extends Fragment {
    private EditText etExpectedSales;
    private TextView tvSales;
    private Button btSubmit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_home_type_sales, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleButton();
    }

    private void findViews(View view) {
        etExpectedSales = view.findViewById(R.id.et_expectedSales);
        tvSales = view.findViewById(R.id.tv_sales);
        btSubmit = view.findViewById(R.id.bt_salesSubmit);
    }

    private void handleButton() {
        // TODO 連接資料庫傳值

        btSubmit.setOnClickListener(view -> {
            final String sales =  String.valueOf(etExpectedSales.getText());
            // 判斷帳號不可為空
            if (sales.isEmpty()) {
                tvSales.setText("未輸入預期銷售額");
            } else {
                // 取得NavController物件
                NavController navController = Navigation.findNavController(view);
                // 跳至頁面
                navController.navigate(R.id.action_adminHomeTypeSalesFragment_to_adminHomeFragment);
            }
        });
    }
}