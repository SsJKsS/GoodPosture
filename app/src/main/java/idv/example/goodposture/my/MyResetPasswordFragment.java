package idv.example.goodposture.my;

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

import org.jetbrains.annotations.NotNull;

import idv.example.goodposture.R;

public class MyResetPasswordFragment extends Fragment {
    private AppCompatActivity activity;
    private Toolbar toolBar;
    private EditText etOldPassword, etNewPassword, etConfirmNewPassword;
    private Button btReset;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (AppCompatActivity) getActivity();
        // 設定允許Fragment有功能選單
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_my_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleToolbar();
        handleButton();
    }

    private void findViews(View view) {
        toolBar = view.findViewById(R.id.toolbar);
        etOldPassword = view.findViewById(R.id.et_oldPassword);
        etNewPassword = view.findViewById(R.id.et_newPassword);
        etConfirmNewPassword = view.findViewById(R.id.et_confirmNewPassword);
        btReset = view.findViewById(R.id.bt_reset);
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

    private void handleButton() {
        btReset.setOnClickListener(view -> {
            final String oldPassword = String.valueOf(etOldPassword.getText());
            final String newPassword = String.valueOf(etNewPassword.getText());
            final String confirmNewPassword = String.valueOf(etConfirmNewPassword.getText());

            // 判斷新密碼不可和舊密碼相同
            if (oldPassword.equals(newPassword)) {
                etOldPassword.setError("新密碼不可和舊密碼相同");
                etNewPassword.setError("新密碼不可和舊密碼相同");
            }

            // 判斷新密碼和確認新密碼不相同
            if (!newPassword.equals(confirmNewPassword)) {
                etConfirmNewPassword.setError("新密碼和確認新密碼不相同");
            }

            // 判斷舊密碼不可為空
            if (oldPassword.isEmpty()) {
                etOldPassword.setError("請輸入舊密碼");
            }

            // 判斷新密碼不可為空
            if (newPassword.isEmpty()) {
                etNewPassword.setError("請輸入新密碼");
            }

            // 判斷確認新密碼不可為空
            if (confirmNewPassword.isEmpty()) {
                etConfirmNewPassword.setError("請輸入確認新密碼");
            }

            else if (!oldPassword.equals(newPassword) && newPassword.equals(confirmNewPassword)){
                // 取得NavController物件
                NavController navController = Navigation.findNavController(view);
                // 跳至頁面
                navController.navigate(R.id.actionResetToSuccess);
            }
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
            Navigation.findNavController(etOldPassword).popBackStack();
        }
        return true;
    }
}