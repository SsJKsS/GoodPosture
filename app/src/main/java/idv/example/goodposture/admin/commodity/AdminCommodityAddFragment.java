package idv.example.goodposture.admin.commodity;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.sql.Date;

import idv.example.goodposture.R;
import idv.example.goodposture.user.shopping.Product;

import static android.app.Activity.RESULT_OK;

public class AdminCommodityAddFragment extends Fragment {
    private static final String TAG = "TAG_AdminCommodityAddFragment";
    private AppCompatActivity activity;
    private Product product;

    private ImageView ivBack;
    private TextView tvSend;
    private EditText etName;
    //    private RadioButton radioFood;
//    private RadioButton radioEquipment;
    private RadioGroup radioType;
    private EditText etPrice;
    private EditText etStock;
    private EditText etDescription;
    private ImageView ivPicture;

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private File file;
    private Uri contentUri; // 拍照需要的Uri
    private Uri cropImageUri; // 截圖的Uri
    private boolean pictureTaken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        product = new Product();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_commodity_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handlePicture();
        insertProduct();
    }

    private void findViews(View view) {
        ivBack = view.findViewById(R.id.iv_com_back);
        tvSend = view.findViewById(R.id.tv_com_send);
        etName = view.findViewById(R.id.et_com_name);
//        radioFood = view.findViewById(R.id.radio_food);
//        radioEquipment = view.findViewById(R.id.radio_equipment);
        radioType = view.findViewById(R.id.radio_type);
        etPrice = view.findViewById(R.id.et_com_price);
        etStock = view.findViewById(R.id.et_com_stock);
        etDescription = view.findViewById(R.id.et_com_description);
        ivPicture = view.findViewById(R.id.iv_com_picture);
    }

    private void handlePicture() {
        ivPicture.setOnClickListener(v -> {
            showBottomSheetDialog();
        });
    }

    //點擊直接購買會跳出bottomSheet
    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetDialog.setContentView(R.layout.admin_bottom_sheet_take_picture);

        Button btTakePicture = bottomSheetDialog.findViewById(R.id.bt_take_picture);
        Button btPickPicture = bottomSheetDialog.findViewById(R.id.bt_pick_picture);

        //開啟內建相機
        btTakePicture.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File dir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (dir != null && !dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.e(TAG, "DIRECTORY_PICTURES cannot be created!");
                    return;
                }
            }
            file = new File(dir, "picture.jpg");
            contentUri = FileProvider.getUriForFile(
                    activity, activity.getPackageName() + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            try {
                takePictureLauncher.launch(intent);
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, "No camera app found");
            }
            bottomSheetDialog.dismiss();
        });

        //選照片
        btPickPicture.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickPictureLauncher.launch(intent);
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.show();
    }

    private void insertProduct() {
        radioType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_food) {
                product.setType(1);
            } else if (checkedId == R.id.radio_equipment) {
                product.setType(2);
            }
        });
        tvSend.setOnClickListener(view -> {
            //先判斷是否有填資料
            String name = String.valueOf(etName.getText());
            String price = String.valueOf(etPrice.getText());
            String stock = String.valueOf(etStock.getText());
            String description = String.valueOf(etDescription.getText());

            if (name.isEmpty()) {
                etName.setError("請輸入名稱");
            }
            if (price.isEmpty()) {
                etPrice.setError("請輸入價格");
            }
            if (stock.isEmpty()) {
                etStock.setError("請輸入庫存");
            }
            if (description.isEmpty()) {
                etDescription.setError("請輸入商品描述");
            }
            if (product.getType() != 1 && product.getType() != 2) {
                Toast.makeText(activity, "請輸入商品種類", Toast.LENGTH_SHORT).show();
                return;
            }

            //判斷商品資料不為空後，開始設定商品
            String id = db.collection("product").document().getId();
            product.setId(id);
            product.setDate(new Date(System.currentTimeMillis()));
            product.setName(name);
            product.setPrice(Double.parseDouble(price));
            product.setStock(Integer.parseInt(stock));
            product.setDescription(description);
            //種類已設定完
            //如果更新商品的話，這邊要再想一下
            product.setSellAmount(0);

            // 如果有拍照，上傳至Firebase storage
            if (pictureTaken) {
                // document ID成為image path一部分，避免與其他圖檔名稱重複
                final String imagePath = "GoodPosture" + "/product/" + product.getId();
                storage.getReference().child(imagePath).putFile(cropImageUri)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Product image is uploaded");
                                // 圖檔新增成功再將圖檔路徑存入spot物件所代表的document內
                                product.setPicturePath(imagePath);
                            } else {
                                String message = task.getException() == null ?
                                        "Product upload failed" :
                                        task.getException().getMessage();
                                Log.e(TAG, "message: " + message);
                                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                            }
                            //無論圖檔上傳成功或失敗都要將文字資料新增至資料庫
                            addOrReplace(product);
                        });
            } else {
                addOrReplace(product);
            }
        });

        //按下返回鑑 = 取消
        ivBack.setOnClickListener(view -> {
            //dialog
            Navigation.findNavController(ivBack).popBackStack();
        });
    }

    // 新增或修改Firestore上的景點
    private void addOrReplace(final Product product) {
        // 如果Firestore沒有該ID的Document就建立新的，已經有就更新內容
        db.collection("product").document(product.getId()).set(product)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String message = "Product is inserted"
                                + " with ID: " + product.getId();
                        Log.d(TAG, message);
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        // 商品新增完畢，返回商品列表
                        NavController navController = Navigation.findNavController(tvSend);
                        navController.navigate(R.id.action_adminCommodityAddFragment_to_adminCommodityFragment);
                    } else {
                        String message = task.getException() == null ?
                                "Insert failed" :
                                task.getException().getMessage();
                        Log.e(TAG, "message: " + message);
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //--------------------以下跟拍照/照片有關---------------------
    //拍照的跳轉
    ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::takePictureResult);
    //選照片的跳轉
    ActivityResultLauncher<Intent> pickPictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::pickPictureResult);
    //裁切照片的跳轉
    ActivityResultLauncher<Intent> cropPictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::cropPictureResult);

    //拍照後去才裁切
    private void takePictureResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            crop(contentUri);
        }
    }

    //選照片後去裁切
    private void pickPictureResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            if (result.getData() != null) {
                crop(result.getData().getData());
            }
        }
    }

    //跳轉到裁切的INTENT
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

    // 拍照或挑選照片完畢後都會截圖，截圖完畢會呼叫此方法
    private void cropPictureResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            cropImageUri = UCrop.getOutput(result.getData());
            if (cropImageUri != null) {
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(
                            activity.getContentResolver().openInputStream(cropImageUri));
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                if (bitmap != null) {
                    ivPicture.setImageBitmap(bitmap);
                    pictureTaken = true;
                } else {
                    ivPicture.setImageResource(R.drawable.no_product_image);
                    pictureTaken = false;
                }
            }
        }
    }

}