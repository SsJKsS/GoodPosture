package idv.example.goodposture.user.my;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import idv.example.goodposture.R;
import idv.example.goodposture.user.home.Bodyinfo;
import idv.example.goodposture.user.home.Password;

public class MyResetPasswordFragment extends Fragment {
    private AppCompatActivity activity;
    private Toolbar toolBar;
    private ImageView ivAvatar, ivFastNewPassword;
    private EditText etOldPassword, etNewPassword, etConfirmNewPassword;
    private Button btReset;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Password password;
    private Myinfo myinfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        password = new Password();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (AppCompatActivity) getActivity();
        // 設定允許Fragment有功能選單
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_my_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleToolbar();
        showAvatar();
        handleButton();
    }

    private void findViews(View view) {
        toolBar = view.findViewById(R.id.toolbar);
        ivFastNewPassword = view.findViewById(R.id.iv_fastNewPassword);
        ivAvatar = view.findViewById(R.id.ivResetAvatar);
        etOldPassword = view.findViewById(R.id.et_oldPassword);
        etNewPassword = view.findViewById(R.id.et_newPassword);
        etConfirmNewPassword = view.findViewById(R.id.et_confirmNewPassword);
        btReset = view.findViewById(R.id.bt_reset);
    }

    private void handleToolbar() {
        // 設定Toolbar為狀態列
        activity.setSupportActionBar(toolBar);
        // 取得ActionBar
        ActionBar actionBar = activity.getSupportActionBar();
        // 設定顯示返回鍵
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void showAvatar() {
        /**
         查詢指定文件內的欄位 (EX : uid)
         .whereEqualTo("uid", auth.getCurrentUser().getUid())
         查詢指定文件 (有給值指定，文件 ID 為當下使用者的 UID)
         .document(auth.getCurrentUser().getUid())
         查詢指定文件 (沒給值，系統產生一組隨機字串作為文件 ID)
         .document()
         */
        // 查詢指定集合
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
                        assert myinfo != null;
                        if ((myinfo.getImagePath() != null)) {
                            showImage(ivAvatar, myinfo.getImagePath());
                        }
                    } else {
                        String message = task.getException() == null ?
                                "查無資料" :
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

    private void handleButton() {
        ivFastNewPassword.setOnClickListener(view -> {
            etOldPassword.setText("111111");
            etNewPassword.setText("222222");
            etConfirmNewPassword.setText("222222");
        });

        // 查詢指定集合
        db.collection("password")
                .document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                // 獲取資料
                .get()
                // 設置網路傳輸監聽器
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // 將獲取的資料存成自定義類別
                        DocumentSnapshot documentSnapshot = task.getResult();
                        password = documentSnapshot.toObject(Password.class);
                    } else {
                        String message = task.getException() == null ?
                                "查無資料" :
                                task.getException().getMessage();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }

                });

        btReset.setOnClickListener(view -> {
            final String oldPassword = String.valueOf(etOldPassword.getText());
            final String newPassword = String.valueOf(etNewPassword.getText());
            final String confirmNewPassword = String.valueOf(etConfirmNewPassword.getText());

            // 判斷舊密碼是否和 db 中的密碼相同
            if (!oldPassword.equals(password.getPassword())) {
                etOldPassword.setError("舊密碼錯誤");
            }

            // 判斷新密碼不可和舊密碼相同
            if (oldPassword.equals(newPassword)) {
                etOldPassword.setError("新密碼不可和舊密碼相同");
                etNewPassword.setError("新密碼不可和舊密碼相同");
            }

            // 判斷新密碼和確認新密碼不相同
            if (!newPassword.equals(confirmNewPassword)) {
                etConfirmNewPassword.setError("新密碼和確認新密碼不相同");
            }

            // 判斷舊密碼不可為空
            if (oldPassword.isEmpty()) {
                etOldPassword.setError("請輸入舊密碼");
            }

            // 判斷新密碼不可為空
            if (newPassword.isEmpty()) {
                etNewPassword.setError("請輸入新密碼");
            }

            // 判斷確認新密碼不可為空
            if (confirmNewPassword.isEmpty()) {
                etConfirmNewPassword.setError("請輸入確認新密碼");
            }

            if (oldPassword.equals(newPassword)) {
                etOldPassword.setError("舊密碼不可和新密碼相同");
                etNewPassword.setError("舊密碼不可和新密碼相同");
            }

            if (!newPassword.equals(confirmNewPassword)) {
                etNewPassword.setError("新密碼和確認新密碼不相同");
                etConfirmNewPassword.setError("新密碼和確認新密碼不相同");
            } else {
                auth.getCurrentUser().updatePassword(newPassword);
                password.setPassword(newPassword);
                // 修改指定 ID 的文件
                db.collection("password")
                        .document(auth.getCurrentUser().getUid()).set(password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String message = "修改成功";
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                // 跳轉至修改成功頁面
                                NavController navController = Navigation.findNavController(view);
                                navController.navigate(R.id.actionResetToSuccess);
                            } else {
                                String message = task.getException() == null ?
                                        "修改失敗" : task.getException().getMessage();
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    /**
     * 覆寫 功能選單監聽器
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            // 返回鍵的資源ID固定為android.R.id.home
            Navigation.findNavController(etOldPassword).popBackStack();
        }
        return true;
    }
}