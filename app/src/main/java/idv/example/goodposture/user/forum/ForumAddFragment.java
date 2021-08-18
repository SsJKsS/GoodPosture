package idv.example.goodposture.user.forum;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
            // 取得NavController物件
            NavController navController = Navigation.findNavController(view);
            // 跳至頁面
            navController.navigate(R.id.action_forumAddFragment_to_forumBrowseFragment2);
        });
    }
}