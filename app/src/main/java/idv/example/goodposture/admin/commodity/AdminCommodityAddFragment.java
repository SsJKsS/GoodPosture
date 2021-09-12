package idv.example.goodposture.admin.commodity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.firestore.ThrowOnExtraProperties;

import idv.example.goodposture.R;

public class AdminCommodityAddFragment extends Fragment {
    private static final String TAG = "TAG_AdminCommodityAddFragment";
    private AppCompatActivity activity;

    private ImageView ivBack;
    private TextView tvSend;
    private EditText etName;
    private RadioButton radioFood;
    private RadioButton radioEquipment;
    private EditText etPrice;
    private EditText etStock;
    private EditText etDescription;
    private ImageView ivPicture;
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
        ivBack = view.findViewById(R.id.iv_com_back);
        tvSend = view.findViewById(R.id.tv_com_send);
        etName = view.findViewById(R.id.et_com_name);
        radioFood = view.findViewById(R.id.radio_food);
        radioEquipment = view.findViewById(R.id.radio_equipment);
        etPrice = view.findViewById(R.id.et_com_price);
        etStock = view.findViewById(R.id.et_com_stock);
        etDescription = view.findViewById(R.id.et_com_description);
        ivPicture = view.findViewById(R.id.iv_com_picture);
    }

    private void handleButton() {
        tvSend.setOnClickListener(view->{
//            final String title = String.valueOf(et_com_title.getText());
//            final String price = String.valueOf(et_com_price.getText());
//            final String remain = String.valueOf(et_com_remain.getText());
//            final String describe = String.valueOf(et_com_describe.getText());
//
//            if (title.isEmpty()){
//                et_com_title.setError("請輸入標題");
//            }
//            if (price.isEmpty()){
//                et_com_price.setError("請輸入價格");
//            }
//            if (remain.isEmpty()){
//                et_com_remain.setError("請輸入剩餘數量");
//            }
//            if (describe.isEmpty()){
//                et_com_describe.setError("請輸入商品描述");
//            }
//            if(!title.isEmpty() && !remain.isEmpty() && !price.isEmpty() && !describe.isEmpty()){
//                // 取得NavController物件
//                //NavController navController = Navigation.findNavController(view);
//                // 跳至頁面
//                //navController.navigate(R.id.action_adminCommodityAddFragment_to_adminCommodityContextFragment);
//            }
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_adminCommodityAddFragment_to_adminCommodityFragment);

        });

        ivBack.setOnClickListener(view->{
            //dialog
            Navigation.findNavController(ivBack).popBackStack();
        });
    }

}