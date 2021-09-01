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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.ThrowOnExtraProperties;

import idv.example.goodposture.R;

public class AdminCommodityAddFragment extends Fragment {
    private TextView tv_com_add_send;
    private ImageView iv_com_add_back;
    private EditText et_com_title;
    private EditText et_com_price;
    private EditText et_com_remain;
    private EditText et_com_describe;
    private ImageView iv_com_picture;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_commodity_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleButton();
    }

    private void findViews(View view) {
        tv_com_add_send = view.findViewById(R.id.tv_com_add_send);
        iv_com_add_back = view.findViewById(R.id.iv_com_add_back);
        et_com_title = view.findViewById(R.id.et_com_title);
        et_com_price = view.findViewById(R.id.et_com_price);
        et_com_remain = view.findViewById(R.id.et_com_remain);
        et_com_describe = view.findViewById(R.id.et_com_describe);
        iv_com_picture = view.findViewById(R.id.iv_com_picture);
    }

    private void handleButton() {
        tv_com_add_send.setOnClickListener(view->{
            final String title = String.valueOf(et_com_title.getText());
            final String price = String.valueOf(et_com_price.getText());
            final String remain = String.valueOf(et_com_remain.getText());
            final String describe = String.valueOf(et_com_describe.getText());

            if (title.isEmpty()){
                et_com_title.setError("請輸入標題");
            }
            if (price.isEmpty()){
                et_com_price.setError("請輸入價格");
            }
            if (remain.isEmpty()){
                et_com_remain.setError("請輸入剩餘數量");
            }
            if (describe.isEmpty()){
                et_com_describe.setError("請輸入商品描述");
            }
            if(!title.isEmpty() && !remain.isEmpty() && !price.isEmpty() && !describe.isEmpty()){
                // 取得NavController物件
                NavController navController = Navigation.findNavController(view);
                // 跳至頁面
                navController.navigate(R.id.action_adminCommodityAddFragment_to_adminCommodityContextFragment);
            }

        });

        iv_com_add_back.setOnClickListener(view->{
//            // 取得NavController物件
//            NavController navController = Navigation.findNavController(view);
//            // 跳至頁面
//            navController.navigate(R.id.action_adminCommodityAddFragment_to_adminCommodityFragment);
            Navigation.findNavController(iv_com_add_back).popBackStack();
        });
    }

}