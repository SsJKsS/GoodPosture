package idv.example.goodposture.user.home;

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
import android.widget.ImageView;
import android.widget.Toast;

import idv.example.goodposture.R;
import idv.example.goodposture.admin.AdminActivity;

public class HomeLoginFragment extends Fragment {
    private EditText etAccount, etPassword;
    private ImageView ivPerson, ivKey;
    private Button btLogin;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleImage();
        handleButton();
    }

    private void findViews(View view) {
        ivPerson = view.findViewById(R.id.iv_person);
        ivKey = view.findViewById(R.id.iv_key);
        etAccount = view.findViewById(R.id.et_loginAccount);
        etPassword = view.findViewById(R.id.et_loginPassword);
        btLogin = view.findViewById(R.id.bt_login);
    }

    private void handleImage() {
        ivPerson.setOnClickListener(view -> {
            etAccount.setText("s");
            etPassword.setText("s");
        });

        ivKey.setOnClickListener(view -> {
            etAccount.setText("a");
            etPassword.setText("a");
        });
    }

    private void handleButton() {
        /**
         * Intent 為 Activity 和 Activity 之間跳轉的方法，如果要用 Fragment 跳轉到 Activity，
         * 需先將該 Fragment 藉由 getActivity() 來轉換為 Activity
         */
        btLogin.setOnClickListener(view -> {
            final String account =  String.valueOf(etAccount.getText());
            final String password = String.valueOf(etPassword.getText());

            if ("a".equals(account) && "a".equals(password)) {
                // 跳轉至廠商首頁
                Intent intent = new Intent(getActivity(), AdminActivity.class);
                startActivity(intent);
            }

            if ("s".equals(account) && "s".equals(password)) {
                // 取得NavController物件
                NavController navController = Navigation.findNavController(view);
                // 跳至頁面
                navController.navigate(R.id.actionLoginToTypeBodyInfo);
            }

            // 判斷帳號不可為空
            if (account.isEmpty()) {
                etAccount.setError("請輸入帳號");
            }

            // 判斷密碼不可為空
            if (password.isEmpty()) {
                etPassword.setError("請輸入密碼");
            }
        });
    }

}