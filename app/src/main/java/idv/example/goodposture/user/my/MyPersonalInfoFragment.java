package idv.example.goodposture.user.my;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import idv.example.goodposture.R;
import idv.example.goodposture.user.MainActivity;
import idv.example.goodposture.user.PreActivity;
import idv.example.goodposture.user.home.Bodyinfo;

public class MyPersonalInfoFragment extends Fragment {
    private AppCompatActivity activity;
    private Toolbar toolbar;
    private ImageView ivMyAvatar;
    private TextView tvMyInfoNameContent, tvMyInfoAccountContent,
            tvMyInfoNicknameContent, tvMyInfoGenderContent, tvMyInfoPhoneContent, tvMyInfoBirthContent;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Myinfo myinfo;
    private Bodyinfo bodyinfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        setHasOptionsMenu(true);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        myinfo = new Myinfo();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.my_personal_info_menu, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_personal_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleToolbar();
        setCommonInfo();
        handleView();
    }

    private void findViews(View view) {
        toolbar = view.findViewById(R.id.tb_my);
        ivMyAvatar = view.findViewById(R.id.iv_my_avatar_info);
        tvMyInfoNameContent = view.findViewById(R.id.my_info_name_content);
        tvMyInfoAccountContent = view.findViewById(R.id.my_info_account_content);
        tvMyInfoNicknameContent = view.findViewById(R.id.my_info_nickname_content);
        tvMyInfoGenderContent = view.findViewById(R.id.my_info_gender_content);
        tvMyInfoPhoneContent = view.findViewById(R.id.my_info_telephone_content);
        tvMyInfoBirthContent = view.findViewById(R.id.my_info_birth_content);
    }

    private void handleToolbar() {
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle("??????????????????");
        ActionBar actionBar = activity.getSupportActionBar();
        //???????????????
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //??????menu???????????????
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.my_personal_info_edit) {
            NavController navController = Navigation.findNavController(toolbar);
            navController.navigate(R.id.action_myPersonalInfoFragment_to_myPersonalInfoFragmentEdit);
            return true;
        } else if (itemId == android.R.id.home) {
            Navigation.findNavController(toolbar).popBackStack();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void setCommonInfo() {
        tvMyInfoAccountContent
                .setText(Objects.requireNonNull(auth.getCurrentUser()).getEmail());
        // ????????????????????????
        db.collection("body_info")
                .document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                // ????????????
                .get()
                // ???????????????????????????
                .addOnCompleteListener(bodyInfoTask -> {
                    if (bodyInfoTask.isSuccessful() && bodyInfoTask.getResult() != null) {
                        // ???????????????????????????????????????
                        // for (DocumentSnapshot documentSnapshot : task.getResult())
                        DocumentSnapshot documentSnapshot = bodyInfoTask.getResult();
                        bodyinfo = documentSnapshot.toObject(Bodyinfo.class);
                        assert bodyinfo != null;
                        if (bodyinfo.getGender().equals("male")) {
                            tvMyInfoGenderContent.setText("???");
                        } else {
                            tvMyInfoGenderContent.setText("???");
                        }

                    } else {
                        String message = bodyInfoTask.getException() == null ?
                                "????????????" :
                                bodyInfoTask.getException().getMessage();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleView() {
        // ???????????? (????????????????????? uid ??? db ??????????????????)
        DocumentReference queRef = db.collection("my_info")
                .document(auth.getCurrentUser().getUid());
        // ???????????????????????? & ???????????????
        queRef.get().addOnCompleteListener(queryTask -> {
            // ??????????????????
            if (queryTask.isSuccessful()) {
                // ?????? getResult() ?????????????????????????????????
                DocumentSnapshot document = queryTask.getResult();
                // ???????????????????????????????????????????????????
                assert document != null;
                if (!document.exists()) {
                    ivMyAvatar.setImageResource(R.drawable.no_image);
                    tvMyInfoNameContent.setText("?????????");
                    tvMyInfoNicknameContent.setText("?????????");
                    tvMyInfoPhoneContent.setText("?????????");
                    tvMyInfoBirthContent.setText("?????????");
                } else {
                    showInfo();
                }
            }
        });

    }

    private void showInfo() {
        /**
         ?????????????????????????????? (EX : uid)
         .whereEqualTo("uid", auth.getCurrentUser().getUid())
         ?????????????????? (???????????????????????? ID ????????????????????? UID)
         .document(auth.getCurrentUser().getUid())
         ?????????????????? (?????????????????????????????????????????????????????? ID)
         .document()
         */
        // ??????????????????
        db.collection("my_info")
                .document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                // ????????????
                .get()
                // ???????????????????????????
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // ???????????????????????????????????????
                        DocumentSnapshot documentSnapshot = task.getResult();
                        myinfo = documentSnapshot.toObject(Myinfo.class);
                        assert myinfo != null;
                        if ((myinfo.getImagePath() != null)) {
                            showImage(ivMyAvatar, myinfo.getImagePath());
                        }
                        tvMyInfoNameContent.setText(myinfo.getName());
                        tvMyInfoNicknameContent.setText(myinfo.getNickname());
                        tvMyInfoPhoneContent.setText(myinfo.getPhone());
                        tvMyInfoBirthContent.setText(myinfo.getBirth());
                    } else {
                        String message = task.getException() == null ?
                                "????????????" :
                                task.getException().getMessage();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showImage(ImageView ivMyAvatar, String imagePath) {
        final int ONE_MEGABYTE = 1024 * 1024;
        StorageReference imageRef = storage.getReference().child(imagePath);
        imageRef.getBytes(ONE_MEGABYTE)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        byte[] bytes = task.getResult();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        ivMyAvatar.setImageBitmap(bitmap);
                    } else {
                        String message = task.getException() == null ?
                                "Download fail" + " : " + imagePath :
                                task.getException().getMessage() + " : " + imagePath;
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}