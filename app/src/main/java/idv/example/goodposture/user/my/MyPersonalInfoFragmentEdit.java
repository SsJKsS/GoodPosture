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
            etMyInfoName.setText("黃美好");
            etMyInfoNickname.setText("美好");
            etMyInfoPhone.setText("0977777777");
        });
    }

    //顯示返回鍵
    private void handleToolbar() {
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle("修改基本資料");
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //覆寫menu選項的監聽
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
//                .withAspectRatio(16, 9) // 設定裁減比例
//                .withMaxResultSize(500, 500) // 設定結果尺寸不可超過指定寬高
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
        // 取得storage根目錄位置
        StorageReference rootRef = storage.getReference();
        final String imagePath = getString(R.string.app_name)
                + "/images/"
                + Objects.requireNonNull(auth.getCurrentUser()).getUid();
        // 建立當下目錄的子路徑
        final StorageReference imageRef = rootRef.child(imagePath);
        // 將儲存在uri的照片上傳
        imageRef.putFile(imageUri)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String message = "Upload successfully";
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        // 下載剛上傳好的照片
                        downloadImage(imagePath);
                    } else {
                        String message = task.getException() == null ?
                                "Upload fail" : task.getException().getMessage();
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 下載Firebase storage的照片
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
        // 查詢身體資訊集合
        db.collection("body_info")
                .document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                // 獲取資料
                .get()
                // 設置網路傳輸監聽器
                .addOnCompleteListener(bodyInfoTask -> {
                    if (bodyInfoTask.isSuccessful() && bodyInfoTask.getResult() != null) {
                        // 將獲取的資料存成自定義類別
                        // for (DocumentSnapshot documentSnapshot : task.getResult())
                        DocumentSnapshot documentSnapshot = bodyInfoTask.getResult();
                        bodyinfo = documentSnapshot.toObject(Bodyinfo.class);
                        assert bodyinfo != null;
                        if (bodyinfo.getGender().equals("male")) {
                            tvMyInfoGender.setText("男");
                        } else {
                            tvMyInfoGender.setText("女");
                        }

                    } else {
                        String message = bodyInfoTask.getException() == null ?
                                "查無資料" :
                                bodyInfoTask.getException().getMessage();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleView() {
        // 搜尋條件 (搜尋當下登入的 uid 在 db 裡是否有資料)
        DocumentReference queRef = db.collection("my_info")
                .document(Objects.requireNonNull(auth.getCurrentUser()).getUid());
        // 搜尋條件獲取資料 & 設置監聽器
        queRef.get().addOnCompleteListener(queryTask -> {
            // 獲取資料成功
            if (queryTask.isSuccessful()) {
                // 藉由 getResult() 將獲取到的資料塞至變數
                DocumentSnapshot document = queryTask.getResult();
                // 獲取到的條件資料是空的，顯示無資料
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
                            showImage(ivMyEditAvatar, myinfo.getImagePath());
                        }
                        etMyInfoName.setText(myinfo.getName());
                        etMyInfoNickname.setText(myinfo.getNickname());
                        etMyInfoPhone.setText(myinfo.getPhone());
                        etMyInfoBirth.setText(myinfo.getBirth());
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

    private void handleCalendar() {
        ivCalendar.setOnClickListener(v -> {
            // 1. 取得Calendar物件
            Calendar calendar = Calendar.getInstance();
            // 2. 實例化DatePickerDialog物件
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    activity,
                    this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            // 設定可選取日期區間
            // 取得DatePicker物件
            DatePicker datePicker = datePickerDialog.getDatePicker();
            // 設定可選取的最大日期
            datePicker.setMaxDate(new Date().getTime());
            // 顯示對話框
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

            // 判斷名字不可為空
            if (name.isEmpty()) {
                etMyInfoName.setError("請輸入名字");
            }

            // 判斷暱稱不可為空
            if (nickname.isEmpty()) {
                etMyInfoNickname.setError("請輸入暱稱");
            }

            // 判斷電話不可為空
            if (phone.isEmpty()) {
                etMyInfoPhone.setError("請輸入電話");
            }

            // 判斷生日不可為空
            if (birth.isEmpty()) {
                etMyInfoBirth.setError("請輸入生日");
            } else {
                // 將文件 ID (UID) 設為 ID
                myinfo.setId(auth.getCurrentUser().getUid());
                // 個人資訊
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
        // 修改指定 ID 的文件
        db.collection("my_info").document(myinfo.getId()).set(myinfo)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String message = "修改成功 with ID: " + myinfo.getId();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        // 修改完畢跳轉至顯示個人資訊頁面
                        NavController navController = Navigation.findNavController(etMyInfoName);
                        navController.popBackStack();
                    } else {
                        String message = task.getException() == null ?
                                "修改失敗" : task.getException().getMessage();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}