package idv.example.goodposture.user.my;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import idv.example.goodposture.R;
import idv.example.goodposture.user.MainActivity;
import idv.example.goodposture.user.PreActivity;

public class MyFragment extends Fragment {
    private Button btMyBodyInfo, btMyCalorieRecord;
    private Button btMyOrder, btMyAccountData, btAbout, btLogout;
    private FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleButton();
    }

    private void findViews(View view) {
        btMyBodyInfo = view.findViewById(R.id.bt_myBodyInfo);
        btMyCalorieRecord = view.findViewById(R.id.bt_myCalorieRecord);
        btMyOrder = view.findViewById(R.id.bt_myOrder);
        btMyAccountData = view.findViewById(R.id.bt_myAccountData);
        btAbout = view.findViewById(R.id.bt_about);
        btLogout = view.findViewById(R.id.bt_logout);
    }

    private void handleButton() {
        btMyBodyInfo.setOnClickListener(view -> {
            // 取得NavController物件
            NavController navController = Navigation.findNavController(view);
            // 跳至頁面
            navController.navigate(R.id.action_fragmentMy_to_myBodyInfoDetailFragment);
        });

        btMyCalorieRecord.setOnClickListener(view -> {
            // 取得NavController物件
            NavController navController = Navigation.findNavController(view);
            // 跳至頁面
            navController.navigate(R.id.action_fragmentMy_to_myRecordIndexFragment);
        });

        btMyOrder.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_fragmentMy_to_myOrderFragment);
        });

        btMyAccountData.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_fragmentMy_to_myAccountFragment);
        });

        btAbout.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_fragmentMy_to_myAboutFragment);
        });

        btLogout.setOnClickListener(view -> {
            Logout();
        });
    }

    private void Logout(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage("是否確定登出");
        alertDialog.setPositiveButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.setNegativeButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 登出
                auth.signOut();
                // 登出後跳轉至歡迎頁
                Intent intent = new Intent(getActivity(), PreActivity.class);
                startActivity(intent);
            }
        });
        alertDialog.setCancelable(false);   // disable click other area
        alertDialog.show();
    }
}