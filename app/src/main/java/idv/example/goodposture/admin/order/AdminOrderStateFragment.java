package idv.example.goodposture.admin.order;

import android.os.Bundle;

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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import idv.example.goodposture.R;
import idv.example.goodposture.user.my.MyOrderStateFragment;
import idv.example.goodposture.user.my.Order;

public class AdminOrderStateFragment extends Fragment {
    private static final String TAG = "TAG_AdminOrderStateFragment";
    private AppCompatActivity activity;
    private RecyclerView rv_admin_order;
    int adminOrderState;
    private FirebaseFirestore db;
    private ListenerRegistration registration;
    private FirebaseAuth auth;
    private List<Order> orders;

    public AdminOrderStateFragment(){
    }

    public AdminOrderStateFragment(int adminOrderState){
        this.adminOrderState = adminOrderState;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        orders = new ArrayList<>();
        listenToOrders();   // 加上異動監聽器
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_order_state, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
    }

    private void findViews(View view) {
        rv_admin_order = view.findViewById(R.id.rv_admin_order);
        rv_admin_order.setLayoutManager(new LinearLayoutManager(activity));
    }

    @Override
    public void onStart() {
        super.onStart();
        reloadOrders();
    }

    private void reloadOrders(){
        db.collection("order")
                .whereEqualTo("orderState", adminOrderState)
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
                    }else{
                        String message = task.getException() == null ?
                                "No Product found" :
                                task.getException().getMessage();
                        Log.e(TAG, "exception message: " + message);
                    }
                });
    }

    private void showOrders() {
        AdminOrderAdapter adminOrderAdapter = (AdminOrderAdapter) rv_admin_order.getAdapter();
        if (adminOrderAdapter == null){
            adminOrderAdapter = new AdminOrderAdapter();
            rv_admin_order.setAdapter(adminOrderAdapter);
        }
        adminOrderAdapter.setOrders(orders);
        adminOrderAdapter.notifyDataSetChanged();
    }


    private class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.AdminOrderViewHolder>{
        private List<Order> orders;

        public AdminOrderAdapter(){
        }

        private void setOrders(List<Order> orders){
            this.orders = orders;
        }

        private class AdminOrderViewHolder extends RecyclerView.ViewHolder{
            TextView tv_admin_order_id;
            TextView tv_admin_order_date;
            TextView tv_admin_order_amount;

            public AdminOrderViewHolder(View itemView){
                super(itemView);
                tv_admin_order_id = itemView.findViewById(R.id.tv_admin_order_id);
                tv_admin_order_date = itemView.findViewById(R.id.tv_admin_order_date);
                tv_admin_order_amount = itemView.findViewById(R.id.tv_admin_order_amount);
            }
        }

        @Override
        public AdminOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_admin_order, parent, false);
            return new AdminOrderViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(AdminOrderViewHolder holder, int position) {
            Order order = orders.get(position);
            holder.tv_admin_order_id.setText(order.getId());
            holder.tv_admin_order_date.setText(order.getOrderTime() + "");
            holder.tv_admin_order_amount.setText("$" + order.getOrderAmount());
            holder.itemView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("order", order);
                Log.d(TAG,"order : "+ order.getId());
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_adminOrderFragment_to_adminOrderDetailsFragment, bundle);
            });
        }

        @Override
        public int getItemCount() {
            return orders == null ? 0 : orders.size();
        }
    }

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
                            if (o.getOrderState() == adminOrderState){
                                orders.add(o);
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