package idv.example.goodposture.user.my;

import android.content.Context;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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
        //讀取資料庫order表格的state
        Log.d(TAG,"MyOrderStateFragment's parameter: "+orderState);
        for(Order order : allOrder){
            Log.d(TAG,"order.getOrderState(): "+order.getOrderState());
            if(orderState == order.getOrderState()){
                Log.d(TAG,"orderState: "+orderState);
                showOrderStateList.add(order);
            }
        }
        Log.d(TAG,"new MyOrderStateFragment "+orderState);
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
        //Log.d(TAG,"showMyOrderList"+allOrder);
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
//            holder.tvMyOrderDate.setText("tvMyOrderDate");
//            holder.tvMyOrderAmount.setText("tvMyOrderAmount");
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
        orderList.add(new Order(2,100));
        orderList.add(new Order(3,100));
//        //訂單時間 orderState 數量 隨機產生資料
//        for(int i =0; i <10; i++){
//            Date orderDate = new Date();
//            int state = 0;
//            state = (int)(Math.random()*10);
//            int amount = 0;
//            amount = (int)(Math.random()*1000);
//            orderList.add(new Order(orderDate, state, amount));
//        }
        return orderList;
    }

}