package idv.example.goodposture.user.my;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import idv.example.goodposture.R;
import idv.example.goodposture.user.home.Bodyinfo;

public class MyPersonalInfoFragmentEdit extends Fragment implements DatePickerDialog.OnDateSetListener {
    private AppCompatActivity activity;
    private Toolbar toolbar;
    private ImageView ivMyEditAvatar, ivCalendar;
    private EditText etMyInfoName, etMyInfoNickname, etMyInfoPhone, etMyInfoBirth;
    private TextView tvMyInfoName, tvMyInfoAccount, tvMyInfoGender, tvSubmitMyInfo;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Uri contentUri;
    private Bodyinfo bodyinfo;
    private Myinfo myinfo;

    ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::takePictureResult);

    ActivityResultLauncher<Intent> cropPictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::cropPictureResult);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        setHasOptionsMenu(true);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        myinfo = new Myinfo();
        bodyinfo = new Bodyinfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_personal_info_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleToolbar();
        handleCamera();
        setCommonInfo();
        handleView();
        handleCalendar();
        handleSubmit();
    }

    private void findViews(View view) {
        toolbar = view.findViewById(R.id.tb_my);
        ivMyEditAvatar = view.findViewById(R.id.iv_my_edit_avatar);
        tvMyInfoName = view.findViewById(R.id.my_info_name);
        ivCalendar = view.findViewById(R.id.iv_calendar);
        etMyInfoName = view.findViewById(R.id.et_my_info_name_content);
        etMyInfoNickname = view.findViewById(R.id.et_my_info_nickname_content);
        etMyInfoPhone = view.findViewById(R.id.et_my_info_telephone_content);
        etMyInfoBirth = view.findViewById(R.id.et_my_info_birth_content);
        tvMyInfoAccount = view.findViewById(R.id.tv_my_info_account_content);
        tvMyInfoGender = view.findViewById(R.id.tv_my_info_gender_content);
        tvSubmitMyInfo = view.findViewById(R.id.tv_submitMyInfo);

        tvMyInfoName.setOnClickListener(v -> {
            etMyInfoName.setText("?????????");
            etMyInfoNickname.setText("??????");
            etMyInfoPhone.setText("0977777777");
        });
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
        if (itemId == R.id.my_personal_info_edit) {
            NavController navController = Navigation.findNavController(toolbar);
            navController.navigate(R.id.action_fragmentShopping_to_shoppingCartFragment);
            return true;
        } else if (itemId == android.R.id.home) {
            Navigation.findNavController(toolbar).popBackStack();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void handleCamera() {
        ivMyEditAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File dir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (dir != null && !dir.exists()) {
                if (!dir.mkdirs()) {
                    return;
                }
            }
            File file = new File(dir, "picture.jpg");
            contentUri = FileProvider.getUriForFile(
                    activity, activity.getPackageName() + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            try {
                takePictureLauncher.launch(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(activity, "No camera app found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void takePictureResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            crop(contentUri);
        }
    }

    private void crop(Uri sourceImageUri) {
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg");
        Uri destinationUri = Uri.fromFile(file);
        Intent cropIntent = UCrop.of(sourceImageUri, destinationUri)
//                .withAspectRatio(16, 9) // ??????????????????
//                .withMaxResultSize(500, 500) // ??????????????????????????????????????????
                .getIntent(activity);
        cropPictureLauncher.launch(cropIntent);
    }

    private void cropPictureResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Uri imageUri = UCrop.getOutput(result.getData());
            if (imageUri != null) {
                uploadImage(imageUri);
            }
        }
    }

    private void uploadImage(Uri imageUri) {
        // ??????storage???????????????
        StorageReference rootRef = storage.getReference();
        final String imagePath = getString(R.string.app_name)
                + "/images/"
                + Objects.requireNonNull(auth.getCurrentUser()).getUid();
        // ??????????????????????????????
        final StorageReference imageRef = rootRef.child(imagePath);
        // ????????????uri???????????????
        imageRef.putFile(imageUri)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String message = "Upload successfully";
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        // ???????????????????????????
                        downloadImage(imagePath);
                    } else {
                        String message = task.getException() == null ?
                                "Upload fail" : task.getException().getMessage();
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ??????Firebase storage?????????
    private void downloadImage(String path) {
        final int ONE_MEGABYTE = 1024 * 1024;
        StorageReference imageRef = storage.getReference().child(path);
        imageRef.getBytes(ONE_MEGABYTE)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        byte[] bytes = task.getResult();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        ivMyEditAvatar.setImageBitmap(bitmap);
                    } else {
                        String message = task.getException() == null ?
                                "Download fail" : task.getException().getMessage();
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void setCommonInfo() {
        tvMyInfoAccount
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
                            tvMyInfoGender.setText("???");
                        } else {
                            tvMyInfoGender.setText("???");
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
                    ivMyEditAvatar.setImageResource(R.drawable.no_image);
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
                            showImage(ivMyEditAvatar, myinfo.getImagePath());
                        }
                        etMyInfoName.setText(myinfo.getName());
                        etMyInfoNickname.setText(myinfo.getNickname());
                        etMyInfoPhone.setText(myinfo.getPhone());
                        etMyInfoBirth.setText(myinfo.getBirth());
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

    private void handleCalendar() {
        ivCalendar.setOnClickListener(v -> {
            // 1. ??????Calendar??????
            Calendar calendar = Calendar.getInstance();
            // 2. ?????????DatePickerDialog??????
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    activity,
                    this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            // ???????????????????????????
            // ??????DatePicker??????
            DatePicker datePicker = datePickerDialog.getDatePicker();
            // ??????????????????????????????
            datePicker.setMaxDate(new Date().getTime());
            // ???????????????
            datePickerDialog.show();
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        final String text = "" + year + "/" + (month + 1) + "/" + dayOfMonth;
        etMyInfoBirth.setText(text);
    }

    private void handleSubmit() {
        tvSubmitMyInfo.setOnClickListener(v -> {
            final String name = String.valueOf(etMyInfoName.getText());
            final String account = String.valueOf(tvMyInfoAccount.getText());
            final String nickname = String.valueOf(etMyInfoNickname.getText());
            final String gender = String.valueOf(tvMyInfoGender.getText());
            final String phone = String.valueOf(etMyInfoPhone.getText());
            final String birth = String.valueOf(etMyInfoBirth.getText());

            // ????????????????????????
            if (name.isEmpty()) {
                etMyInfoName.setError("???????????????");
            }

            // ????????????????????????
            if (nickname.isEmpty()) {
                etMyInfoNickname.setError("???????????????");
            }

            // ????????????????????????
            if (phone.isEmpty()) {
                etMyInfoPhone.setError("???????????????");
            }

            // ????????????????????????
            if (birth.isEmpty()) {
                etMyInfoBirth.setError("???????????????");
            } else {
                // ????????? ID (UID) ?????? ID
                myinfo.setId(auth.getCurrentUser().getUid());
                // ????????????
                final String imagePath = getString(R.string.app_name)
                        + "/images/"
                        + Objects.requireNonNull(auth.getCurrentUser()).getUid();
                myinfo.setImagePath(imagePath);
                myinfo.setName(name);
                myinfo.setAccount(account);
                myinfo.setNickname(nickname);
                myinfo.setGender(gender);
                myinfo.setPhone(phone);
                myinfo.setBirth(birth);
                modify(myinfo);
            }
        });
    }

    private void modify(Myinfo myinfo) {
        // ???????????? ID ?????????
        db.collection("my_info").document(myinfo.getId()).set(myinfo)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String message = "???????????? with ID: " + myinfo.getId();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        // ?????????????????????????????????????????????
                        NavController navController = Navigation.findNavController(etMyInfoName);
                        navController.popBackStack();
                    } else {
                        String message = task.getException() == null ?
                                "????????????" : task.getException().getMessage();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}