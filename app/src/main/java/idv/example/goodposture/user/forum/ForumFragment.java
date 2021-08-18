package idv.example.goodposture.user.forum;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import idv.example.goodposture.R;

public class ForumFragment extends Fragment {

//    private Resources resources;
    private Toolbar toolbar6;
    private AppCompatActivity activity;
    private Button btBrowse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = (AppCompatActivity) getActivity();
//        resources = getResources();
        // 設定允許Fragment有功能選單
        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_forum, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleToolbar();
//        handleBtBrowse();
    }

    private void handleToolbar() {
        // 設定Toolbar為狀態列
        activity.setSupportActionBar(toolbar6);
        // 取得ActionBar
        ActionBar actionBar = activity.getSupportActionBar();
        // 設定顯示返回鍵
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void findViews(View view) {
        toolbar6 = view.findViewById(R.id.toolbar6);
        btBrowse = view.findViewById(R.id.btBrowse);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull  Menu menu, @NonNull  MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.bottom_nav_menu, menu);
    }

//    private void handleBtBrowse() {
//        // 取得NavController物件
//        // 跳至頁面
//        btBrowse.setOnClickListener(view -> {
//            NavController navController = Navigation.findNavController(view);
//            navController.navigate(R.id.action_fragmentForum_to_forumBrowseFragment3);
//            Toast.makeText(getActivity(), "123123123", Toast.LENGTH_SHORT).show();
//
//        });
//    }
}