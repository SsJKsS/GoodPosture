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
         * Intent ??? Activity ??? Activity ???????????????????????????????????? Fragment ????????? Activity???
         * ???????????? Fragment ?????? getActivity() ???????????? Activity
         */
        btLogin.setOnClickListener(view -> {
            final String account = String.valueOf(etAccount.getText());
            final String password = String.valueOf(etPassword.getText());

            if ("admin".equals(account) && "admin".equals(password)) {
                // ?????????????????????
                Intent intent = new Intent(getActivity(), AdminActivity.class);
                startActivity(intent);
            }

            // ????????????????????????
            if (account.isEmpty()) {
                etAccount.setError("???????????????");
            }

            // ????????????????????????
            if (password.isEmpty()) {
                etPassword.setError("???????????????");
            }
            // ?????? firebase ??????
            else if (!"admin".equals(account) && !"admin".equals(password)){
                signIn(account, password);
                pass.setPassword(password);
            }
        });
    }

    private void signIn(String email, String password) {
        // ??????user?????????email???password??????
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    // ????????????
                    if (task.isSuccessful()) {
                        // ???????????? (????????????????????? uid ??? db ??????????????????)
//                        Query queRef = db.collection("body_info")
//                                .whereEqualTo("uid", auth.getCurrentUser().getUid());
                        DocumentReference queRef = db.collection("body_info")
                                .document(auth.getCurrentUser().getUid());
                        // ???????????????????????? & ???????????????
                        queRef.get().addOnCompleteListener(queryTask -> {
                            // ??????????????????
                            if (queryTask.isSuccessful()) {
                                // ?????? getResult() ?????????????????????????????????
//                                QuerySnapshot document = queryTask.getResult();
                                DocumentSnapshot document = queryTask.getResult();
                                // ?????????????????????????????????????????????????????????????????????
                                assert document != null;
                                if (!document.exists()) {
                                    Navigation.findNavController(etAccount)
                                            .navigate(R.id.actionLoginToTypeBodyInfo);
                                } else {
                                    // ??????????????????????????????????????????????????????????????????????????????????????????
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
                        // ????????????????????????????????? db???????????????????????????
                        savePassword();
                    // ??????????????????????????????
                    } else {
                        String message;
                        Exception exception = task.getException();
                        if (exception == null) {
                            message = "Sign in fail.";
                        } else {
                            String exceptionType;
                            // FirebaseAuthInvalidCredentialsException????????????????????????????????????email???????????????
                            if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                exceptionType = "Invalid Credential";
                            }
                            // FirebaseAuthInvalidUserException????????????user?????????????????????
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
        // ???????????? ID ?????????
        db.collection("password")
                .document(auth.getCurrentUser().getUid()).set(pass);
    }

}