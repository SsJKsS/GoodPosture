package idv.example.goodposture.user.home;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import idv.example.goodposture.R;

public class HomeRegisterFragment extends Fragment {
    private ImageView ivFastAccount;
    private EditText etAccount;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btRegister;
    private TextView tvError;
    private Activity activity;
    private FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleButton();
    }

    private void findViews(View view) {
        ivFastAccount = view.findViewById(R.id.iv_fastAccount);
        etAccount = view.findViewById(R.id.et_registerAccount);
        etPassword = view.findViewById(R.id.et_registerPassword);
        etConfirmPassword = view.findViewById(R.id.et_confirmPassword);
        btRegister = view.findViewById(R.id.bt_register);
        tvError = view.findViewById(R.id.tv_rError);
    }

    private void handleButton() {
        ivFastAccount.setOnClickListener(view -> {
            etAccount.setText("good@gmail.com");
            etPassword.setText("111111");
            etConfirmPassword.setText("111111");
        });

        btRegister.setOnClickListener(view -> {
            final String account = String.valueOf(etAccount.getText());
            final String password = String.valueOf(etPassword.getText());
            final String confirmPassword = String.valueOf(etConfirmPassword.getText());
            if ("a".equals(account) && "a".equals(password) && "a".equals(confirmPassword)) {
                // ??????NavController??????
                NavController navController = Navigation.findNavController(view);
                // ????????????
                navController.navigate(R.id.actionRegisterToRegisterSuccess);
            } else if ("z".equals(account)) {
                etAccount.setError("??????????????????");
            }

            // ????????????????????????
            if (account.isEmpty()) {
                etAccount.setError("???????????????");
            }

            // ????????????????????????
            if (password.isEmpty()) {
                etPassword.setError("???????????????");
            }

            // ??????????????????????????????
            if (confirmPassword.isEmpty()) {
                etConfirmPassword.setError("?????????????????????");
            }
            // ?????? firebase ??????
            else {
                signUp(account, password);
            }
        });

    }

    private void signUp(String email, String password) {
        /* ??????user?????????email???password?????????????????? */
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    // ?????????????????????????????????????????????????????????
                    if (task.isSuccessful()) {
                        Navigation.findNavController(etAccount)
                                .navigate(R.id.actionRegisterToRegisterSuccess);
                    } else {
                        String message;
                        Exception exception = task.getException();
                        if (exception == null) {
                            message = "Register fail.";
                        } else {
                            String exceptionType;
                            // FirebaseAuthInvalidCredentialsException ????????????????????????????????????email???????????????
                            if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                exceptionType = "Invalid Credential";
                            }
                            // FirebaseAuthInvalidUserException ????????????user?????????????????????
                            else if (exception instanceof FirebaseAuthInvalidUserException) {
                                exceptionType = "Invalid User";
                            }
                            // FirebaseAuthUserCollisionException ???????????????????????????
                            else if (exception instanceof FirebaseAuthUserCollisionException) {
                                exceptionType = "User Collision";
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

}