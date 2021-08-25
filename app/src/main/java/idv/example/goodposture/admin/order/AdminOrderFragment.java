package idv.example.goodposture.admin.order;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import idv.example.goodposture.R;
import idv.example.goodposture.user.forum.ForumBrowseFragment;
import idv.example.goodposture.user.forum.ForumBrowseList;
import idv.example.goodposture.user.forum.ForumContextFragment;
import idv.example.goodposture.user.forum.ForumContextResponseList;

public class AdminOrderFragment extends Fragment {
    private RecyclerView orderRecyclerView;
    protected AppCompatActivity activity;
    private ArrayAdapter<AdminOrderBrowseList> adminOrderBrowseListArrayAdapter;
    private orderAdapter orderAdapter;
    private CardView cv_orderbrowse;
    private SearchView od_searchView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       return inflater.inflate(R.layout.fragment_admin_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleOrderRecycleView();
        handleSearchView();
    }

    private void findViews(View view) {
        orderRecyclerView = view.findViewById(R.id.orderRecyclerView);
        cv_orderbrowse = view.findViewById(R.id.cv_orderbrowse);
        od_searchView = view.findViewById(R.id.od_searchView);
    }

    private void handleSearchView() {
        od_searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                orderAdapter orderAdapter = (orderAdapter) orderRecyclerView.getAdapter();
                if (orderAdapter == null) {
                    return false;
                }

                if (newText.isEmpty()) {
                    orderAdapter.list = getAdminOrderBrowseList();
                } else {
                    List<AdminOrderBrowseList> resultList = new ArrayList<>();
                    for (AdminOrderBrowseList adminOrderBrowseList : orderAdapter.list) {
                        if (adminOrderBrowseList.getOrder().toLowerCase().contains(newText.toLowerCase())) {
                            resultList.add(adminOrderBrowseList);
                        }
                    }
                    orderAdapter.list = resultList;
                }
                orderAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }
    private void handleOrderRecycleView() {
        // 4.2 設定Adapter
        orderRecyclerView.setAdapter(new orderAdapter(this,getAdminOrderBrowseList()));
        // 4.3 設定LayoutManager
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 4.3 設定LayoutManager


        /** 可試試以下2種LayoutManager! */
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 4, RecyclerView.HORIZONTAL, false));
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, RecyclerView.HORIZONTAL));

//        cardView.setOnClickListener(view -> {
//            NavController navController = Navigation.findNavController(view);
//            navController.navigate(R.id.action_forumBrowseFragment_to_forumContextFragment);
//        });


    }
    private static class orderAdapter extends RecyclerView.Adapter<orderAdapter.MyOrderViewHolder> {

        // 3.2 欄位: Context物件、選項資料物件
        private final Context context;
        private List<AdminOrderBrowseList> list;

        // 3.3 建構子: 2個參數(Context型態、選項資料的型態)，用來初始化2欄位
        public orderAdapter(AdminOrderFragment context, List<AdminOrderBrowseList> list) {
            this.context = context.getActivity();
            this.list = list;
        }

        // 3.4 內部類別: 自定義ViewHolder類別
        // 3.4.1 繼承RecyclerView.ViewHolder
        private static class MyOrderViewHolder extends RecyclerView.ViewHolder {
            public View forumBrowseItemView;
            // 3.4.2 欄位: 對應選項容器元件，之內的所有元件
            TextView order;
            TextView date;
            TextView status;
            TextView price;




            // 3.4.3 建構子: 1個參數(View型態)，該參數就是選項容器元件，用來取得各容器元件的參考
            public MyOrderViewHolder(@NonNull View adminOrderBrowseItemView) {
                super(adminOrderBrowseItemView);
                order = adminOrderBrowseItemView.findViewById(R.id.tv_order);
                date = adminOrderBrowseItemView.findViewById(R.id.tv_order_date);
                status = adminOrderBrowseItemView.findViewById(R.id.tv_status);
                price = adminOrderBrowseItemView.findViewById(R.id.tv_price);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 取得NavController物件
                        NavController navController = Navigation.findNavController(view);
                        // 跳至頁面
                        navController.navigate(R.id.action_adminOrderFragment_to_adminOrderDetailsFragment);
                    }
                });

            }
        }

        // 3.5 方法(MyAdapter): 覆寫以下3方法
        // 3.5.1 getItemCount(): 回傳選項數量
        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        // 3.5.2 onCreateViewHolder()
        //  宣告itemView，並載入選項容器元件的外觀
        //  實例化自定義的ViewHolder類別，並回傳
        @NonNull
        @Override
        public orderAdapter.MyOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View forumContextResponseItemView = LayoutInflater.from(context).inflate(R.layout.admin_order_browse_item, parent, false);
            return new orderAdapter.MyOrderViewHolder(forumContextResponseItemView);

        }

        // 3.5.3 onBindViewHolder()
        //  透過ViewHolder物件，將 資料 綁定 至 各元件上
        //  各元件的其他處理，EX.註冊/實作監聽器
        @Override
        public void onBindViewHolder(@NonNull MyOrderViewHolder holder, int position) {
            final AdminOrderBrowseList adminOrderBrowseList = list.get(position);
            holder.order.setText(adminOrderBrowseList.getOrder());
            holder.date.setText(adminOrderBrowseList.getDate());
            holder.status.setText(adminOrderBrowseList.getStatus());
            holder.price.setText(adminOrderBrowseList.getPrice());




//            final String text = position + 1 + ": " + forumBrowseLists.getTitle() ;
//            holder.itemView.setOnClickListener(view -> Toast.makeText(context, text, Toast.LENGTH_SHORT).show());

        }
    }
    private List<AdminOrderBrowseList> getAdminOrderBrowseList() {
        List<AdminOrderBrowseList> adminOrderBrowseLists = new ArrayList<>();
        adminOrderBrowseLists.add(new AdminOrderBrowseList("#D1234567","2021/08/25","配送中","NT,5000"));
        adminOrderBrowseLists.add(new AdminOrderBrowseList("#D1234568","2021/08/25","配送中","NT,5000"));
        adminOrderBrowseLists.add(new AdminOrderBrowseList("#D1234569","2021/08/25","未完成","NT,5000"));
        adminOrderBrowseLists.add(new AdminOrderBrowseList("#D1234563","2021/08/25","已完成","NT,5000"));
        adminOrderBrowseLists.add(new AdminOrderBrowseList("#D1234565","2021/08/25","配送中","NT,5000"));
        return adminOrderBrowseLists;
    }

}