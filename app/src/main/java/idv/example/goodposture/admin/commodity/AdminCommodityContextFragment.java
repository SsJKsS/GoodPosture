package idv.example.goodposture.admin.commodity;

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
import android.widget.TextView;

import idv.example.goodposture.R;

public class AdminCommodityContextFragment extends Fragment {
    private TextView tv_com_con_edit;
    private TextView tv_com_con_del;
    private ImageView iv_com_con_back;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_commodity_context, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleButtons();
    }

    private void findViews(View view) {
        tv_com_con_edit = view.findViewById(R.id.tv_com_con_edit);
        tv_com_con_del = view.findViewById(R.id.tv_com_con_del);
        iv_com_con_back = view.findViewById(R.id.iv_com_con_back);
    }

    private void handleButtons() {
        tv_com_con_edit.setOnClickListener(view->{
            // 取得NavController物件
            NavController navController = Navigation.findNavController(view);
            // 跳至頁面
            navController.navigate(R.id.action_adminCommodityContextFragment_to_adminCommodityAddFragment);
        });

        tv_com_con_del.setOnClickListener(view->{
            // 取得NavController物件
            NavController navController = Navigation.findNavController(view);
            // 跳至頁面
            navController.navigate(R.id.action_adminCommodityContextFragment_to_adminCommodityFragment);
        });

        iv_com_con_back.setOnClickListener(view->{
            // 取得NavController物件
            NavController navController = Navigation.findNavController(view);
            // 跳至頁面
            navController.navigate(R.id.action_adminCommodityContextFragment_to_adminCommodityFragment);
        });
    }
}