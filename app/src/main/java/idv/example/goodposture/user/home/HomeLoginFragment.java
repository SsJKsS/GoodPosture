package idv.example.goodposture.user.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import idv.example.goodposture.R;
import idv.example.goodposture.admin.AdminActivity;
import idv.example.goodposture.user.MainActivity;

public class HomeLoginFragment extends Fragment {
    private EditText etAccount, etPassword;
    private ImageView ivPerson, ivKey;
    private Button btLogin;
    private TextView tvError;
    private Activity activity;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Password pass;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        pass = new Password();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleImage();
        handleButton();
    }

    private void findViews(View view) {
        ivPerson = view.findViewById(R.id.iv_person);
        ivKey = view.findViewById(R.id.iv_key);
        etAccount = view.findViewById(R.id.et_loginAccount);
        etPassword = view.findViewById(R.id.et_loginPassword);
        btLogin = view.findViewById(R.id.bt_login);
        tvError = view.findViewById(R.id.tv_lError);
    }

    private void handleImage() {
        ivPerson.setOnClickListener(view -> {
            etAccount.setText("good@gmail.com");
            etPassword.setText("111111");
        });

        ivKey.setOnClickListener(view -> {
            etAccount.setText("admin");
            etPassword.setText("admin");
        });
    }

    private void handleButton() {
        /**
         * Intent 為 Activity 和 Activity 之間跳轉的方法，如果要用 Fragment 跳轉到 Activity，
         * 需先將該 Fragment 藉由 getActivity() 來轉換為 Activity
         */
        btLogin.setOnClickListener(view -> {
            final String account = String.valueOf(etAccount.getText());
            final String password = String.valueOf(etPassword.getText());

            if ("admin".equals(account) && "admin".equals(password)) {
                // 跳轉至廠商首頁
                Intent intent = new Intent(getActivity(), AdminActivity.class);
                startActivity(intent);
            }

            // 判斷帳號不可為空
            if (account.isEmpty()) {
                etAccount.setError("請輸入帳號");
            }

            // 判斷密碼不可為空
            if (password.isEmpty()) {
                etPassword.setError("請輸入密碼");
            }
            // 連接 firebase 登入
            else if (!"admin".equals(account) && !"admin".equals(password)){
                signIn(account, password);
                pass.setPassword(password);
            }
        });
    }

    private void signIn(String email, String password) {
        // 利用user輸入的email與password登入
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    // 登入成功
                    if (task.isSuccessful()) {
                        // 搜尋條件 (搜尋當下登入的 uid 在 db 裡是否有資料)
//                        Query queRef = db.collection("body_info")
//                                .whereEqualTo("uid", auth.getCurrentUser().getUid());
                        DocumentReference queRef = db.collection("body_info")
                                .document(auth.getCurrentUser().getUid());
                        // 搜尋條件獲取資料 & 設置監聽器
                        queRef.get().addOnCompleteListener(queryTask -> {
                            // 獲取資料成功
                            if (queryTask.isSuccessful()) {
                                // 藉由 getResult() 將獲取到的資料塞至變數
//                                QuerySnapshot document = queryTask.getResult();
                                DocumentSnapshot document = queryTask.getResult();
                                // 獲取到的條件資料是空的，跳轉至輸入身體資訊頁面
                                assert document != null;
                                if (!document.exists()) {
                                    Navigation.findNavController(etAccount)
                                            .navigate(R.id.actionLoginToTypeBodyInfo);
                                } else {
                                    // 獲取到的條件資料有存在，跳轉至顯示身體資訊頁面顯示該條件資料
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
                        // 儲存登入時輸入的密碼至 db，以便重設密碼使用
                        savePassword();
                    // 登入失敗顯示錯誤訊息
                    } else {
                        String message;
                        Exception exception = task.getException();
                        if (exception == null) {
                            message = "Sign in fail.";
                        } else {
                            String exceptionType;
                            // FirebaseAuthInvalidCredentialsException代表帳號驗證不成功，例如email格式不正確
                            if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                exceptionType = "Invalid Credential";
                            }
                            // FirebaseAuthInvalidUserException代表無此user，例如帳密錯誤
                            else if (exception instanceof FirebaseAuthInvalidUserException) {
                                exceptionType = "Invalid User";
                            } else {
                                exceptionType = exception.getClass().toString();
                            }
                            message = exceptionType + ": " + exception.getLocalizedMessage();
                        }
                        tvError.setText(message);
                        tvError.setTextColor(Color.RED);
                    }
                });
    }

    private void savePassword() {
        // 修改指定 ID 的文件
        db.collection("password")
                .document(auth.getCurrentUser().getUid()).set(pass);
    }

}