package idv.example.goodposture.admin.setting;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import idv.example.goodposture.R;

public class AdminAboutusFragment extends Fragment {
    private ImageView iv_aboutus_back;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_aboutus, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleBt();
    }

    private void findViews(View view) {
        iv_aboutus_back = view.findViewById(R.id.iv_aboutus_back);
    }

    private void handleBt() {
        iv_aboutus_back.setOnClickListener(view->{
            // 取得NavController物件
            NavController navController = Navigation.findNavController(view);
            // 跳至頁面
            navController.navigate(R.id.action_adminAboutusFragment_to_adminSettingFragment);
        });
    }
}