package idv.example.goodposture.admin.setting;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import idv.example.goodposture.R;
import idv.example.goodposture.user.MainActivity;
import idv.example.goodposture.user.home.Bodyinfo;

public class AdminSettingTypeSalesFragment extends Fragment {
    private EditText etExpectedSales;
    private TextView tvSales;
    private Button btSubmit;
    private FirebaseFirestore db;
    private ExpectedSales expectedSales;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        expectedSales = new ExpectedSales();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_setting_type_sales, container, false);
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
        btSubmit.setOnClickListener(view -> {
            final String sales =  String.valueOf(etExpectedSales.getText());
            // 判斷帳號不可為空
            if (sales.isEmpty()) {
                tvSales.setText("未輸入預期銷售額");
            } else {
                int intSales = Integer.parseInt(sales);
                expectedSales.setExpectedSales(intSales);
                add(expectedSales);
            }
        });
    }
    private void add(ExpectedSales expectedSales) {
        db.collection("expected_sales")
                .document("JegPOdvDoarQExD8cu00").set(expectedSales)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String message = "修改成功 ";
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        NavController navController = Navigation.findNavController(etExpectedSales);
                        // 跳至廠商首頁
                        navController.navigate(R.id.action_adminSettingTypeSalesFragment_to_adminHomeFragment);
                    } else {
                        String message = task.getException() == null ?
                                "修改失敗" : task.getException().getMessage();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}