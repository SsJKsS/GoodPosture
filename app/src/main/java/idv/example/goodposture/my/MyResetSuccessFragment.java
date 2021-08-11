package idv.example.goodposture.my;

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

import idv.example.goodposture.R;

public class MyResetSuccessFragment extends Fragment {
    private Button btResetSuccess;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_reset_success, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleButton();
    }

    private void findViews(View view) {
        btResetSuccess = view.findViewById(R.id.bt_resetSuccess);
    }

    private void handleButton() {
        btResetSuccess.setOnClickListener(view -> {
            // 取得NavController物件
            NavController navController = Navigation.findNavController(view);
            // 跳至頁面
            navController.navigate(R.id.actionSuccessToMy);
        });
    }
}