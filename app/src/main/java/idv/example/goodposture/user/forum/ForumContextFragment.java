package idv.example.goodposture.user.forum;

import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import idv.example.goodposture.R;
import idv.example.goodposture.user.my.Myinfo;

public class ForumContextFragment extends Fragment {
    private static final String TAG = "TAG_ForumContextFragment";
    private ImageView iv_back;
    private ImageView iv_thumb_black;
    private boolean click = false;
    private RecyclerView resRecyclerView;
    private ArrayAdapter<ForumContextResponseList> forumContextResponseListArrayAdapter;
    private resAdapter resAdapter;
    private CardView cv_Response;
    private EditText et_response;
    private ImageView iv_Response_Send;
    private AppCompatActivity activity;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Myinfo myinfo;
    private ForumBrowseList forumBrowseList;
    private ListenerRegistration registration;
    private List<ForumContextResponseList> forumContextResponseLists;
    private ForumContextResponseList forumContextResponseList;

    private TextView tv_context_title;
    private TextView tv_context_nickname;
    private TextView tv_context;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        myinfo = new Myinfo();
        forumBrowseList = new ForumBrowseList();
        forumContextResponseList = new ForumContextResponseList();
//        listenToSpot();
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

        if (getArguments() != null){
            forumBrowseList = (ForumBrowseList) getArguments().getSerializable("forumBrowseList");
            if (forumBrowseList != null){
                tv_context_title.setText(forumBrowseList.getTitle());
                tv_context.setText(forumBrowseList.getContext());
//                tv_context_nickname.setText(forumBrowseList.getAuthor());

            }

            myinfo = (Myinfo) getArguments().getSerializable("my_info");
            if (myinfo != null){
                tv_context_nickname.setText(myinfo.getNickname());
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
//        showResponse();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (registration != null){
            registration.remove();
            registration = null;
        }
    }

    private void findViews(View view) {
        iv_back = view.findViewById(R.id.iv_back);
        iv_thumb_black = view.findViewById(R.id.iv_thumb_black);
        resRecyclerView = view.findViewById(R.id.recyclerView2);
        cv_Response = view.findViewById(R.id.cv_Response);
        et_response = view.findViewById(R.id.et_response);
        iv_Response_Send = view.findViewById(R.id.iv_Response_Send);
        tv_context_title = view.findViewById(R.id.tv_context_title);
        tv_context_nickname = view.findViewById(R.id.tv_context_nickname);
        tv_context = view.findViewById(R.id.tv_context);
    }

    /**
     * 3. 自定義Adapter類別
     * 3.1 繼承RecyclerView.Adapter
     */
    private void handleResponseSend() {
        iv_Response_Send.setOnClickListener(view-> {
//            final String response = String.valueOf(et_response.getText());

            final String id = db.collection("forumContextRes").document().getId();
            forumContextResponseList.setId(id);

            String response = et_response.getText().toString().trim();
            if(response.isEmpty()){
                et_response.setError("請輸入回覆");
            }else {
                forumContextResponseList.setTv_Response_Context(response);
//                forumContextResponseList.setTv_Response_User(myinfo.getNickname());
                addForumContextRes(forumContextResponseList);
            }
        });
    }

    private void addForumContextRes(final ForumContextResponseList forumContextResponseList){
        db.collection("forumContextRes").document(forumContextResponseList.getId()).set(forumContextResponseList)
                .addOnCompleteListener(task ->{
                    if (task.isSuccessful()){
                        String message = "forumContextRes is inserted" + "with ID:" +forumContextResponseList.getId();
                        Log.e(TAG,"message"+message);
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
//                        Navigation.findNavController(iv_send).popBackStack();
                    } else {
                        String message = task.getException() == null ? "Insert failed" : task.getException().getMessage();
                        Log.e(TAG,"message: "+message);
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleback() {
        iv_back.setOnClickListener(view -> {
            Navigation.findNavController(iv_back).popBackStack();
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
        resAdapter = new resAdapter();
        // 4.2 設定Adapter
        resRecyclerView.setAdapter(resAdapter);
        // 4.3 設定LayoutManager
        resRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        // 4.3 設定LayoutManager


        /** 可試試以下2種LayoutManager! */
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 4, RecyclerView.HORIZONTAL, false));
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, RecyclerView.HORIZONTAL));

//        cardView.setOnClickListener(view -> {
//            NavController navController = Navigation.findNavController(view);
//            navController.navigate(R.id.action_forumBrowseFragment_to_forumContextFragment);
//        });


    }

    private  class resAdapter extends RecyclerView.Adapter<resAdapter.MyResViewHolder> {

        private List<ForumContextResponseList> list;
        resAdapter(){
        }

        public resAdapter(List<ForumContextResponseList> list) {
            this.list = list;
        }

        // 3.4 內部類別: 自定義ViewHolder類別
        // 3.4.1 繼承RecyclerView.ViewHolder
        class MyResViewHolder extends RecyclerView.ViewHolder {


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
        public MyResViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View forumContextResponseItemView = LayoutInflater.from(activity).inflate(R.layout.forum_context_item_response, parent, false);
            return new resAdapter.MyResViewHolder(forumContextResponseItemView);

        }

        // 3.5.3 onBindViewHolder()
        //  透過ViewHolder物件，將 資料 綁定 至 各元件上
        //  各元件的其他處理，EX.註冊/實作監聽器
        @Override
        public void onBindViewHolder(@NonNull resAdapter.MyResViewHolder holder, int position) {
            final ForumContextResponseList forumContextResponseList = list.get(position);


            db.collection("my_info").document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                    .get()
                    .addOnCompleteListener(myTask -> {
                        if (myTask.isSuccessful() && myTask.getResult() != null){
                            DocumentSnapshot documentSnapshot = myTask.getResult();
                            myinfo = documentSnapshot.toObject(Myinfo.class);
                            assert  myinfo != null;
                            if ((myinfo.getName() != null)){
//                                holder.tv_Response_User.setText(myinfo.getNickname());

                            }else {
                                //holder.tv_Response_User.setText("匿名");
                                myinfo.setNickname("匿名");
                            }
                        }else {
                            String message = myTask.getException() == null ?
                                    "查無資料" :
                                    myTask.getException().getMessage();
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }

                    });

            holder.tv_Response_User.setText(myinfo.getNickname());
//            holder.tv_Response_User.setText(forumContextResponseList.getTv_Response_User());
            holder.tv_Response_Time.setText(forumContextResponseList.getTv_Response_Time());
            holder.tv_Response_Context.setText(forumContextResponseList.getTv_Response_Context());




//            final String text = position + 1 + ": " + forumBrowseLists.getTitle() ;
//            holder.itemView.setOnClickListener(view -> Toast.makeText(context, text, Toast.LENGTH_SHORT).show());

        }

    }

    private void showResponse() {
        db.collection("forumContextRes").get()
                .addOnCompleteListener(task->{
                    if (task.isSuccessful() && task.getResult() != null){
                        if (!forumContextResponseLists.isEmpty()){
                            forumContextResponseLists.clear();
                        }
                        for(QueryDocumentSnapshot document : task.getResult()){
                            forumContextResponseLists.add(document.toObject(ForumContextResponseList.class));
                        }
                        handleResRecycleView();
                    } else {
                        String message = task.getException() == null ? "No forumContextRes found" : task.getException().getMessage();
                        Log.e(TAG, "exception message:" + message);
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void listenToSpot() {
        if (registration == null) {
            registration = db.collection("forumContextRes").addSnapshotListener((snapshots, e) -> {
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
                            forumContextResponseLists.add(document.toObject(ForumContextResponseList.class));
                        }
                        this.forumContextResponseLists = forumContextResponseLists;
                        handleResRecycleView();
                    }
                } else {
                    Log.e(TAG, e.getMessage(), e);
                }
            });
        }
    }
}