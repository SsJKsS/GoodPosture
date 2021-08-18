package idv.example.goodposture.user.forum;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import idv.example.goodposture.R;

public class ForumBrowseFragment extends Fragment {
    private RecyclerView recyclerView;
    private AppCompatActivity activity;
    private ArrayAdapter<ForumBrowseList> forumBrowseListArrayAdapter;
    private MyAdapter myAdapter;
    private CardView cardView;
    private SearchView searchView;
    private ImageView ivAdd;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = (AppCompatActivity) getActivity();

        return inflater.inflate(R.layout.fragment_forum_browse, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleRecyclerView();
        handleSearchView();
        handleAdd();
    }

    private void findViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        cardView = view.findViewById(R.id.cardView);
        searchView = view.findViewById(R.id.searchView);
        ivAdd = view.findViewById(R.id.ivAdd);

    }

    private void handleAdd() {
        ivAdd.setOnClickListener(view -> {
            // 取得NavController物件
            NavController navController = Navigation.findNavController(view);
            // 跳至頁面
            navController.navigate(R.id.action_forumBrowseFragment_to_forumAddFragment);
        });
    }


    private void handleSearchView() {

        // 註冊/實作 查詢文字監聽器
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 當點擊提交鍵(虛擬鍵盤)時，自動被呼叫
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 當查詢文字改變時，自動被呼叫
            @Override
            public boolean onQueryTextChange(String newText) {
                MyAdapter adapter = (MyAdapter) recyclerView.getAdapter();
                if (adapter == null) {
                    return false;
                }

                if (newText.isEmpty()) {
                    adapter.list = getForumBrowseList();
                } else {
                    List<ForumBrowseList> resultList = new ArrayList<>();
                    for (ForumBrowseList forumBrowseList : adapter.list) {
                        if (forumBrowseList.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                            resultList.add(forumBrowseList);
                        }
                    }
                    adapter.list = resultList;
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    private void handleRecyclerView() {
        // 4.2 設定Adapter
        recyclerView.setAdapter(new MyAdapter(this, getForumBrowseList()));
        // 4.3 設定LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
    private static class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        // 3.2 欄位: Context物件、選項資料物件
        private final Context context;
        private List<ForumBrowseList> list;

        // 3.3 建構子: 2個參數(Context型態、選項資料的型態)，用來初始化2欄位
        public MyAdapter(ForumBrowseFragment context, List<ForumBrowseList> list) {
            this.context = context.getActivity();
            this.list = list;
        }

        // 3.4 內部類別: 自定義ViewHolder類別
        // 3.4.1 繼承RecyclerView.ViewHolder
        private static class MyViewHolder extends RecyclerView.ViewHolder {
            public View forumBrowseItemView;
            // 3.4.2 欄位: 對應選項容器元件，之內的所有元件
            TextView tvTitle;
            TextView tvAuthor;
            TextView tvTime;
            ImageView ivLike;
            TextView tvLikes;
            ImageView ivMessage;
            TextView tvMessages;



            // 3.4.3 建構子: 1個參數(View型態)，該參數就是選項容器元件，用來取得各容器元件的參考
            public MyViewHolder(@NonNull View forumBrowseItemView) {
                super(forumBrowseItemView);
                tvTitle = forumBrowseItemView.findViewById(R.id.tvTitle);
                tvAuthor = forumBrowseItemView.findViewById(R.id.tvAuthor);
                tvTime = forumBrowseItemView.findViewById(R.id.tvTime);
                ivLike = forumBrowseItemView.findViewById(R.id.ivLike);
                tvLikes = forumBrowseItemView.findViewById(R.id.tvLikes);
                ivMessage = forumBrowseItemView.findViewById(R.id.ivMessage);
                tvMessages = forumBrowseItemView.findViewById(R.id.tvMessages);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 取得NavController物件
                        NavController navController = Navigation.findNavController(view);
                        // 跳至頁面
                        navController.navigate(R.id.action_forumBrowseFragment_to_forumContextFragment);
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
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View forumBrowseItemView = LayoutInflater.from(context).inflate(R.layout.forum_browse_item_view, parent, false);
            return new MyViewHolder(forumBrowseItemView);

        }

        // 3.5.3 onBindViewHolder()
        //  透過ViewHolder物件，將 資料 綁定 至 各元件上
        //  各元件的其他處理，EX.註冊/實作監聽器
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final ForumBrowseList forumBrowseLists = list.get(position);
            holder.tvTitle.setText(forumBrowseLists.getTitle());
            holder.tvAuthor.setText(forumBrowseLists.getAuthor());
            holder.tvTime.setText(forumBrowseLists.getTime());
            holder.ivLike.setImageResource(forumBrowseLists.getIvLike());
            holder.tvLikes.setText(forumBrowseLists.getLikes());
            holder.ivMessage.setImageResource(forumBrowseLists.getIvMessage());
            holder.tvMessages.setText(forumBrowseLists.getMessages());



//            final String text = position + 1 + ": " + forumBrowseLists.getTitle() ;
//            holder.itemView.setOnClickListener(view -> Toast.makeText(context, text, Toast.LENGTH_SHORT).show());

        }
    }
    private List<ForumBrowseList> getForumBrowseList() {
        List<ForumBrowseList> forumBrowseLists = new ArrayList<>();
        forumBrowseLists.add(new ForumBrowseList("苦民所苦，睡到中午A","雪莉","2021/07/29 14:46", R.drawable.ic_outline_thumb_up_black_48,"20", R.drawable.ic_baseline_forum_black_48,"20" ));
        forumBrowseLists.add(new ForumBrowseList("苦民所苦，睡到中午B","雪莉","2021/07/29 14:46", R.drawable.ic_outline_thumb_up_black_48,"20", R.drawable.ic_baseline_forum_black_48,"20" ));
        forumBrowseLists.add(new ForumBrowseList("苦民所苦，睡到中午C","雪莉","2021/07/29 14:46", R.drawable.ic_outline_thumb_up_black_48,"20", R.drawable.ic_baseline_forum_black_48,"20" ));
        forumBrowseLists.add(new ForumBrowseList("苦民所苦，睡到中午D","雪莉","2021/07/29 14:46", R.drawable.ic_outline_thumb_up_black_48,"20", R.drawable.ic_baseline_forum_black_48,"20" ));
        forumBrowseLists.add(new ForumBrowseList("苦民所苦，睡到中午E","雪莉","2021/07/29 14:46", R.drawable.ic_outline_thumb_up_black_48,"20", R.drawable.ic_baseline_forum_black_48,"20" ));
        return forumBrowseLists;
    }
}