package idv.example.goodposture.user.forum;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import idv.example.goodposture.R;
import idv.example.goodposture.user.my.Myinfo;

public class ForumBrowseFragment extends Fragment {
    private static  final  String TAG = "TAG_ForumBrowseFragment";
    private RecyclerView recyclerView;
//    private ForumBrowseList forumBrowseList;
    private SearchView searchView;
    private ImageView ivAdd;
    private FirebaseFirestore db;
    private ListenerRegistration registration;
    private List<ForumBrowseList> forumBrowseLists;
    private AppCompatActivity activity;
    private FirebaseAuth auth;
    private Myinfo myinfo;
    private ForumBrowseList forumList;
//    private List<ForumBrowseList> forum;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        forumBrowseLists = new ArrayList<>();
        myinfo = new Myinfo();
        forumList = new ForumBrowseList();
        listenToSpot();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forum_browse, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleSearchView();
        handleAdd();
    }

    private void findViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
//        cardView = view.findViewById(R.id.cardView);
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
                showForumBrowseList();
                return true;
            }
        });
    }
    private void showForumBrowseList(){
        MyAdapter adapter = (MyAdapter) recyclerView.getAdapter();
        if (adapter == null) {
            adapter = new MyAdapter();
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            recyclerView.setAdapter(adapter);
        }
        String queryStr = searchView.getQuery().toString();
        if (queryStr.isEmpty()) {
            adapter.setForumBrowseLists(forumBrowseLists);
        } else {
            List<ForumBrowseList> resultList = new ArrayList<>();
            for (ForumBrowseList forumBrowseList : adapter.list) {
                if (forumBrowseList.getTitle().toLowerCase().contains(queryStr.toLowerCase())) {
                    resultList.add(forumBrowseList);
                }
            }
            adapter.setForumBrowseLists(resultList);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        showAllForumBrowseList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(registration != null){
            registration.remove();
            registration = null;
        }
    }

    private void showAllForumBrowseList() {
        db.collection("forumBrowseList").get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null){
                        if(!forumBrowseLists.isEmpty()){
                            forumBrowseLists.clear();
                        }
                        for (QueryDocumentSnapshot document : task.getResult()){
                            forumBrowseLists.add(document.toObject(ForumBrowseList.class));
                        }
                        showForumBrowseList();
                    } else {
                        String message = task.getException() == null ? "No forumBrowseList found" : task.getException().getMessage();
                        Log.e(TAG, "exception message:" + message);
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }



    /**
     * 3. 自定義Adapter類別
     * 3.1 繼承RecyclerView.Adapter
     */
    private  class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private List<ForumBrowseList> list;
        MyAdapter(){
        }

        public void setForumBrowseLists(List<ForumBrowseList> forumBrowseLists){
            this.list = forumBrowseLists;
        }
        // 3.4 內部類別: 自定義ViewHolder類別
        // 3.4.1 繼承RecyclerView.ViewHolder
        class MyViewHolder extends RecyclerView.ViewHolder {
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
            View forumBrowseItemView = LayoutInflater.from(activity).inflate(R.layout.forum_browse_item_view, parent, false);
            return new MyViewHolder(forumBrowseItemView);
        }
        // 3.5.3 onBindViewHolder()
        //  透過ViewHolder物件，將 資料 綁定 至 各元件上
        //  各元件的其他處理，EX.註冊/實作監聽器
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final ForumBrowseList forumBrowseList = list.get(position);

            db.collection("my_info").whereEqualTo("id",forumBrowseList.getAuthor())
                    .get()
                    .addOnCompleteListener(myTask -> {
                        if (myTask.isSuccessful() && myTask.getResult() != null){
                            if (!forumBrowseLists.isEmpty()){
                                forumBrowseLists.clear();
                            }
                            for (QueryDocumentSnapshot document : myTask.getResult()){
                                forumBrowseLists.add(document.toObject(ForumBrowseList.class));
                            }
                        }else {
                            String message = myTask.getException() == null ?
                                    "查無資料" :
                                    myTask.getException().getMessage();
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                    });



            holder.tvTitle.setText(forumBrowseList.getTitle());
            holder.tvAuthor.setText(forumBrowseList.getAuthor());
            holder.tvTime.setText(forumBrowseList.getTime());
            holder.ivLike.setImageResource(R.drawable.ic_outline_thumb_up_black_48);
            holder.tvLikes.setText("20");
            holder.ivMessage.setImageResource(R.drawable.ic_baseline_forum_black_48);
            holder.tvMessages.setText("20");



            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("forumBrowseList", forumBrowseList);
                    bundle.putSerializable("my_info",myinfo);
                    // 取得NavController物件
                    NavController navController = Navigation.findNavController(view);
                    // 跳至頁面
                    navController.navigate(R.id.action_forumBrowseFragment_to_forumContextFragment, bundle);
                }
            });


//            final String text = position + 1 + ": " + forumBrowseLists.getTitle() ;
//            holder.itemView.setOnClickListener(view -> Toast.makeText(context, text, Toast.LENGTH_SHORT).show());

        }
    }

    private void listenToSpot() {
        if (registration == null) {
            registration = db.collection("forumBrowseList").addSnapshotListener((snapshots, e) -> {
                Log.d(TAG, "event happened");
                if (e == null) {
                    List<ForumBrowseList> forumBrowseLists = new ArrayList<>();
                    if (snapshots != null) {
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            ForumBrowseList forumBrowseList = dc.getDocument().toObject(ForumBrowseList.class);
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "Added forumBrowse: " + forumBrowseList.getTitle());
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified forumBrowse: " + forumBrowseList.getTitle());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed forumBrowse: " + forumBrowseList.getTitle());
                                    break;
                                default:
                                    break;
                            }
                        }

                        for (DocumentSnapshot document : snapshots.getDocuments()) {
                            forumBrowseLists.add(document.toObject(ForumBrowseList.class));
                        }
                        this.forumBrowseLists = forumBrowseLists;
                        showForumBrowseList();
                    }
                } else {
                    Log.e(TAG, e.getMessage(), e);
                }
            });
        }
    }
}