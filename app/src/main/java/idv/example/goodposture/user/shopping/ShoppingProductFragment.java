package idv.example.goodposture.user.shopping;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import idv.example.goodposture.R;


public class ShoppingProductFragment extends Fragment {
    private static final String TAG = "TAG_ShoppingProductFragment";
    private AppCompatActivity activity;
    //資料
    private Product product;    //從ShoppingList傳過來的Product物件
    private CartDetail cartDetail;
    //元件
    private Toolbar toolbar;
    private ImageView ivProduct;
    private TextView tvProductName;
    private TextView tvProductPrice;
    private TextView tvProductDesc;
    private Button btAddToCart;
    private Button btBuyProduct;
    private ViewGroup mRootView;    //為了動畫建立的
    //firebase
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        product = (Product) (getArguments() != null ? getArguments().getSerializable("product") : null);
        activity = (AppCompatActivity) getActivity();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        cartDetail = new CartDetail();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shopping_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleToolbar();
        showProduct();
        addToCart();
        buyProduct();
    }

    private void findViews(View view) {
        toolbar = view.findViewById(R.id.tb_shopping);
        ivProduct = view.findViewById(R.id.iv_product);
        tvProductName = view.findViewById(R.id.tv_product_name);
        tvProductPrice = view.findViewById(R.id.tv_product_price);
        tvProductDesc = view.findViewById(R.id.tv_product_description);
        btAddToCart = view.findViewById(R.id.bt_add_to_Cart);
        btBuyProduct = view.findViewById(R.id.bt_buy_product);

        mRootView = (ViewGroup) activity.getWindow().getDecorView();
    }

    private void handleToolbar() {
        activity.setSupportActionBar(toolbar);
        //ToolBar的標題預設是AndroidManifest檔案中<Application/>標籤下屬性label設定的值
        toolbar.setTitle("");
        //設定toolbar為狀態列
        activity.setSupportActionBar(toolbar);
        //取用返回鑑的方法在actionBar
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //建立ToolBar的menu選單
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //載入menu
        inflater.inflate(R.menu.shopping_cart_toolbar_menu, menu);
        //Log.d("onCreateOptionsMenu","success");
    }
    //返回鑑被視為功能選單的選項
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        final int itemId = item.getItemId();
        if(itemId == android.R.id.home){
            Navigation.findNavController(ivProduct).popBackStack();
        }else if(itemId == R.id.menu_toolbar_cart){
            NavController navController = Navigation.findNavController(toolbar);
            navController.navigate(R.id.action_shoppingProductFragment_to_shoppingCartFragment);
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
        return true;
    }


    //顯示商品畫面
    private void showProduct() {
        if(product != null){
            tvProductName.setText(product.getName());
            tvProductPrice.setText(String.format("$%s", product.getPrice()));
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

    //加入購物車，顯示加入的動畫|改變購物車icon的圖案
    private void addToCart() {
        btAddToCart.setOnClickListener(v -> {
            //todo
            //顯示加入購物車的動畫|改變購物車icon的圖案
            playAnim(btAddToCart);
            //get()會抓db所有資料
            db.collection("cartDetail").get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            //task有結果表示cartDetail有文件
                            if(task.getResult() != null){
                                //把每個document轉成object物件
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    CartDetail cartDetailInDb = document.toObject(CartDetail.class);
                                    //當前使用者的cartDetail && 這個商品已經存在購物車 ==> 更新這筆資料
                                    if (cartDetailInDb.getUid().equals(auth.getCurrentUser().getUid()) &&
                                            cartDetailInDb.getProductId().equals(product.getId())) {
                                        Log.d(TAG,"當前使用者的cartDetail && 這個商品已經存在購物車");
                                        updateCartDetail(cartDetailInDb);
                                        return;
                                    }
                                }
                                //當前使用者從沒有新增此商品到資料庫時，就可以直接insert一筆cartDetail進去資料庫
                                Log.d(TAG,"當前使用者從沒有新增此商品到資料庫時，就可以直接insert");
                                insertCartDetail();
                            }else{
                                //task無結果表示cartDetail還沒有文件，
                                //所以不用判斷就直接新增cardDetail
                                Log.d(TAG,"task無結果表示cartDetail還沒有文件");
                                insertCartDetail();
                            }
                        } else {
                            String message = task.getException() == null ?
                                    "No cartDetail found" :
                                    task.getException().getMessage();
                            Log.e(TAG, "exception message: " + message);
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    //todo
    private void updateCartDetail(CartDetail cartDetail) {
        cartDetail.setProductAmount(cartDetail.getProductAmount()+1);
        db.collection("cartDetail").document(cartDetail.getId()).set(cartDetail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String message = "Update "
                                + " with ID: " + cartDetail.getId();
                        Log.d(TAG, message);
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    } else {
                        String message = task.getException() == null ?
                                "Insert Fail ":
                                task.getException().getMessage();
                        Log.e(TAG, "message: " + message);
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void insertCartDetail() {
        String id = db.collection("cartDetail").document().getId(); //隨機產生一個id給cartDetail
        cartDetail.setId(id);
        String uid =  auth.getCurrentUser().getUid();
        cartDetail.setUid(uid);
        cartDetail.setProductId(product.getId());
        cartDetail.setProductPrice(product.getPrice());
        cartDetail.setProductAmount(1);
        db.collection("cartDetail").document(cartDetail.getId()).set(cartDetail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String message = "cartDetail is inserted"
                                + " with ID: " + cartDetail.getId();
                        Log.d(TAG, message);
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    } else {
                        String message = task.getException() == null ?
                                "Insert failed" :
                                task.getException().getMessage();
                        Log.e(TAG, "message: " + message);
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //購買商品，跳出bottomSheet確認數量並結帳
    private void buyProduct() {
        btBuyProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });
    }
    //todo
    private void showBottomSheetDialog(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetDialog.setContentView(R.layout.shopping_bottom_sheet_checkout);

        ImageView ivProduct = bottomSheetDialog.findViewById(R.id.iv_product);
        TextView tvProductPrice = bottomSheetDialog.findViewById(R.id.tv_product_price);
        TextView tvProductStock = bottomSheetDialog.findViewById(R.id.tv_product_stock);
        AmountView amountView = bottomSheetDialog.findViewById(R.id.amountView_bottomSheet);
        Button btCheckout = bottomSheetDialog.findViewById(R.id.bt_checkout);

        tvProductPrice.append(String.format("$%s", product.getPrice()));
        tvProductStock.append(String.valueOf(product.getStock()));

        if(product.getPicturePath() != null){
            showImage(ivProduct, product.getPicturePath());
        }else{
            ivProduct.setImageResource(R.drawable.no_product_image);
        }

        amountView.setGoods_storage(product.getStock());
        amountView.setOnAmountChangeListener(new AmountView.OnAmountChangeListener() {
            //amount是計數器上顯示的數字
            @Override
            public void onAmountChange(View view, int amount) {
                //Log.d(TAG, String.valueOf(amount));
            }
        });

        btCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity.getApplicationContext(), "checkout ", Toast.LENGTH_LONG).show();
                NavController navController = Navigation.findNavController(btBuyProduct);
                navController.navigate(R.id.action_shoppingProductFragment_to_shoppingOrderFragment);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();

    }

    // 執行加入購物車動畫
    private void playAnim(View view) {

        //建立int陣列，用来接收起點坐標和終點坐標
        int[] startPosition = new int[2];
        int[] endPosition = new int[2];

        view.getLocationInWindow(startPosition);
        toolbar.getLocationInWindow(endPosition);

        PointF startF = new PointF();        //起始點 startF
        PointF endF = new PointF();          //终點 endF
        PointF controlF = new PointF();      //控制點 controlF

        //設定起點
        startF.x = startPosition[0];
        startF.y = startPosition[1];
        //微調處理，確保動畫執行完畢 添加 圖標中心點與購物車中心點重合
        //view 是 bt加入購物車
        endF.x = (float) (endPosition[0] + toolbar.getWidth() * 0.7);
        endF.y = endPosition[1] + toolbar.getHeight() / 2 - view.getHeight() / 2;
        controlF.x = endF.x;
        controlF.y = startF.y;

        //todo
        // 建立執行動畫的 添加 圖標
        ImageView imageView = new ImageView(activity);
        mRootView.addView(imageView);
        imageView.setImageResource(R.drawable.ic_baseline_shopping_cart_24); //動畫圖形
        imageView.getLayoutParams().width = view.getMeasuredWidth();
        imageView.getLayoutParams().height = view.getMeasuredHeight();

        ValueAnimator valueAnimator = ValueAnimator.ofObject(new CartEvaluator(controlF), startF, endF);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF pointF = (PointF) animation.getAnimatedValue();
                imageView.setX(pointF.x);
                imageView.setY(pointF.y);
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // 動畫執行完畢，將執行動畫的 添加 圖標移除掉
                mRootView.removeView(imageView);

                // 執行購物車縮放動畫
                AnimatorSet animatorSet = new AnimatorSet();
                ObjectAnimator animatorX = ObjectAnimator.ofFloat(toolbar, "scaleX", 1f, 1.2f, 1f);
                ObjectAnimator animatorY = ObjectAnimator.ofFloat(toolbar, "scaleY", 1f, 1.2f, 1f);
                animatorSet.play(animatorX).with(animatorY);
                animatorSet.setDuration(400);
                animatorSet.start();
            }
        });

        valueAnimator.setDuration(800);
        valueAnimator.start();
    }

    public class CartEvaluator implements TypeEvaluator<PointF> {

        private PointF pointCur;
        private PointF mControlPoint;

        public CartEvaluator(PointF mControlPoint) {

            this.mControlPoint = mControlPoint;
            pointCur = new PointF();
        }

        @Override
        public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
            // 将二阶贝塞尔曲线的计算公式直接代入即可
            pointCur.x = (1 - fraction) * (1 - fraction) * startValue.x
                    + 2 * fraction * (1 - fraction) * mControlPoint.x + fraction * fraction * endValue.x;
            pointCur.y = (1 - fraction) * (1 - fraction) * startValue.y
                    + 2 * fraction * (1 - fraction) * mControlPoint.y + fraction * fraction * endValue.y;

            return pointCur;
        }
    }
}


