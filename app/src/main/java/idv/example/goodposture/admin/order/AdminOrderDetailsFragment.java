package idv.example.goodposture.admin.order;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import idv.example.goodposture.R;
import idv.example.goodposture.user.MainActivity;
import idv.example.goodposture.user.forum.ForumContextFragment;
import idv.example.goodposture.user.forum.ForumContextResponseList;

public class AdminOrderDetailsFragment extends Fragment {
    private ImageView iv_order_back;
    private RecyclerView odDetailsRecyclerView;
    private ArrayAdapter<AdminOrderDetailsItem> adminOrderDetailsItemArrayAdapter;
    private odDetailsAdapter odDetailsAdapter;
    private CardView cv_od_details;
    private Button bt_Cancel;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_order_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleOrderBack();
        handleOdDetailsRecyclerView();
        handleBtCanael();
    }

    private void findViews(View view) {
        iv_order_back = view.findViewById(R.id.iv_order_back);
        cv_od_details = view.findViewById(R.id.cv_od_details);
        odDetailsRecyclerView = view.findViewById(R.id.rv_order_details);
        bt_Cancel = view.findViewById(R.id.bt_Cancel);
    }

    private void handleBtCanael() {
        bt_Cancel.setOnClickListener(view->{
            NewDialog();
        });
    }

    private void handleOrderBack() {
        iv_order_back.setOnClickListener(view ->{
                // 取得NavController物件
                NavController navController = Navigation.findNavController(view);
                // 跳至頁面
                navController.navigate(R.id.action_adminOrderDetailsFragment_to_adminOrderFragment);
        });
    }

    private void handleOdDetailsRecyclerView() {
        // 4.2 設定Adapter
        odDetailsRecyclerView.setAdapter(new odDetailsAdapter(this,getAdminOrderDetailsItem()));
        // 4.3 設定LayoutManager
        odDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 4.3 設定LayoutManager


        /** 可試試以下2種LayoutManager! */
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 4, RecyclerView.HORIZONTAL, false));
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, RecyclerView.HORIZONTAL));

//        cardView.setOnClickListener(view -> {
//            NavController navController = Navigation.findNavController(view);
//            navController.navigate(R.id.action_forumBrowseFragment_to_forumContextFragment);
//        });


    }
    private static class odDetailsAdapter extends RecyclerView.Adapter<odDetailsAdapter.DetailsViewHolder> {

        // 3.2 欄位: Context物件、選項資料物件
        private final Context context;
        private List<AdminOrderDetailsItem> list;

        // 3.3 建構子: 2個參數(Context型態、選項資料的型態)，用來初始化2欄位
        public odDetailsAdapter(AdminOrderDetailsFragment context, List<AdminOrderDetailsItem> list) {
            this.context = context.getActivity();
            this.list = list;
        }

        // 3.4 內部類別: 自定義ViewHolder類別
        // 3.4.1 繼承RecyclerView.ViewHolder
        private static class DetailsViewHolder extends RecyclerView.ViewHolder {
            public View adminOrderDetailItem;
            // 3.4.2 欄位: 對應選項容器元件，之內的所有元件
            ImageView iv_order_product;
            TextView tv_order_product;
            TextView tv_order_price;
            TextView tv_order_context;




            // 3.4.3 建構子: 1個參數(View型態)，該參數就是選項容器元件，用來取得各容器元件的參考
            public DetailsViewHolder(@NonNull View detailsItemView) {
                super(detailsItemView);
                iv_order_product = detailsItemView.findViewById(R.id.iv_order_product);
                tv_order_product = detailsItemView.findViewById(R.id.tv_order_product);
                tv_order_context = detailsItemView.findViewById(R.id.tv_order_context);
                tv_order_price = detailsItemView.findViewById(R.id.tv_order_price);

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
        public DetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View forumContextResponseItemView = LayoutInflater.from(context).inflate(R.layout.admin_order_details_item, parent, false);
            return new DetailsViewHolder(forumContextResponseItemView);

        }

        // 3.5.3 onBindViewHolder()
        //  透過ViewHolder物件，將 資料 綁定 至 各元件上
        //  各元件的其他處理，EX.註冊/實作監聽器
        @Override
        public void onBindViewHolder(@NonNull DetailsViewHolder holder, int position) {
            final AdminOrderDetailsItem adminOrderDetailsItem = list.get(position);
            holder.tv_order_price.setText(adminOrderDetailsItem.getPrice());
            holder.tv_order_context.setText(adminOrderDetailsItem.getContext());
            holder.tv_order_product.setText(adminOrderDetailsItem.getProduct());
            holder.iv_order_product.setImageResource(adminOrderDetailsItem.getImage());




//            final String text = position + 1 + ": " + forumBrowseLists.getTitle() ;
//            holder.itemView.setOnClickListener(view -> Toast.makeText(context, text, Toast.LENGTH_SHORT).show());

        }
    }
    private List<AdminOrderDetailsItem> getAdminOrderDetailsItem() {
        List<AdminOrderDetailsItem> adminOrderDetailsItems = new ArrayList<>();
        adminOrderDetailsItems.add(new AdminOrderDetailsItem(R.drawable.shark,"鯊鯊一號","呱呱","NT$1,000"));
        adminOrderDetailsItems.add(new AdminOrderDetailsItem(R.drawable.shark,"鯊鯊一號","呱呱","NT$1,000"));
        adminOrderDetailsItems.add(new AdminOrderDetailsItem(R.drawable.shark,"鯊鯊一號","呱呱","NT$1,000"));
        adminOrderDetailsItems.add(new AdminOrderDetailsItem(R.drawable.shark,"鯊鯊一號","呱呱","NT$1,000"));
        adminOrderDetailsItems.add(new AdminOrderDetailsItem(R.drawable.shark,"鯊鯊一號","呱呱","NT$1,000"));
        return adminOrderDetailsItems;
    }

    private void NewDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        EditText editText = new EditText(getActivity());
        alertDialog.setTitle("請輸入原因");
        alertDialog.setView(editText);
        alertDialog.setNegativeButton("送出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity().getBaseContext(), "已送出", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}