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

import idv.example.goodposture.R;

public class MyBodyInfoDetailFragment extends Fragment {
    private AppCompatActivity activity;
    private Toolbar toolBar;
    private TextView tvGender;
    private Button btModifyBodyInfo;

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
        //TODO 串接 DB
        tvGender.append("男");
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