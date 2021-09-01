package idv.example.goodposture.user.my;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.Objects;

import idv.example.goodposture.R;
import idv.example.goodposture.user.home.Bodyinfo;

public class MyBodyInfoDetailFragment extends Fragment {
    private AppCompatActivity activity;
    private Toolbar toolBar;
    private TextView tvGender, tvAge, tvHeight, tvWeight,
            tvCurrentBodyStatus, tvCurrentBMI, tvCurrentBMR;
    private Button btModifyBodyInfo;
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
        activity = (AppCompatActivity) getActivity();
        // 設定允許Fragment有功能選單
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_my_body_info_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleToolbar();
        handleText();
        handleButton();
    }

    private void findViews(View view) {
        toolBar = view.findViewById(R.id.bodyInfoToolbar);
        tvGender = view.findViewById(R.id.tv_gender);
        tvAge = view.findViewById(R.id.tv_age);
        tvHeight = view.findViewById(R.id.tv_height);
        tvWeight = view.findViewById(R.id.tv_weight);
        tvCurrentBodyStatus = view.findViewById(R.id.tv_currentBodyStatus);
        tvCurrentBMI = view.findViewById(R.id.tv_currentBMI);
        tvCurrentBMR = view.findViewById(R.id.tv_currentBMR);
        btModifyBodyInfo = view.findViewById(R.id.bt_modifyBodyInfo);
    }

    private void handleToolbar() {
        // 設定Toolbar為狀態列
        activity.setSupportActionBar(toolBar);
        // 取得ActionBar
        ActionBar actionBar = activity.getSupportActionBar();
        // 設定顯示返回鍵
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void handleText() {
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
                    } else {
                        String message = task.getException() == null ?
                                "查無資料" :
                                task.getException().getMessage();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showInfo() {
        // 性別
        if (bodyinfo.getGender().equals("male")) {
            tvGender.append(" 男");
        } else {
            tvGender.append(" 女");
        }

        // 年齡、身高、體重
        tvAge.append(" " + bodyinfo.getAge());
        tvHeight.append(" " + bodyinfo.getHeight());
        tvWeight.append(" " + bodyinfo.getWeight());

        // 判斷體態
        Double height = Double.parseDouble(bodyinfo.getHeight());
        Double weight = Double.parseDouble(bodyinfo.getWeight());
        double bmi = weight / ((height / 100) * (height / 100));
        DecimalFormat df = new DecimalFormat("#.#");  // 設定小數點後位數
        if (bmi < 18.5) {
            tvCurrentBodyStatus.append(" 過輕 ");
        } else if (18.5 <= bmi && bmi < 24) {
            tvCurrentBodyStatus.append(" 標準 ");
        } else if (24 <= bmi && bmi < 27) {
            tvCurrentBodyStatus.append(" 過重 ");
        } else {
            tvCurrentBodyStatus.append(" 肥胖 ");
        }
        // BMI
        tvCurrentBMI.append(" " + Double.parseDouble(df.format(bmi)));
        // 判斷 BMR
        // 男生 = 66 + (13.7 × 體重) + (5.0 × 身高) – (6.8 × 年齡)
        // 女生 = 655 + (9.6 × 體重) + (1.8 × 身高) – (4.7 × 年齡)
        Double age = Double.parseDouble(bodyinfo.getAge());
        if (bodyinfo.getGender().equals("male")) {
            double bmrMale = 66 + (13.7 * weight) + (5.0 * height) - (6.8 * age);
            tvCurrentBMR.append(" " + Math.round(bmrMale));
        } else {
            double bmrFemale = 655 + (9.6 * weight) + (1.8 * height) - (4.7 * age);
            tvCurrentBMR.append(" " + Math.round(bmrFemale));
        }
    }

    private void handleButton() {
        btModifyBodyInfo.setOnClickListener(view -> {
            // 取得NavController物件
            NavController navController = Navigation.findNavController(view);
            // 跳至頁面
            navController
                    .navigate(R.id.action_myBodyInfoDetailFragment_to_homeModifyBodyInfoFragment);
        });
    }

    /**
     * 覆寫 功能選單監聽器
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            // 返回鍵的資源ID固定為android.R.id.home
            Navigation.findNavController(btModifyBodyInfo)
                    .navigate(R.id.action_myBodyInfoDetailFragment_to_fragmentMy);
        }
        return true;
    }
}