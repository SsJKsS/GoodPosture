package idv.example.goodposture.user.my;

import android.content.Context;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import idv.example.goodposture.R;
import idv.example.goodposture.user.shopping.Product;
import idv.example.goodposture.user.shopping.ShoppingOrderFragment;

public class MyOrderStateFragment extends Fragment {
    private static final String TAG = "TAG_MyOrderStateFragment";
    private AppCompatActivity activity;
    private RecyclerView rvMyOrder;
    private List<Order> allOrder =  getOrderList();
    private List<Order> showOrderStateList = new ArrayList<>();

    public MyOrderStateFragment() {
        // Required empty public constructor
    }

    public MyOrderStateFragment(int orderState) {
        //從order表格所有資料取出指定的orderState的order資料
        for(Order order : allOrder){
            if(orderState == order.getOrderState()){
                showOrderStateList.add(order);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_order_state, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        showMyOrderList();
    }

    private void findViews(View view) {
        rvMyOrder = view.findViewById(R.id.rv_my_order);
    }

    private void showMyOrderList() {
        rvMyOrder.setAdapter(new MyOrderRVAdapter(getContext(), showOrderStateList));
        rvMyOrder.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private static class MyOrderRVAdapter extends RecyclerView.Adapter<MyOrderRVAdapter.MyOrderViewHolder>{

        private Context context;
        private List<Order> list;

        public MyOrderRVAdapter(Context context, List<Order> list) {
            this.context = context;
            this.list = list;
        }
        private static class MyOrderViewHolder extends RecyclerView.ViewHolder {
            ImageView ivMyOrder;
            TextView tvMyOrderDate;
            TextView tvMyOrderAmount;

            public MyOrderViewHolder(View itemView) {
                super(itemView);
                ivMyOrder = itemView.findViewById(R.id.iv_my_order);
                tvMyOrderDate = itemView.findViewById(R.id.tv_my_order_date);
                tvMyOrderAmount = itemView.findViewById(R.id.tv_my_order_amount);
            }
        }

        @NotNull
        @Override
        public MyOrderRVAdapter.MyOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_my_order, parent, false);
            return new MyOrderRVAdapter.MyOrderViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyOrderRVAdapter.MyOrderViewHolder holder, int position) {
            Order order = list.get(position);
            holder.ivMyOrder.setImageResource(R.drawable.ic_baseline_close_24);
            holder.tvMyOrderDate.setText(order.getOrderTime()+"");
            holder.tvMyOrderAmount.setText("$" + order.getOrderAmount());
            holder.itemView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("order", order);
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_myOrderFragment_to_myOrderDetailFragment, bundle);
            });
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }
    }

    //假資料
    private List<Order> getOrderList() {
        List<Order> orderList = new ArrayList<>();
        orderList.add(new Order(1,100));
        orderList.add(new Order(1,100));
        orderList.add(new Order(1,100));
        orderList.add(new Order(1,100));
        orderList.add(new Order(1,100));
        orderList.add(new Order(1,100));
        orderList.add(new Order(1,100));
        orderList.add(new Order(2,100));
        orderList.add(new Order(3,100));
        orderList.add(new Order(2,100));
        orderList.add(new Order(3,100));
        orderList.add(new Order(4,100));
        orderList.add(new Order(4,100));
        return orderList;
    }

}