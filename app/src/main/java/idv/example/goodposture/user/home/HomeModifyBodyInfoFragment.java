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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import idv.example.goodposture.R;
import idv.example.goodposture.user.MainActivity;

public class HomeModifyBodyInfoFragment extends Fragment {
    private RadioGroup rgGender;
    private EditText etModifyAge, etModifyHeight, etModifyWeight;
    private Button btModifySubmit;
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

        return inflater.inflate(R.layout.fragment_home_modify_body_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleButton();
    }

    private void findViews(View view) {
        rgGender = view.findViewById(R.id.rg_modifyGender);
        etModifyAge = view.findViewById(R.id.et_modifyAge);
        etModifyHeight = view.findViewById(R.id.et_modifyHeight);
        etModifyWeight = view.findViewById(R.id.et_modifyWeight);
        btModifySubmit = view.findViewById(R.id.bt_modifyBodyInfoSubmit);
    }

    private void handleButton() {
        // ?????????????????????
        rgGender.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_modifyMale) {
                bodyinfo.setGender("male");
            } else {
                bodyinfo.setGender("female");
            }
        });
        btModifySubmit.setOnClickListener(view -> {
            final String age = String.valueOf(etModifyAge.getText());
            final String height = String.valueOf(etModifyHeight.getText());
            final String weight = String.valueOf(etModifyWeight.getText());

            // ????????????????????????
            if (age.isEmpty()) {
                etModifyAge.setError("???????????????");
            }

            // ????????????????????????
            if (height.isEmpty()) {
                etModifyHeight.setError("???????????????");
            }

            // ????????????????????????
            if (weight.isEmpty()) {
                etModifyWeight.setError("???????????????");
            } else {
                // ????????? ID (UID) ?????? ID
                bodyinfo.setId(auth.getCurrentUser().getUid());
                // ????????????
                bodyinfo.setAge(age);
                bodyinfo.setHeight(height);
                bodyinfo.setWeight(weight);
                modify(bodyinfo);
            }
        });
    }

    private void modify(Bodyinfo bodyinfo) {
        // ???????????? ID ?????????
        db.collection("body_info").document(bodyinfo.getId()).set(bodyinfo)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String message = "???????????? with ID: " + bodyinfo.getId();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        // ?????????????????????????????????????????????
                        NavController navController = Navigation.findNavController(rgGender);
                        navController.navigate(R.id.action_homeModifyBodyInfoFragment_to_myBodyInfoDetailFragment);
                    } else {
                        String message = task.getException() == null ?
                                "????????????" : task.getException().getMessage();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}