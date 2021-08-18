package idv.example.goodposture.user.forum;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import idv.example.goodposture.R;

public class ForumContextFragment extends Fragment {
    private ImageView iv_back;
    private ImageView iv_thumb_black;
    private boolean click = false;



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
    }

    private void findViews(View view) {
        iv_back = view.findViewById(R.id.iv_back);
        iv_thumb_black = view.findViewById(R.id.iv_thumb_black);

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
}