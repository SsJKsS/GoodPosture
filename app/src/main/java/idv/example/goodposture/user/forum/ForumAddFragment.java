package idv.example.goodposture.user.forum;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

import idv.example.goodposture.R;
import idv.example.goodposture.user.home.Bodyinfo;
import idv.example.goodposture.user.my.Myinfo;

public class ForumAddFragment extends Fragment {
    private static final String TAG = "TAG_ForumAddFragment";
    private AppCompatActivity activity;
    private ImageView iv_back2;
    private ImageView iv_send;
    private EditText et_title;
    private EditText et_context;
    private FirebaseFirestore db;
    private ForumBrowseList forumBrowseList;
    private FirebaseAuth auth;
    private Myinfo myinfo;
    private TextView tv_title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        forumBrowseList = new ForumBrowseList();
        myinfo = new Myinfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_forum_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleback2();
        handlesend();
        handleTv_title();
    }

    private void findViews(View view) {
        iv_back2 = view.findViewById(R.id.iv_back2);
        iv_send = view.findViewById(R.id.iv_send);
        et_context = view.findViewById(R.id.et_context);
        et_title = view.findViewById(R.id.et_title);
        tv_title = view.findViewById(R.id.tv_title);
    }

    private void handleTv_title() {
        tv_title.setOnClickListener(view->{
            et_title.setText("如何吃得健康？");
            et_context.setText("原則一：均衡飲食！ \n" + "原則二：不食用加工食品！ \n"+"原則三：適當補充維他命及抗氧化物！ \n "+"額外話題....最重要的：請配合運動！");
        });
    }

    private void handleback2() {
        iv_back2.setOnClickListener( view ->{
            Navigation.findNavController(iv_back2).popBackStack();
        });
    }

    private void handlesend() {

        db.collection("my_info")
                .document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                // 獲取資料
                .get()
                // 設置網路傳輸監聽器
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // 將獲取的資料存成自定義類別
                        DocumentSnapshot documentSnapshot = task.getResult();
                        myinfo = documentSnapshot.toObject(Myinfo.class);
                    } else {
                        String message = task.getException() == null ?
                                "查無資料" :
                                task.getException().getMessage();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });

        iv_send.setOnClickListener( view ->{
            final String id = db.collection("forumBrowseList").document().getId();


            String title = et_title.getText().toString().trim();
            if (title.isEmpty()){
                et_title.setError("請輸入標題");
            }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            String time = dtf.format(LocalDateTime.now());


            String context = et_context.getText().toString().trim();
            if (context.isEmpty()){
                et_context.setError("請輸入內文");
            }else {
                forumBrowseList.setId(id);
                forumBrowseList.setTitle(title);
                forumBrowseList.setContext(context);
                forumBrowseList.setAuthor(myinfo.getNickname());
                forumBrowseList.setImagePath(myinfo.getImagePath());
                forumBrowseList.setAuthorUid(auth.getCurrentUser().getUid());
                forumBrowseList.setTime(time);
                forumBrowseList.setLikes(0);
                forumBrowseList.setMessages(0);
                forumBrowseList.setClick(false);
                addForumBrowseList(forumBrowseList);
            }
        });
    }

    private void addForumBrowseList(final ForumBrowseList forumBrowseList){
        db.collection("forumBrowseList").document(forumBrowseList.getId()).set(forumBrowseList)
                .addOnCompleteListener(task ->{
                    if (task.isSuccessful()){
                        String message = "forumBrowseList is inserted" + "with ID:" +forumBrowseList.getId();
                        Log.e(TAG,"message"+message);
                        Navigation.findNavController(iv_send).popBackStack();
                    } else {
                        String message = task.getException() == null ? "Insert failed" : task.getException().getMessage();
                        Log.e(TAG,"message: "+message);
                    }
                });
    }
}