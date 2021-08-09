package idv.example.goodposture.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Objects;

import idv.example.goodposture.MainActivity;
import idv.example.goodposture.PreActivity;
import idv.example.goodposture.R;

public class HomeDisplayBodyInfoFragment extends Fragment {
    private TextView tvDisplayBodyStatus, tvDisplayBMI, tvDisplayBMR;
    private Button btModifyBodyInfo, btBuyNow, btDiscussNow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        btModifyBodyInfo = view.findViewById(R.id.bt_modifyBodyInfo);
        btBuyNow = view.findViewById(R.id.bt_buyNow);
        btDiscussNow = view.findViewById(R.id.bt_discussNow);
    }

    private void handleView() {
        Intent intent = requireActivity().getIntent();
        Bundle bundle = intent.getExtras();
        String age = bundle.getString("age");
        Double height = Double.parseDouble(bundle.getString("height"));
        Double weight = Double.parseDouble(bundle.getString("weight"));
        double bmi = weight / ((height / 100) * (height / 100));
        DecimalFormat df = new DecimalFormat("#.#");  // 設定小數點後位數
        tvDisplayBodyStatus.append(" 適中 ( " + age + " 歲)");
        tvDisplayBMI.append(" " + Double.parseDouble(df.format(bmi)));
        tvDisplayBMR.append(" 2000");
    }

    private void handleButton() {
        btModifyBodyInfo.setOnClickListener(view -> {
            // 取得NavController物件
            NavController navController = Navigation.findNavController(view);
            // 跳至修改身體資訊頁
            navController.navigate(R.id.actionDisplayToModify);
        });

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
            navController.navigate(R.id.actionDisplayToForum);
        });
    }
}