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

import idv.example.goodposture.R;

public class HomeModifyBodyInfoFragment extends Fragment {
    private EditText etModifyAge, etModifyHeight, etModifyWeight;
    private Button btModifySubmit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home_modify_body_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleButton();
    }

    private void findViews(View view) {
        etModifyAge = view.findViewById(R.id.et_modifyAge);
        etModifyHeight = view.findViewById(R.id.et_modifyHeight);
        etModifyWeight = view.findViewById(R.id.et_modifyWeight);
        btModifySubmit = view.findViewById(R.id.bt_modifyBodyInfoSubmit);
    }

    private void handleButton() {
        btModifySubmit.setOnClickListener(view -> {
            final String age = String.valueOf(etModifyAge.getText());
            final String height = String.valueOf(etModifyHeight.getText());
            final String weight = String.valueOf(etModifyWeight.getText());

            // 判斷年齡不可為空
            if (age.isEmpty()) {
                etModifyAge.setError("請輸入年齡");
            }

            // 判斷身高不可為空
            if (height.isEmpty()) {
                etModifyHeight.setError("請輸入身高");
            }

            // 判斷體重不可為空
            if (weight.isEmpty()) {
                etModifyWeight.setError("請輸入體重");
            } else {
                // 取得NavController物件
                NavController navController = Navigation.findNavController(view);
                // 跳至修改身體資訊頁
                navController.navigate(R.id.action_homeModifyBodyInfoFragment_to_myBodyInfoDetailFragment);
            }
        });
    }
}