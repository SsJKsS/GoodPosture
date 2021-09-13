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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import idv.example.goodposture.R;
import idv.example.goodposture.user.shopping.Product;
import idv.example.goodposture.user.shopping.ShoppingListFragment;
import idv.example.goodposture.user.shopping.ShoppingOrderFragment;

public class MyOrderStateFragment extends Fragment {
    private static final String TAG = "TAG_MyOrderStateFragment";
    private AppCompatActivity activity;
    private RecyclerView rvMyOrder;
    int orderState;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration registration;  //全程監控db的資料，所以db一變動就會更新資料
    private List<Order> orders;
    //private final List<Order> allOrderList = TestData.allOrderList;

    public MyOrderStateFragment() {
    }

    //產生不同訂單狀態頁面的訂單狀態建構子
    public MyOrderStateFragment(int orderState) {
        this.orderState = orderState;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(TAG, "onCreate " + Order.getOrderStateName(orderState));
        activity = (AppCompatActivity) getActivity();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        orders = new ArrayList<>();
        listenToOrders();   // 加上異動監聽器
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
        Log.d(TAG, "onViewCreated " + Order.getOrderStateName(orderState));
    }

    @Override
    public void onStart() {
        super.onStart();
        //Log.d(TAG, "onStart " + Order.getOrderStateName(orderState));
        reloadOrders(); //  重新回到這個頁面，會重新載入頁面
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.d(TAG, "onResume " + Order.getOrderStateName(orderState));
    }

    @Override
    public void onPause() {
        super.onPause();
        //Log.d(TAG, "onPause  " + Order.getOrderStateName(orderState));
    }

    @Override
    public void onStop() {
        super.onStop();
        //Log.d(TAG, "onStop  " + Order.getOrderStateName(orderState));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Log.d(TAG, "onDestroyView " + Order.getOrderStateName(orderState));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 解除異動監聽器
        if (registration != null) {
            registration.remove();
            registration = null;
        }
        //Log.d(TAG, "onDestroy " + Order.getOrderStateName(orderState));
    }

    private void findViews(View view) {
        rvMyOrder = view.findViewById(R.id.rv_my_order);
        rvMyOrder.setLayoutManager(new LinearLayoutManager(activity));
    }

    //重新載入清單
    private void reloadOrders() {
        db.collection("order")
                .whereEqualTo("uid", auth.getCurrentUser().getUid())
                .whereEqualTo("orderState", orderState)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // 先清除舊資料後再儲存新資料
                        if (!orders.isEmpty()) {
                            orders.clear();
                        }
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            orders.add(document.toObject(Order.class));
                        }
                        showOrders();
                    } else {
                        String message = task.getException() == null ?
                                "No Product found" :
                                task.getException().getMessage();
                        Log.e(TAG, "exception message: " + message);
                    }
                });
    }

    //顯示訂單列表
    //設定rv的adapter和吃的list
    private void showOrders() {
        OrderAdapter orderAdapter = (OrderAdapter) rvMyOrder.getAdapter();
        if (orderAdapter == null) {
            orderAdapter = new OrderAdapter();
            rvMyOrder.setAdapter(orderAdapter);
        }
        //Log.d(TAG, "showOrder 的 orders 大小：" + orders.size());
        orderAdapter.setOrders(orders);
        orderAdapter.notifyDataSetChanged();
    }


    private class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
        private List<Order> orders;

        public OrderAdapter() {
        }

        public void setOrders(List<Order> orders) {
            this.orders = orders;
        }

        private class OrderViewHolder extends RecyclerView.ViewHolder {
            TextView tvOrderId;
            ImageView ivMyOrder;
            TextView tvMyOrderDate;
            TextView tvMyOrderAmount;

            public OrderViewHolder(View itemView) {
                super(itemView);
                tvOrderId = itemView.findViewById(R.id.tv_order_id);
                ivMyOrder = itemView.findViewById(R.id.iv_my_order);
                tvMyOrderDate = itemView.findViewById(R.id.tv_my_order_date);
                tvMyOrderAmount = itemView.findViewById(R.id.tv_my_order_amount);
            }
        }

        @Override
        public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_my_order, parent, false);
            return new OrderViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(OrderViewHolder holder, int position) {
            Order order = orders.get(position);
            holder.tvOrderId.setText(order.getId());
            holder.ivMyOrder.setImageResource(R.drawable.ic_baseline_library_books_24);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            holder.tvMyOrderDate.setText(sdf.format(order.getOrderTime()));
            holder.tvMyOrderAmount.setText("$" + order.getOrderAmount());
            holder.itemView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("order", order);
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_myOrderFragment_to_myOrderDetailFragment, bundle);
            });
            //Log.d(TAG, Order.getOrderStateName(order.getOrderState()));
        }

        @Override
        public int getItemCount() {
            return orders == null ? 0 : orders.size();
        }
    }

    /**
     * 監聽資料是否發生異動，有則同步更新。
     * 開啟2台模擬器，一台新增/修改/刪除；另一台畫面會同步更新
     * 但自己做資料異動也會觸發監聽器
     */
    private void listenToOrders() {
        if (registration == null) {
            //監聽到這個表有異動
            registration = db.collection("order").addSnapshotListener((snapshots, e) -> {
                Log.d(TAG, "event happened");
                if (e == null) {
                    List<Order> orders = new ArrayList<>();
                    if (snapshots != null) {
                        //取得發生異動的資料
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            Order order = dc.getDocument().toObject(Order.class);
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "Added order: " + order.getId());
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified order: " + order.getId());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed order: " + order.getId());
                                    break;
                                default:
                                    break;
                            }
                        }

                        for (DocumentSnapshot document : snapshots.getDocuments()) {
                            Order o = document.toObject(Order.class);
                            assert o != null;
                            if (o.getUid().equals(auth.getCurrentUser().getUid())) {
                                if (o.getOrderState() == orderState) {
                                    orders.add(o);
                                }
                            }

                        }
                        this.orders = orders;
                        showOrders();
                    }
                } else {
                    Log.e(TAG, e.getMessage(), e);
                }
            });
        }
    }

}