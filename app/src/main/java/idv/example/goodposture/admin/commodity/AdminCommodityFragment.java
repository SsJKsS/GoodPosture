package idv.example.goodposture.admin.commodity;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import idv.example.goodposture.R;

public class AdminCommodityFragment extends Fragment {
    private TextView tv_com_insert;
    private RecyclerView comRecyclerView;
    private ArrayAdapter<AdminCommodityList> adminCommodityListArrayAdapter;
    private comAdapter comAdapter;
    private CardView cv_com;
    private SearchView sv_com;
    private AppCompatActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_commodity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleComRecyclerView();
        handleSearchView();
        handleButton();
    }

    private void findViews(View view) {
        comRecyclerView = view.findViewById(R.id.rv_com);
        cv_com = view.findViewById(R.id.cv_com);
        sv_com = view.findViewById(R.id.sv_com);
        tv_com_insert = view.findViewById(R.id.tv_com_insert);
    }

    private void handleSearchView() {

        // 註冊/實作 查詢文字監聽器
        sv_com.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 當點擊提交鍵(虛擬鍵盤)時，自動被呼叫
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 當查詢文字改變時，自動被呼叫
            @Override
            public boolean onQueryTextChange(String newText) {
                comAdapter adapter = (comAdapter) comRecyclerView.getAdapter();
                if (adapter == null) {
                    return false;
                }

                if (newText.isEmpty()) {
                    adapter.list = getAdminCommodityList();
                } else {
                    List<AdminCommodityList> resultList = new ArrayList<>();
                    for (AdminCommodityList adminCommodityList : adapter.list) {
                        if (adminCommodityList.getTv_com_name().toLowerCase().contains(newText.toLowerCase())) {
                            resultList.add(adminCommodityList);
                        }
                    }
                    adapter.list = resultList;
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    private void handleComRecyclerView() {
        // 4.2 設定Adapter
        comRecyclerView.setAdapter(new comAdapter(this, getAdminCommodityList()));
        // 4.3 設定LayoutManager
        comRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        /** 可試試以下2種LayoutManager! */
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 4, RecyclerView.HORIZONTAL, false));
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, RecyclerView.HORIZONTAL));

//        cardView.setOnClickListener(view -> {
//            NavController navController = Navigation.findNavController(view);
//            navController.navigate(R.id.action_forumBrowseFragment_to_forumContextFragment);
//        });


    }

    /**
     * 3. 自定義Adapter類別
     * 3.1 繼承RecyclerView.Adapter
     */
    private static class comAdapter extends RecyclerView.Adapter<comAdapter.MyComViewHolder> {
        // 3.2 欄位: Context物件、選項資料物件
        private final Context context;
        private List<AdminCommodityList> list;

        // 3.3 建構子: 2個參數(Context型態、選項資料的型態)，用來初始化2欄位
        public comAdapter(AdminCommodityFragment context, List<AdminCommodityList> list) {
            this.context = context.getActivity();
            this.list = list;
        }

        // 3.4 內部類別: 自定義ViewHolder類別
        // 3.4.1 繼承RecyclerView.ViewHolder
        private static class MyComViewHolder extends RecyclerView.ViewHolder {
            public View adminCommodityList;
            // 3.4.2 欄位: 對應選項容器元件，之內的所有元件
            TextView tv_com_name;
            TextView tv_com_price;
            TextView tv_com_goods;
            TextView tv_com_sold;
            ImageView iv_com;

            // 3.4.3 建構子: 1個參數(View型態)，該參數就是選項容器元件，用來取得各容器元件的參考
            public MyComViewHolder(@NonNull View adminCommodityList) {
                super(adminCommodityList);
                tv_com_goods = adminCommodityList.findViewById(R.id.tv_com_goods);
                tv_com_name = adminCommodityList.findViewById(R.id.tv_com_name);
                tv_com_price = adminCommodityList.findViewById(R.id.tv_com_goods);
                tv_com_sold = adminCommodityList.findViewById(R.id.tv_com_sold);
                iv_com = adminCommodityList.findViewById(R.id.iv_com);


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 取得NavController物件
                        NavController navController = Navigation.findNavController(view);
                        // 跳至頁面
                        navController.navigate(R.id.action_adminCommodityFragment_to_adminCommodityContextFragment);
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
        public comAdapter.MyComViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View adminCommodityList = LayoutInflater.from(context).inflate(R.layout.admin_product_item, parent, false);
            return new comAdapter.MyComViewHolder(adminCommodityList);

        }

        // 3.5.3 onBindViewHolder()
        //  透過ViewHolder物件，將 資料 綁定 至 各元件上
        //  各元件的其他處理，EX.註冊/實作監聽器
        @Override
        public void onBindViewHolder(@NonNull MyComViewHolder holder, int position) {
            final AdminCommodityList adminCommodityList = list.get(position);
            holder.tv_com_name.setText(adminCommodityList.getTv_com_name());
            holder.tv_com_sold.setText(adminCommodityList.getTv_com_sold());
            holder.tv_com_price.setText(adminCommodityList.getTv_com_price());
            holder.tv_com_goods.setText(adminCommodityList.getTv_com_goods());
            holder.iv_com.setImageResource(adminCommodityList.getIv_com());



//            final String text = position + 1 + ": " + forumBrowseLists.getTitle() ;
//            holder.itemView.setOnClickListener(view -> Toast.makeText(context, text, Toast.LENGTH_SHORT).show());

        }
    }
    private List<AdminCommodityList> getAdminCommodityList() {
        List<AdminCommodityList> adminCommodityLists = new ArrayList<>();
        adminCommodityLists.add(new AdminCommodityList(R.drawable.shark,"鯊鯊A","NT1,999","999","10"));
        adminCommodityLists.add(new AdminCommodityList(R.drawable.shark,"鯊鯊B","NT1,999","999","10"));
        adminCommodityLists.add(new AdminCommodityList(R.drawable.shark,"鯊鯊C","NT1,999","999","10"));
        adminCommodityLists.add(new AdminCommodityList(R.drawable.shark,"鯊鯊D","NT1,999","999","10"));
        adminCommodityLists.add(new AdminCommodityList(R.drawable.shark,"鯊鯊E","NT1,999","999","10"));
        return adminCommodityLists;
    }

    private void handleButton() {
        tv_com_insert.setOnClickListener(view->{
            // 取得NavController物件
            NavController navController = Navigation.findNavController(view);
            // 跳至頁面
            navController.navigate(R.id.action_adminCommodityFragment_to_adminCommodityAddFragment);
        });
    }
}