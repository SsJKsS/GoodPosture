package idv.example.goodposture.user.forum;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import idv.example.goodposture.R;

public class ForumAddFragment extends Fragment {
    private ImageView iv_back2;
    private ImageView iv_send;
    private EditText et_title;
    private EditText et_context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    private void findViews(View view) {
        iv_back2 = view.findViewById(R.id.iv_back2);
        iv_send = view.findViewById(R.id.iv_send);
        et_context = view.findViewById(R.id.et_context);
        et_title = view.findViewById(R.id.et_title);
    }

    private void handleback2() {
        iv_back2.setOnClickListener( view ->{
            // 取得NavController物件
            NavController navController = Navigation.findNavController(view);
            // 跳至頁面
            navController.navigate(R.id.action_forumAddFragment_to_forumBrowseFragment2);
        });
    }

    private void handlesend() {
        iv_send.setOnClickListener( view ->{
            final String title = String.valueOf(et_title.getText());
            final String context = String.valueOf(et_context.getText());

            if (title.isEmpty()){
                et_title.setError("請輸入標題");
            }
            if (context.isEmpty()){
                et_context.setError("請輸入內文");

            }
            if(!title.isEmpty() && !context.isEmpty()){
                // 取得NavController物件
                NavController navController = Navigation.findNavController(view);
                // 跳至頁面
                navController.navigate(R.id.action_forumAddFragment_to_forumBrowseFragment2);
            }




        });
    }
}