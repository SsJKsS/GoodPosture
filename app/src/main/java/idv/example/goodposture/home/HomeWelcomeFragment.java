package idv.example.goodposture.home;

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
import android.widget.TextView;
import android.widget.Toast;

import idv.example.goodposture.R;

public class HomeWelcomeFragment extends Fragment {
    private Button btLogin;
    private TextView tvRegister;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleButton();
    }

    private void findViews(View view) {
        btLogin = view.findViewById(R.id.bt_welcomeLogin);
        tvRegister = view.findViewById(R.id.tv_register);
    }

    private void handleButton() {
        tvRegister.setOnClickListener(view -> {
            /**
             * Intent 為 Activity 和 Activity 之間跳轉的方法，如果要用 Fragment 跳轉到 Activity，
             * 需先將該 Fragment 藉由 getActivity() 來轉換為 Activity
             */
            // 取得NavController物件
            NavController navController = Navigation.findNavController(view);
            // 跳至頁面
            navController.navigate(R.id.actionWelcomeToRegister);
        });

        btLogin.setOnClickListener(view -> {
            // 取得NavController物件
            NavController navController = Navigation.findNavController(view);
            // 跳至頁面
            navController.navigate(R.id.actionWelcomeToLogin);
        });

    }

}