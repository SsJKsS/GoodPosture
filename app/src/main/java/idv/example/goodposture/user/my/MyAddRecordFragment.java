package idv.example.goodposture.user.my;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import idv.example.goodposture.R;
import idv.example.goodposture.user.MainActivity;
import idv.example.goodposture.user.home.Bodyinfo;

public class MyAddRecordFragment extends Fragment {
    private AppCompatActivity activity;
    private ArrayAdapter adapter;
    private Toolbar toolBar;
    private RadioGroup radioGroup;
    private Spinner itemSpinner, timeSpinner;
    private Button btSubmit;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private MyRecord myRecord;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        myRecord = new MyRecord();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (AppCompatActivity) getActivity();
        // 設定允許Fragment有功能選單
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_add_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleToolbar();
        handleCalorie();
        handleData();
    }

    private void handleToolbar() {
        // 設定Toolbar為狀態列
        activity.setSupportActionBar(toolBar);
        // 取得ActionBar
        ActionBar actionBar = activity.getSupportActionBar();
        // 設定顯示返回鍵
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            // 返回鍵的資源ID固定為android.R.id.home
            Navigation.findNavController(btSubmit).popBackStack();
        }
        return true;
    }

    private void findViews(View view) {
        toolBar = view.findViewById(R.id.add_toolbar);
        radioGroup = view.findViewById(R.id.rg_record);
        itemSpinner = view.findViewById(R.id.sp_record_item);
        timeSpinner = view.findViewById(R.id.sp_record_time);
        btSubmit = view.findViewById(R.id.bt_record_submit);
    }

    private void handleCalorie() {
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_exerciseRecord) {
                myRecord.setCalorieType("exercise");
                adapter = ArrayAdapter
                        .createFromResource(getActivity(),
                                R.array.exercise_record_type,
                                android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                itemSpinner.setAdapter(adapter);

                // 運動熱量計算
                itemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        switch (i) {
                            case 0:
                                myRecord.setCalorie(500f);
                                break;
                            case 1:
                                myRecord.setCalorie(400f);
                                break;
                            case 2:
                                myRecord.setCalorie(300f);
                                break;
                            case 3:
                                myRecord.setCalorie(200f);
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
            else if (checkedId == R.id.rb_eatRecord) {
                myRecord.setCalorieType("eat");
                adapter = ArrayAdapter
                        .createFromResource(getActivity(),
                                R.array.eat_record_type,
                                android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                itemSpinner.setAdapter(adapter);

                // 飲食熱量計算
                itemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        switch (i) {
                            case 0:
                                myRecord.setCalorie(300f);
                                break;
                            case 1:
                                myRecord.setCalorie(200f);
                                break;
                            case 2:
                                myRecord.setCalorie(100f);
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        });
    }

    private void handleData() {
        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        myRecord.setTime("0:00");
                        break;
                    case 1:
                        myRecord.setTime("1:00");
                        break;
                    case 2:
                        myRecord.setTime("2:00");
                        break;
                    case 3:
                        myRecord.setTime("3:00");
                        break;
                    case 4:
                        myRecord.setTime("4:00");
                        break;
                    case 5:
                        myRecord.setTime("5:00");
                        break;
                    case 6:
                        myRecord.setTime("6:00");
                        break;
                    case 7:
                        myRecord.setTime("7:00");
                        break;
                    case 8:
                        myRecord.setTime("8:00");
                        break;
                    case 9:
                        myRecord.setTime("9:00");
                        break;
                    case 10:
                        myRecord.setTime("10:00");
                        break;
                    case 11:
                        myRecord.setTime("11:00");
                        break;
                    case 12:
                        myRecord.setTime("12:00");
                        break;
                    case 13:
                        myRecord.setTime("13:00");
                        break;
                    case 14:
                        myRecord.setTime("14:00");
                        break;
                    case 15:
                        myRecord.setTime("15:00");
                        break;
                    case 16:
                        myRecord.setTime("16:00");
                        break;
                    case 17:
                        myRecord.setTime("17:00");
                        break;
                    case 18:
                        myRecord.setTime("18:00");
                        break;
                    case 19:
                        myRecord.setTime("19:00");
                        break;
                    case 20:
                        myRecord.setTime("20:00");
                        break;
                    case 21:
                        myRecord.setTime("21:00");
                        break;
                    case 22:
                        myRecord.setTime("22:00");
                        break;
                    case 23:
                        myRecord.setTime("23:00");
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btSubmit.setOnClickListener(v -> {
            myRecord.setUid(auth.getCurrentUser().getUid());
            // 設定記錄當天日期
            String date = new SimpleDateFormat("yyyy/MM/dd")
                    .format(Calendar.getInstance().getTime());
            myRecord.setDate(date);
            add();
            // 取得NavController物件
            NavController navController = Navigation.findNavController(btSubmit);
            // 跳至頁面
            navController.navigate(R.id.action_myAddRecordFragment_to_myRecordIndexFragment);
        });
    }

    private void add() {
        // 如果Firestore沒有該ID的Document就建立新的，已經有就更新內容
        db.collection("record").document().set(myRecord)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String message = "新增成功";
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    } else {
                        String message = task.getException() == null ?
                                "新增失敗" : task.getException().getMessage();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}