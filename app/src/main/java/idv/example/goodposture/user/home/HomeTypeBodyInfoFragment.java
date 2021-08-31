package idv.example.goodposture.user.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

import idv.example.goodposture.user.MainActivity;
import idv.example.goodposture.R;

public class HomeTypeBodyInfoFragment extends Fragment {
    private TextView tvBodyStatus;
    private RadioGroup rgGender;
    private EditText etAge, etHeight, etWeight;
    private Button btSubmit;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Bodyinfo bodyinfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        bodyinfo = new Bodyinfo();
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
        handleTextView();
        handleButton();
    }

    private void findViews(View view) {
        tvBodyStatus = view.findViewById(R.id.tv_bodyStatus);
        rgGender = view.findViewById(R.id.rg_gender);
        etAge = view.findViewById(R.id.et_age);
        etHeight = view.findViewById(R.id.et_height);
        etWeight = view.findViewById(R.id.et_weight);
        btSubmit = view.findViewById(R.id.bt_bodyInfoSubmit);
    }

    private void handleTextView() {
        tvBodyStatus.setOnClickListener(view -> {
            etAge.setText("1");
            etHeight.setText("1");
            etWeight.setText("1");
        });
    }

    private void handleButton() {
        // 監聽選擇的性別
        rgGender.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_male) {
                bodyinfo.setGender("male");
            }
            else {
                bodyinfo.setGender("female");
            }
        });
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
                // 先取得插入document的ID
                final String id = db.collection("body_info").document().getId();
                bodyinfo.setId(id);
                // 使用者 ID
                bodyinfo.setUid(Objects.requireNonNull(auth.getCurrentUser()).getUid());
                // 身體資訊
                bodyinfo.setAge(age);
                bodyinfo.setHeight(height);
                bodyinfo.setWeight(weight);
                addOrReplace(bodyinfo);
            }
        });
    }

    private void addOrReplace(Bodyinfo bodyinfo) {
        // 如果Firestore沒有該ID的Document就建立新的，已經有就更新內容
        db.collection("body_info").document(bodyinfo.getId()).set(bodyinfo)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String message = "新增成功 with ID: " + bodyinfo.getId();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        // 新增完畢跳轉至顯示身體資訊頁面
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        String message = task.getException() == null ?
                                "新增失敗" : task.getException().getMessage();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}