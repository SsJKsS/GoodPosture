package idv.example.goodposture.user.forum;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import idv.example.goodposture.R;

public class ForumContextFragment extends Fragment {
    private ImageView iv_back;
    private ImageView iv_thumb_black;
    private boolean click = false;
    private RecyclerView resRecyclerView;
    private ArrayAdapter<ForumContextResponseList> forumContextResponseListArrayAdapter;
    private resAdapter resAdapter;
    private CardView cv_Response;
    private EditText et_response;
    private ImageView iv_Response_Send;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forum_context, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleback();
        handlethumb();
        handleResRecycleView();
        handleResponseSend();
    }

    private void findViews(View view) {
        iv_back = view.findViewById(R.id.iv_back);
        iv_thumb_black = view.findViewById(R.id.iv_thumb_black);
        resRecyclerView = view.findViewById(R.id.recyclerView2);
        cv_Response = view.findViewById(R.id.cv_Response);
        et_response = view.findViewById(R.id.et_response);
        iv_Response_Send = view.findViewById(R.id.iv_Response_Send);

    }

    /**
     * 3. 自定義Adapter類別
     * 3.1 繼承RecyclerView.Adapter
     */
    private void handleResponseSend() {
        iv_Response_Send.setOnClickListener(view-> {
            final String response = String.valueOf(et_response.getText());

            if(response.isEmpty()){
                et_response.setError("請輸入回覆");
            }
        });
    }

    private void handleback() {
        iv_back.setOnClickListener(view -> {
            // 取得NavController物件
            NavController navController = Navigation.findNavController(view);
            // 跳至頁面
            navController.navigate(R.id.action_forumContextFragment_to_forumBrowseFragment);
        });
    }

    private void handlethumb() {

        iv_thumb_black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!click){
                    iv_thumb_black.setImageResource(R.drawable.ic_outline_thumb_up_blue_24);
                    click = true;
                } else {
                    iv_thumb_black.setImageResource(R.drawable.ic_outline_thumb_up_black_48);
                    click = false;
                }
            }
        });


    }

    private void handleResRecycleView() {
        // 4.2 設定Adapter
        resRecyclerView.setAdapter(new resAdapter(this,getForumContextResponseList()));
        // 4.3 設定LayoutManager
        resRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 4.3 設定LayoutManager


        /** 可試試以下2種LayoutManager! */
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 4, RecyclerView.HORIZONTAL, false));
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, RecyclerView.HORIZONTAL));

//        cardView.setOnClickListener(view -> {
//            NavController navController = Navigation.findNavController(view);
//            navController.navigate(R.id.action_forumBrowseFragment_to_forumContextFragment);
//        });


    }
    private static class resAdapter extends RecyclerView.Adapter<resAdapter.MyResViewHolder> {

        // 3.2 欄位: Context物件、選項資料物件
        private final Context context;
        private List<ForumContextResponseList> list;

        // 3.3 建構子: 2個參數(Context型態、選項資料的型態)，用來初始化2欄位
        public resAdapter(ForumContextFragment context, List<ForumContextResponseList> list) {
            this.context = context.getActivity();
            this.list = list;
        }

        // 3.4 內部類別: 自定義ViewHolder類別
        // 3.4.1 繼承RecyclerView.ViewHolder
        private static class MyResViewHolder extends RecyclerView.ViewHolder {
            public View forumBrowseItemView;
            // 3.4.2 欄位: 對應選項容器元件，之內的所有元件
            TextView tv_Response_User;
            TextView tv_Response_Time;
            TextView tv_Response_Context;
            



            // 3.4.3 建構子: 1個參數(View型態)，該參數就是選項容器元件，用來取得各容器元件的參考
            public MyResViewHolder(@NonNull View forumContextResponseItemView) {
                super(forumContextResponseItemView);
                tv_Response_User = forumContextResponseItemView.findViewById(R.id.tv_Response_User);
                tv_Response_Time = forumContextResponseItemView.findViewById(R.id.tv_Response_Time);
                tv_Response_Context = forumContextResponseItemView.findViewById(R.id.tv_Response_Context);

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
        public resAdapter.MyResViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View forumContextResponseItemView = LayoutInflater.from(context).inflate(R.layout.forum_context_item_response, parent, false);
            return new resAdapter.MyResViewHolder(forumContextResponseItemView);

        }

        // 3.5.3 onBindViewHolder()
        //  透過ViewHolder物件，將 資料 綁定 至 各元件上
        //  各元件的其他處理，EX.註冊/實作監聽器
        @Override
        public void onBindViewHolder(@NonNull resAdapter.MyResViewHolder holder, int position) {
            final ForumContextResponseList forumContextResponseList = list.get(position);
            holder.tv_Response_User.setText(forumContextResponseList.getTv_Response_User());
            holder.tv_Response_Time.setText(forumContextResponseList.getTv_Response_Time());
            holder.tv_Response_Context.setText(forumContextResponseList.getTv_Response_Context());




//            final String text = position + 1 + ": " + forumBrowseLists.getTitle() ;
//            holder.itemView.setOnClickListener(view -> Toast.makeText(context, text, Toast.LENGTH_SHORT).show());

        }
    }
    private List<ForumContextResponseList> getForumContextResponseList() {
        List<ForumContextResponseList> forumContextResponseLists = new ArrayList<>();
        forumContextResponseLists.add(new ForumContextResponseList("鯊鯊1","2021/08/25 10:35","djfgisjdoijidfjigo"));
        forumContextResponseLists.add(new ForumContextResponseList("鯊鯊2","2021/08/25 10:35","djfgisjdoijidfjigo"));
        forumContextResponseLists.add(new ForumContextResponseList("鯊鯊3","2021/08/25 10:35","djfgisjdoijidfjigo"));
        forumContextResponseLists.add(new ForumContextResponseList("鯊鯊4","2021/08/25 10:35","djfgisjdofjigo"));
        forumContextResponseLists.add(new ForumContextResponseList("鯊鯊5","2021/08/25 10:35","djfgisjdoidfjigo"));
        return forumContextResponseLists;
    }
}