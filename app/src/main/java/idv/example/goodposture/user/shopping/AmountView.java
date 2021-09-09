package idv.example.goodposture.user.shopping;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import idv.example.goodposture.R;


//TextWatcher是監聽文字變化的類別
//使用editText的addTextChangedListener(TextWatcher)方法之後就可以監聽EditText的輸入
//TextWatcher 需要實作三個抽象方法-beforeTextChanged, onTextChanged, afterTextChanged

public class AmountView extends LinearLayout implements View.OnClickListener, TextWatcher {
    private static final String TAG = "AmountView";
    private int amount =1; //購買數量
    private int goods_storage = 1; //商品庫存
    private OnAmountChangeListener mListener;
    private EditText etAmount;
    private Button btnDecrease;
    private Button btnIncrease;

    public AmountView(Context context) {
        this(context, null);
    }

    public AmountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_shopping_amount, this);
        etAmount = (EditText) findViewById(R.id.etAmount);
        btnDecrease = (Button) findViewById(R.id.btnDecrease);
        btnIncrease = (Button) findViewById(R.id.btnIncrease);
        btnDecrease.setOnClickListener(this);
        btnIncrease.setOnClickListener(this);
        etAmount.addTextChangedListener(this);

//        //todo-studying
//        //related values-amountview_attrs.xml ,not add yet
//        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attrs, R.styleable.AmountView);
//        int btnWidth = obtainStyledAttributes.getDimensionPixelSize(R.styleable.AmountView_btnWidth, LayoutParams.WRAP_CONTENT);
//        int tvWidth = obtainStyledAttributes.getDimensionPixelSize(R.styleable.AmountView_tvWidth, 80);
//        int tvTextSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.AmountView_tvTextSize, 0);
//        int btnTextSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.AmountView_btnTextSize, 0);
//        obtainStyledAttributes.recycle();
//
//        LayoutParams btnParams = new LayoutParams(btnWidth, LayoutParams.MATCH_PARENT);
//        btnDecrease.setLayoutParams(btnParams);
//        btnIncrease.setLayoutParams(btnParams);
//        if (btnTextSize != 0) {
//            btnDecrease.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnTextSize);
//            btnIncrease.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnTextSize);
//        }
//
//        LayoutParams textParams = new LayoutParams(tvWidth, LayoutParams.MATCH_PARENT);
//        etAmount.setLayoutParams(textParams);
//        if (tvTextSize != 0) {
//            etAmount.setTextSize(tvTextSize);
//        }
    }

    public void setOnAmountChangeListener(OnAmountChangeListener onAmountChangeListener){
        this.mListener = onAmountChangeListener;
    }

    public void setGoods_storage(int goods_storage){
        this.goods_storage = goods_storage;
    }

    public void setAmount(int amount) {
        this.amount = amount;
        etAmount.setText(amount+"");
    }

    public int getAmount() {
        return Integer.parseInt(String.valueOf(etAmount.getText()));
    }

    @Override
    public void onClick(View v) {
        int btn = v.getId();
        if(btn == R.id.btnDecrease){
            if(amount > 1){
                amount--;
                etAmount.setText(amount+"");
            }
        }else if(btn == R.id.btnIncrease){
            if(amount < goods_storage){
                amount++;
                etAmount.setText(amount+"");
            }
            //如果數量超過存貨就沒有反應
        }
        etAmount.clearFocus();

        if(mListener != null){
            mListener.onAmountChange(this, amount);
        }else{
            Log.d(TAG,"mListener is null");
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s.toString().isEmpty()){
            return;
        }
        amount = Integer.valueOf(s.toString());
        if(amount > goods_storage){
            etAmount.setText(goods_storage+"");
            return;
        }
        //如果輸入的文字比庫存少，表示合法值，傳回listener
        if(mListener != null){
            mListener.onAmountChange(this, amount);
        }
    }

    public interface OnAmountChangeListener {
        void onAmountChange(View view, int amount);
    }
}

