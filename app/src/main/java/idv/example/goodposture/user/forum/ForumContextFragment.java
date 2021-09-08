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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private TextView tv_context_time;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        myinfo = new Myinfo();
        forumBrowseList = new ForumBrowseList();
        forumContextResponseList = new ForumContextResponseList();
        forumContextResponseLists = new ArrayList<>();
        listenToRes();
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
        if (getArguments() != null){
            forumBrowseList = (ForumBrowseList) getArguments().getSerializable("forumBrowseList");
            if (forumBrowseList != null){
                tv_context_title.setText(forumBrowseList.getTitle());
                tv_context.setText(forumBrowseList.getContext());
                tv_context_time.setText(forumBrowseList.getTime());
                tv_context_nickname.setText(forumBrowseList.getAuthor());
                forumContextResponseList.setBr_id(forumBrowseList.getId());
            }

        handleback();
        handlethumb();
        handleResRecycleView();
        handleResponseSend();



//            myinfo = (Myinfo) getArguments().getSerializable("my_info");
//            if (myinfo != null){
//                tv_context_nickname.setText(myinfo.getNickname());
//            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        handleResRecycleView();
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
        et_response = view.findViewById(R.id.et_response);
        iv_Response_Send = view.findViewById(R.id.iv_Response_Send);
        tv_context_title = view.findViewById(R.id.tv_context_title);
        tv_context_nickname = view.findViewById(R.id.tv_context_nickname);
        tv_context = view.findViewById(R.id.tv_context);
        tv_context_time = view.findViewById(R.id.tv_context_time);
    }

    /**
     * 3. 自定義Adapter類別
     * 3.1 繼承RecyclerView.Adapter
     */
    private void handleResponseSend() {

        db.collection("my_info")
                .document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                .get()
                .addOnCompleteListener(resTask->{
                    if (resTask.isSuccessful() && resTask.getResult() != null) {
                        // 將獲取的資料存成自定義類別
                        DocumentSnapshot documentSnapshot = resTask.getResult();
                        myinfo = documentSnapshot.toObject(Myinfo.class);
                    } else {
                        String message = resTask.getException() == null ?
                                "查無資料" :
                                resTask.getException().getMessage();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });

        iv_Response_Send.setOnClickListener(view-> {
//            final String response = String.valueOf(et_response.getText());

            final String id = db.collection("forumContextRes").document().getId();
            forumContextResponseList.setId(id);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            String time = dtf.format(LocalDateTime.now());

            String response = et_response.getText().toString().trim();
            if(response.isEmpty()){
                et_response.setError("請輸入回覆");
            }else {
                forumContextResponseList.setTv_Response_Context(response);
                forumContextResponseList.setTv_Response_User(myinfo.getNickname());
                forumContextResponseList.setTv_Response_Time(time);
                addForumContextRes(forumContextResponseList);
            }
        });
//        resAdapter.notifyDataSetChanged();
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
//        db.collection("forumContextRes").get()
//                .addOnCompleteListener(resTask->{
//                    if (resTask.isSuccessful() && resTask.getResult() != null){
//                        if (!forumContextResponseLists.isEmpty()){
//                            forumContextResponseLists.clear();
//                        }
//                        for (QueryDocumentSnapshot documentSnapshot : resTask.getResult()){
//                            forumContextResponseLists.add(documentSnapshot.toObject(ForumContextResponseList.class));
//                        }
//
//                        resAdapter adapter = (resAdapter) resRecyclerView.getAdapter();
//                        if (adapter == null){
//                            adapter = new resAdapter();
//                            resRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
//                            resRecyclerView.setAdapter(adapter);
//                        }
//                        List<ForumContextResponseList> resultList = new ArrayList<>();
//                        adapter.setForumContextList(resultList);
//
//                    } else {
//                        String message = resTask.getException() == null ? "not found" : resTask.getException().getMessage();
//                        Log.e(TAG,"exception message: "+ message);
//                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
//                    }
//                });




        db.collection("forumContextRes")
                .whereEqualTo("br_id",forumBrowseList.getId())
                .get()
                .addOnCompleteListener(resTask ->{
                    if (resTask.isSuccessful() && resTask.getResult() != null) {
                        // 先清除舊資料後再儲存新資料
                        if (!forumContextResponseLists.isEmpty()) {
                            forumContextResponseLists.clear();
                        }
                        for (QueryDocumentSnapshot document : resTask.getResult()) {
                            forumContextResponseLists.add(document.toObject(ForumContextResponseList.class));
                        }
                        Log.e(TAG, "list = " +forumContextResponseLists.toString());
                        Log.e(TAG, "list = " +forumContextResponseLists.size());
                        if (forumContextResponseLists.size() != 0){
                        Log.e(TAG, "list = " +forumContextResponseLists.get(0).getTv_Response_Context());
                        }

                        showRecyclerView();
                    } else {
                        String message = resTask.getException() == null ?
                                "forumContextRes is not found" :
                                resTask.getException().getMessage();
                        Log.e(TAG, "exception message: " + message);
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showRecyclerView() {
        resAdapter adapter = (resAdapter) resRecyclerView.getAdapter();
        if(adapter == null) {
            adapter = new resAdapter();
            resRecyclerView.setAdapter(adapter);
        }
        resRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        adapter.setForumContextList(forumContextResponseLists);
    }

    private class resAdapter extends RecyclerView.Adapter<resAdapter.MyResViewHolder> {
        List<ForumContextResponseList> forumContextResponseLists = new ArrayList<>();
        resAdapter(){
        }

        public void setForumContextList(List<ForumContextResponseList> forumContextResponseLists){
            this.forumContextResponseLists = forumContextResponseLists;
        }
        // 3.4 內部類別: 自定義ViewHolder類別
        // 3.4.1 繼承RecyclerView.ViewHolder
        class MyResViewHolder extends RecyclerView.ViewHolder {
            // 3.4.2 欄位: 對應選項容器元件，之內的所有元件
            TextView tv_Response_User;
            TextView tv_Response_Time;
            TextView tv_Response_Context;

            // 3.4.3 建構子: 1個參數(View型態)，該參數就是選項容器元件，用來取得各容器元件的參考
            MyResViewHolder(View forumContextResponseItemView) {
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
            return  forumContextResponseLists.size();
        }

        // 3.5.2 onCreateViewHolder()
        //  宣告itemView，並載入選項容器元件的外觀
        //  實例化自定義的ViewHolder類別，並回傳
        @NonNull
        @Override
        public MyResViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View forumContextResponseItemView = LayoutInflater.from(activity).inflate(R.layout.forum_context_item_response, parent, false);
            return new MyResViewHolder(forumContextResponseItemView);
        }
        // 3.5.3 onBindViewHolder()
        //  透過ViewHolder物件，將 資料 綁定 至 各元件上
        //  各元件的其他處理，EX.註冊/實作監聽器
        @Override
        public void onBindViewHolder(@NonNull MyResViewHolder holder, int position) {
            final ForumContextResponseList forumContextResponseList = forumContextResponseLists.get(position);

            /*db.collection("forumContextRes").whereEqualTo("Br_id",forumBrowseList.getId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null){
                            if (!forumContextResponseLists.isEmpty()){
                                forumContextResponseLists.clear();
                            }
                            for (QueryDocumentSnapshot document : task.getResult()){
                                forumContextResponseLists.add(document.toObject(ForumContextResponseList.class));
                            }
                        }else {
                            String message = task.getException() == null ?
                                    "查無資料" :
                                    task.getException().getMessage();
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                    });*/


//            db.collection("forumContextRes")
//                    .get()
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful() && task.getResult() != null){
//                            if (!forumContextResponseLists.isEmpty()){
//                                forumContextResponseLists.clear();
//                            }
//                            for (QueryDocumentSnapshot document : task.getResult()){
////                                forumContextResponseLists.add(document.toObject(ForumContextResponseList.class));
//                                if (forumBrowseList.getId().equals(forumContextResponseList.getBr_id())){
//                                    forumContextResponseLists.add(document.toObject(ForumContextResponseList.class));
//                                }
//                            }
//                        }else {
//                            String message = task.getException() == null ?
//                                    "查無資料" :
//                                    task.getException().getMessage();
//                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
//                        }
//                    });

            holder.tv_Response_User.setText(forumContextResponseList.getTv_Response_User());
            holder.tv_Response_Time.setText(forumContextResponseList.getTv_Response_Time());
            holder.tv_Response_Context.setText(forumContextResponseList.getTv_Response_Context());

        }
    }


    private void listenToRes() {
        if (registration == null) {
            registration = db.collection("forumContextRes").addSnapshotListener((snapshots, e) -> {
                Log.d(TAG, "event happened");
                if (e == null) {
                    List<ForumContextResponseList> forumContextResponseLists = new ArrayList<>();
                    if (snapshots != null) {
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            ForumContextResponseList forumContextResponseList = dc.getDocument().toObject(ForumContextResponseList.class);
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "Added forumContextRes: " + forumContextResponseList.getTv_Response_User());
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified forumContextRes: " + forumContextResponseList.getTv_Response_User());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed forumContextRes: " + forumContextResponseList.getTv_Response_User());
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