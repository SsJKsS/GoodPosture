package idv.example.goodposture.user.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import idv.example.goodposture.user.MainActivity;
import idv.example.goodposture.R;

public class HomeTypeBodyInfoFragment extends Fragment {
    private EditText etAge, etHeight, etWeight;
    private Button btSubmit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home_type_body_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleButton();
    }

    private void findViews(View view) {
        etAge = view.findViewById(R.id.et_age);
        etHeight = view.findViewById(R.id.et_height);
        etWeight = view.findViewById(R.id.et_weight);
        btSubmit = view.findViewById(R.id.bt_bodyInfoSubmit);
    }

    private void handleButton() {
        btSubmit.setOnClickListener(view -> {
            final String age = String.valueOf(etAge.getText());
            final String height = String.valueOf(etHeight.getText());
            final String weight = String.valueOf(etWeight.getText());

            // 判斷年齡不可為空
            if (age.isEmpty()) {
                etAge.setError("請輸入年齡");
            }

            // 判斷身高不可為空
            if (height.isEmpty()) {
                etHeight.setError("請輸入身高");
            }

            // 判斷體重不可為空
            if (weight.isEmpty()) {
                etWeight.setError("請輸入體重");
            } else {
                // 實例化Bundle物件
                Bundle bundle = new Bundle();
                // 放入資料
                bundle.putString("age", age);
                bundle.putString("height", height);
                bundle.putString("weight", weight);
                // 跳轉至顯示身體資訊頁面
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}