package idv.example.goodposture.admin.commodity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;

import idv.example.goodposture.R;
import idv.example.goodposture.user.PreActivity;
import idv.example.goodposture.user.shopping.Product;

public class AdminCommodityContextFragment extends Fragment {
    private static final String TAG = "TAG_AdminCommodityContextFragment";
    private AppCompatActivity activity;
    private Product product;

    private Toolbar toolbar;
    private ImageView ivProduct;
    private TextView tvProductId;
    private TextView tvProductName;
    private TextView tvProductPrice;
    private TextView tvProductDate;
    private TextView tvProductType;
    private TextView tvProductStock;
    private TextView tvProductSellAmount;
    private TextView tvProductDesc;

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (AppCompatActivity) getActivity();
        product = (Product) (getArguments() != null ? getArguments().getSerializable("product") : null);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_commodity_context, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleToolbar();
        showProduct();
    }

    private void findViews(View view) {
        toolbar = view.findViewById(R.id.tb_admin_context);
        ivProduct = view.findViewById(R.id.iv_product);
        tvProductId = view.findViewById(R.id.tv_product_id);
        tvProductName = view.findViewById(R.id.tv_product_name);
        tvProductPrice = view.findViewById(R.id.tv_product_price);
        tvProductDate = view.findViewById(R.id.tv_product_date);
        tvProductType = view.findViewById(R.id.tv_product_type);
        tvProductStock= view.findViewById(R.id.tv_product_stock);
        tvProductSellAmount = view.findViewById(R.id.tv_product_sell_amount);
        tvProductDesc = view.findViewById(R.id.tv_product_desc);
    }

    private void handleToolbar() {
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle("");
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        }
    }

    //建立ToolBar的menu選單
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //載入menu
        inflater.inflate(R.menu.admin_commodity_context_menu, menu);
    }

    //覆寫menu選項的監聽 //返回鑑被視為功能選單的選項
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(toolbar);
        int itemId = item.getItemId();
        if (itemId == R.id.menu_toolbar_edit) {
            //建立navController// 跳至shoppingCart頁面
            Bundle bundle = new Bundle();
            bundle.putSerializable("product", product);
            navController.navigate(R.id.action_adminCommodityContextFragment_to_adminCommodityAddFragment,bundle);
            return true;
        } else if (itemId == android.R.id.home) {
            Navigation.findNavController(toolbar).popBackStack();
            return true;
        } else if (itemId == R.id.menu_toolbar_delete) {
            //deleteProduct();
            deleteDialog();

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

//    private void deleteProduct() {
//
//    }

    //跳出視窗讓使用者確認是否刪除商品
    private void deleteDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage("是否確定刪除?");
        alertDialog.setPositiveButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.setNegativeButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.collection("product").document(product.getId()).delete();
                Navigation.findNavController(toolbar).popBackStack();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    //顯示商品畫面
    private void showProduct() {
        if(product != null){
            tvProductId.append(product.getId());
            tvProductName.append(product.getName());
            tvProductPrice.append(String.format("$%s", product.getPrice()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            tvProductDate.append(sdf.format(product.getDate()));
            if(product.getType() == 1){
                tvProductType.append("食品");
            }else{
                tvProductType.append("器材");
            }
            tvProductStock.append(product.getStock()+"");
            tvProductSellAmount.append(product.getSellAmount()+"");
            tvProductDesc.setText(product.getDescription());
        }
        if(product.getPicturePath() != null){
            showImage(ivProduct, product.getPicturePath());
        }else{
            ivProduct.setImageResource(R.drawable.no_product_image);
        }
    }

    // 下載Firebase storage的照片並顯示在ImageView上
    private void showImage(final ImageView imageView, final String path) {
        final int ONE_MEGABYTE = 1024 * 1024;
        StorageReference imageRef = storage.getReference().child(path);
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
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }


}