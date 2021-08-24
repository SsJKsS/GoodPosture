package idv.example.goodposture.user.shopping;

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
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import idv.example.goodposture.R;

public class ShoppingPayResultFragment extends Fragment {
    private TextView tvPayResult;
    private Button btPayBack;
    boolean isPayFail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shopping_pay_result, container, false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        isPayFail = false;
        showResult(isPayFail);
        handleBtPayBack(isPayFail);
    }

    private void findViews(View view) {
        tvPayResult = view.findViewById(R.id.tv_pay_result);
        btPayBack = view.findViewById(R.id.bt_pay_back);
    }

    private void showResult(boolean isPayFail) {
        if(isPayFail){
            tvPayResult.setText("付款失敗");
            btPayBack.setText("返回下單頁");
        }else{
            tvPayResult.setText("付款成功");
            btPayBack.setText("返回購物頁");
        }
    }

    private void handleBtPayBack(boolean isPayFail) {
        btPayBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(btPayBack);
            if(isPayFail){
                navController.navigate(R.id.action_shoppingPayResultFragment_to_shoppingOrderFragment);
            }else{
                navController.navigate(R.id.action_shoppingPayResultFragment_to_shoppingFragment);
            }
        });
    }

}