package idv.example.goodposture.user.my;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import idv.example.goodposture.R;
import idv.example.goodposture.user.PreActivity;

public class MyAccountFragment extends Fragment {
    private AppCompatActivity activity;
    private Toolbar toolbar;
    private ImageView ivMyAccount;
    private TextView tvAccountInfo, tvAccountPassword, tvAccountLogout;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Myinfo myinfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (AppCompatActivity) getActivity();
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        myinfo = new Myinfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_account, container, false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleToolbar();
        handleImageView();
        handleTextView();
    }

    private void findViews(View view) {
        toolbar = view.findViewById(R.id.tb_my);
        ivMyAccount = view.findViewById(R.id.iv_my_account);
        tvAccountInfo = view.findViewById(R.id.tv_account_info);
        tvAccountPassword = view.findViewById(R.id.tv_account_password);
        tvAccountLogout = view.findViewById(R.id.tv_account_logout);
    }

    //???????????????

    private void handleToolbar() {
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle("??????????????????");
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    //??????menu???????????????

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == android.R.id.home){
            Navigation.findNavController(toolbar).popBackStack();
        }else{
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void handleImageView() {
        // ???????????? (????????????????????? uid ??? db ??????????????????)
        DocumentReference queRef = db.collection("my_info")
                .document(Objects.requireNonNull(auth.getCurrentUser()).getUid());
        // ???????????????????????? & ???????????????
        queRef.get().addOnCompleteListener(queryTask -> {
            // ??????????????????
            if (queryTask.isSuccessful()) {
                // ?????? getResult() ?????????????????????????????????
                DocumentSnapshot document = queryTask.getResult();
                // ???????????????????????????????????????????????????
                assert document != null;
                if (!document.exists()) {
                    ivMyAccount.setImageResource(R.drawable.no_image);
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
                            showImage(ivMyAccount, myinfo.getImagePath());
                        }
                    } else {
                        String message = task.getException() == null ?
                                "????????????" :
                                task.getException().getMessage();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showImage(ImageView ivMyAccount, String imagePath) {
        final int ONE_MEGABYTE = 1024 * 1024;
        StorageReference imageRef = storage.getReference().child(imagePath);
        imageRef.getBytes(ONE_MEGABYTE)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        byte[] bytes = task.getResult();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        ivMyAccount.setImageBitmap(bitmap);
                    } else {
                        String message = task.getException() == null ?
                                "Download fail" + " : " + imagePath :
                                task.getException().getMessage() + " : " + imagePath;
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleTextView() {
        tvAccountInfo.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(tvAccountInfo);
            navController.navigate(R.id.action_myAccountFragment_to_myPersonalInfoFragment);
        });
        tvAccountPassword.setOnClickListener( v -> {
            NavController navController = Navigation.findNavController(tvAccountPassword);
            navController.navigate(R.id.action_myAccountFragment_to_myResetPasswordFragment);
        });
        tvAccountLogout.setOnClickListener(v -> {
            LogoutDialog();
        });
    }

    private void LogoutDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage("??????????????????");
        alertDialog.setPositiveButton("???", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity().getBaseContext(), "?????????", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setNegativeButton("???", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // ??????
                auth.signOut();
                // ???????????????????????????
                Intent intent = new Intent(getActivity(), PreActivity.class);
                startActivity(intent);
            }
        });
        alertDialog.setCancelable(false);   // disable click other area
        alertDialog.show();
    }
}