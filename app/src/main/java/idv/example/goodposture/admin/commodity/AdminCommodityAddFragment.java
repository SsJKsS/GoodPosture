package idv.example.goodposture.admin.commodity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
    private String toolbarTitle;

    private Toolbar toolbar;
    private EditText etName;
    private RadioButton radioFood;
    private RadioButton radioEquipment;
    private RadioGroup radioType;
    private EditText etPrice;
    private EditText etStock;
    private EditText etDescription;
    private ImageView ivPicture;

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private File file;
    private Uri contentUri; // ???????????????Uri
    private Uri cropImageUri; // ?????????Uri
    private boolean pictureTaken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (AppCompatActivity) getActivity();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

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
        if (getArguments() != null) {
            //??????????????????
            product = (Product) getArguments().getSerializable("product");
            if (product.getPicturePath() == null) {
                ivPicture.setImageResource(R.drawable.no_product_image);
            } else {
                showImage(ivPicture, product.getPicturePath());
            }
            etName.setText(product.getName());
            if(product.getType()==1){
                radioFood.setChecked(true);
            }else{
                radioEquipment.setChecked(true);
            }
            etPrice.setText(product.getPrice()+"");
            etStock.setText(product.getStock()+"");
            etDescription.setText(product.getDescription());
            toolbarTitle = "????????????";
        } else {
            //????????????
            product = new Product();
            String id = db.collection("product").document().getId();
            product.setId(id);
            toolbarTitle = "????????????";
        }

        radioType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_food) {
                product.setType(1);
            } else if (checkedId == R.id.radio_equipment) {
                product.setType(2);
            }
        });

        handleToolbar();
        handlePicture();
        fillOutDate();
    }

    private void fillOutDate() {
        toolbar.setOnClickListener(v -> {
            etName.setText("???????????????11??????");
            radioEquipment.setChecked(true);
            product.setType(2);
            etPrice.setText("450");
            etStock.setText("22");
            etDescription.setText("??????\n" +
                    "5????????????????????????\n" +
                    "????????????????????????\n" +
                    "????????????????????????\n"+"?????????????????????*1????????????*5??????????????????*2???????????????*2???????????????*1");
        });
    }

    private void findViews(View view) {
        toolbar = view.findViewById(R.id.tb_add_product_context);
        etName = view.findViewById(R.id.et_com_name);
        radioFood = view.findViewById(R.id.radio_food);
        radioEquipment = view.findViewById(R.id.radio_equipment);
        radioType = view.findViewById(R.id.radio_type);
        etPrice = view.findViewById(R.id.et_com_price);
        etStock = view.findViewById(R.id.et_com_stock);
        etDescription = view.findViewById(R.id.et_com_description);
        ivPicture = view.findViewById(R.id.iv_com_picture);
    }

    private void handleToolbar() {
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle(toolbarTitle);
        toolbar.setTitleTextColor(Color.WHITE);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        }
    }

    //??????ToolBar???menu??????
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //??????menu
        inflater.inflate(R.menu.admin_add_product_menu, menu);
    }

    //??????menu??????????????? //???????????????????????????????????????
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_toolbar_send) {

            //???????????????????????????
            String name = String.valueOf(etName.getText());
            String price = String.valueOf(etPrice.getText());
            String stock = String.valueOf(etStock.getText());
            String description = String.valueOf(etDescription.getText());

            if (name.isEmpty()) {
                etName.setError("???????????????");
            }
            if (price.isEmpty()) {
                etPrice.setError("???????????????");
            }
            if (stock.isEmpty()) {
                etStock.setError("???????????????");
            }
            if (description.isEmpty()) {
                etDescription.setError("?????????????????????");
            }
            if (product.getType() != 1 && product.getType() != 2) {
                Toast.makeText(activity, "?????????????????????" + product.getType(), Toast.LENGTH_SHORT).show();
                return false;
            }

            //???????????????????????????????????????????????????
            product.setDate(new Date(System.currentTimeMillis()));
            product.setName(name);
            product.setPrice(Double.parseDouble(price));
            product.setStock(Integer.parseInt(stock));
            product.setDescription(description);
            //??????id??????bundle????????????
            //??????????????????
            //?????????????????????????????????:0???????????????????????????????????????????????????

            // ???????????????????????????Firebase storage
            if (pictureTaken) {
                // document ID??????image path?????????????????????????????????????????????
                final String imagePath = "GoodPosture" + "/product/" + product.getId();
                storage.getReference().child(imagePath).putFile(cropImageUri)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Product image is uploaded");
                                // ??????????????????????????????????????????spot??????????????????document???
                                product.setPicturePath(imagePath);
                            } else {
                                String message = task.getException() == null ?
                                        "Product upload failed" :
                                        task.getException().getMessage();
                                Log.e(TAG, "message: " + message);
                                //Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                            }
                            //????????????????????????????????????????????????????????????????????????
                            addOrReplace(product);
                        });
            } else {
                addOrReplace(product);
            }
            return true;
        } else if (itemId == android.R.id.home) {
            Navigation.findNavController(toolbar).popBackStack();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void handlePicture() {
        ivPicture.setOnClickListener(v -> {
            showBottomSheetDialog();
        });
    }

    //???????????????????????????bottomSheet
    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetDialog.setContentView(R.layout.admin_bottom_sheet_take_picture);

        Button btTakePicture = bottomSheetDialog.findViewById(R.id.bt_take_picture);
        Button btPickPicture = bottomSheetDialog.findViewById(R.id.bt_pick_picture);

        //??????????????????
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

        //?????????
        btPickPicture.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickPictureLauncher.launch(intent);
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.show();
    }



    // ???????????????Firestore????????????
    private void addOrReplace(final Product product) {
        // ??????Firestore?????????ID???Document??????????????????????????????????????????
        db.collection("product").document(product.getId()).set(product)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String message = "Product is inserted"
                                + " with ID: " + product.getId();
                        Log.d(TAG, message);
                        //Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        // ???????????????????????????????????????
                        NavController navController = Navigation.findNavController(toolbar);
                        navController.navigate(R.id.action_adminCommodityAddFragment_to_adminCommodityFragment);
                    } else {
                        String message = task.getException() == null ?
                                "Insert failed" :
                                task.getException().getMessage();
                        Log.e(TAG, "message: " + message);
                        //Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ??????Firebase storage?????????????????????ImageView???
    private void showImage(final ImageView imageView, final String path) {
        final int ONE_MEGABYTE = 1024 * 1024;
        StorageReference imageRef = storage.getReference().child(path);
        //byte[]??????bitmap?????????bitmap
        imageRef.getBytes(ONE_MEGABYTE)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        byte[] bytes = task.getResult();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageView.setImageBitmap(bitmap);
                    } else {
                        String message = task.getException() == null ?
                                getString(R.string.textImageDownloadFail) + ": " + path :
                                task.getException().getMessage() + ": " + path;
                        Log.e(TAG, message);
                        //Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    //--------------------???????????????/????????????---------------------
    //???????????????
    ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::takePictureResult);
    //??????????????????
    ActivityResultLauncher<Intent> pickPictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::pickPictureResult);
    //?????????????????????
    ActivityResultLauncher<Intent> cropPictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::cropPictureResult);

    //?????????????????????
    private void takePictureResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            crop(contentUri);
        }
    }

    //?????????????????????
    private void pickPictureResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            if (result.getData() != null) {
                crop(result.getData().getData());
            }
        }
    }

    //??????????????????INTENT
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

    // ???????????????????????????????????????????????????????????????????????????
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