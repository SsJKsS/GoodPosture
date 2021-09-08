package idv.example.goodposture.user.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DecimalFormat;
import java.util.Objects;

import idv.example.goodposture.R;

public class HomeDisplayBodyInfoFragment extends Fragment {
    private TextView tvDisplayBodyStatus, tvDisplayBMI, tvDisplayBMR;
    private Button btBuyNow, btDiscussNow;
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

        return inflater.inflate(R.layout.fragment_home_display_body_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleView();
        handleButton();
    }

    private void findViews(View view) {
        tvDisplayBodyStatus = view.findViewById(R.id.tv_displayBodyStatus);
        tvDisplayBMI = view.findViewById(R.id.tv_displayBMI);
        tvDisplayBMR = view.findViewById(R.id.tv_displayBMR);
        btBuyNow = view.findViewById(R.id.bt_buyNow);
        btDiscussNow = view.findViewById(R.id.bt_discussNow);
    }

    private void handleView() {
        /**
         查詢指定文件內的欄位 (EX : uid)
         .whereEqualTo("uid", auth.getCurrentUser().getUid())
         查詢指定文件 (有給值指定，文件 ID 為當下使用者的 UID)
         .document(auth.getCurrentUser().getUid())
         查詢指定文件 (沒給值，系統產生一組隨機字串作為文件 ID)
         .document()
         */
        // 查詢指定集合
        db.collection("body_info")
                .document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                // 獲取資料
                .get()
                // 設置網路傳輸監聽器
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // 將獲取的資料存成自定義類別
                        // for (DocumentSnapshot documentSnapshot : task.getResult())
                        DocumentSnapshot documentSnapshot = task.getResult();
                        bodyinfo = documentSnapshot.toObject(Bodyinfo.class);
                        showInfo();
                    }
                 else{
            String message = task.getException() == null ?
                    "查無資料" :
                    task.getException().getMessage();
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }

    });
}

    private void showInfo() {
        if (bodyinfo.getHeight() == null) {
            tvDisplayBodyStatus.append(" 無資料");
            tvDisplayBMI.append(" 無資料");
            tvDisplayBMR.append(" 無資料");
        } else {
            Double height = Double.parseDouble(bodyinfo.getHeight());
            Double weight = Double.parseDouble(bodyinfo.getWeight());
            double bmi = weight / ((height / 100) * (height / 100));
            DecimalFormat df = new DecimalFormat("#.#");  // 設定小數點後位數
            // 判斷 bmi
            if (bmi < 18.5) {
                tvDisplayBodyStatus.append(" 過輕 ( " + bodyinfo.getAge() + " 歲 )");
            } else if (18.5 <= bmi && bmi < 24) {
                tvDisplayBodyStatus.append(" 標準 ( " + bodyinfo.getAge() + " 歲 )");
            } else if (24 <= bmi && bmi < 27) {
                tvDisplayBodyStatus.append(" 過重 ( " + bodyinfo.getAge() + " 歲 )");
            } else {
                tvDisplayBodyStatus.append(" 肥胖 ( " + bodyinfo.getAge() + " 歲 )");
            }
            tvDisplayBMI.append(" " + Double.parseDouble(df.format(bmi)));
            // 判斷 bmr
            // 男生 = 66 + (13.7 × 體重) + (5.0 × 身高) – (6.8 × 年齡)
            // 女生 = 655 + (9.6 × 體重) + (1.8 × 身高) – (4.7 × 年齡)
            Double age = Double.parseDouble(bodyinfo.getAge());
            if (bodyinfo.getGender().equals("male")) {
                double bmr = 66 + (13.7 * weight) + (5.0 * height) - (6.8 * age);
                String bmrStr = Math.round(bmr) + " (男)";
                tvDisplayBMR.append(" " + bmrStr);
            } else {
                double bmr = 655 + (9.6 * weight) + (1.8 * height) - (4.7 * age);
                String bmrStr = Math.round(bmr) + " (女)";
                tvDisplayBMR.append(" " + bmrStr);
            }
        }
    }

    private void handleButton() {
        btBuyNow.setOnClickListener(view -> {
            // 取得NavController物件
            NavController navController = Navigation.findNavController(view);
            // 跳至購物首頁
            navController.navigate(R.id.actionDisplayToShopping);
        });

        btDiscussNow.setOnClickListener(view -> {
            // 取得NavController物件
            NavController navController = Navigation.findNavController(view);
            // 跳至討論首頁
            navController.navigate(R.id.action_homeDisplayBodyInfoFragment_to_forumBrowseFragment);
        });
    }
}