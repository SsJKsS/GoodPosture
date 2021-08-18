package idv.example.goodposture.user.home;

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

import idv.example.goodposture.R;

public class HomeRegisterFragment extends Fragment {
    private EditText etAccount;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btRegister;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleButton();
    }

    private void findViews(View view) {
        etAccount = view.findViewById(R.id.et_registerAccount);
        etPassword = view.findViewById(R.id.et_registerPassword);
        etConfirmPassword = view.findViewById(R.id.et_confirmPassword);
        btRegister = view.findViewById(R.id.bt_register);
    }

    private void handleButton() {
        btRegister.setOnClickListener(view -> {
            final String account =  String.valueOf(etAccount.getText());
            final String password = String.valueOf(etPassword.getText());
            final String confirmPassword = String.valueOf(etConfirmPassword.getText());
            if ("a".equals(account) && "a".equals(password) && "a".equals(confirmPassword)) {
                // 取得NavController物件
                NavController navController = Navigation.findNavController(view);
                // 跳至頁面
                navController.navigate(R.id.actionRegisterToRegisterSuccess);
            }
            else if ("z".equals(account)) {
                etAccount.setError("此帳號已註冊");
            }

            // 判斷帳號不可為空
            if (account.isEmpty()) {
                etAccount.setError("請輸入帳號");
            }

            // 判斷密碼不可為空
            if (password.isEmpty()) {
                etPassword.setError("請輸入密碼");
            }

            // 判斷確認密碼不可為空
            if (confirmPassword.isEmpty()) {
                etConfirmPassword.setError("請輸入確認密碼");
            }

        });

    }

}