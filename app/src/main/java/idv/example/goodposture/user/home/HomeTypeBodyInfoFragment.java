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
        // ?????????????????????
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

            // ????????????????????????
            if (age.isEmpty()) {
                etAge.setError("???????????????");
            }

            // ????????????????????????
            if (height.isEmpty()) {
                etHeight.setError("???????????????");
            }

            // ????????????????????????
            if (weight.isEmpty()) {
                etWeight.setError("???????????????");
            } else {
                // ????????? ID (UID) ?????? ID
                bodyinfo.setId(auth.getCurrentUser().getUid());
                // ????????????
                bodyinfo.setAge(age);
                bodyinfo.setHeight(height);
                bodyinfo.setWeight(weight);
                add(bodyinfo);
            }
        });
    }

    private void add(Bodyinfo bodyinfo) {
        // ??????Firestore?????????ID???Document??????????????????????????????????????????
        db.collection("body_info").document(bodyinfo.getId()).set(bodyinfo)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String message = "???????????? with ID: " + bodyinfo.getId();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        // ?????????????????????????????????????????????
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        String message = task.getException() == null ?
                                "????????????" : task.getException().getMessage();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}