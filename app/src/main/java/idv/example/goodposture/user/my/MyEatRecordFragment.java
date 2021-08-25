package idv.example.goodposture.user.my;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import idv.example.goodposture.R;

public class MyEatRecordFragment extends Fragment {

    private Activity activity;
    private Button btExerciseRecord;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddEatRecord;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        return inflater.inflate(R.layout.fragment_my_eat_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleButton();
        handleRecyclerView();
    }

    private void findViews(View view) {
        btExerciseRecord = view.findViewById(R.id.bt_exerciseRecord);
        recyclerView = view.findViewById(R.id.rv_eatRecord);
        fabAddEatRecord = view.findViewById(R.id.fab_addEatRecord);
    }

    private void handleButton() {
        btExerciseRecord.setOnClickListener(view -> {
            // 取得NavController物件
            NavController navController = Navigation.findNavController(view);
            // 跳至頁面
            navController.navigate(R.id.action_myEatRecordFragment_to_myExerciseRecordFragment);
        });

        fabAddEatRecord.setOnClickListener(view -> {
            // 取得NavController物件
            NavController navController = Navigation.findNavController(view);
            // 跳至頁面
            navController.navigate(R.id.action_myEatRecordFragment_to_myAddRecordFragment);
        });

    }

    private void handleRecyclerView() {
        // 4.2 設定Adapter
        recyclerView.setAdapter(new MyEatRecordFragment.MyAdapter(this, getRecordList()));
        // 4.3 設定LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    /**
     * 3. 自定義Adapter類別
     * 3.1 繼承RecyclerView.Adapter
     */
    private static class MyAdapter extends RecyclerView.Adapter<MyEatRecordFragment.MyAdapter.MyViewHolder> {
        // 3.2 欄位: Context物件、選項資料物件
        private final Context context;
        private final List<Record> list;

        // 3.3 建構子: 2個參數(Context型態、選項資料的型態)，用來初始化2欄位
        public MyAdapter(MyEatRecordFragment context, List<Record> list) {
            this.context = context.getActivity();
            this.list = list;
        }

        // 3.4 內部類別: 自定義ViewHolder類別
        // 3.4.1 繼承RecyclerView.ViewHolder
        private static class MyViewHolder extends RecyclerView.ViewHolder {
            // 3.4.2 欄位: 對應選項容器元件，之內的所有元件
            TextView tvDate;
            TextView tvCalorie;

            // 3.4.3 建構子: 1個參數(View型態)，該參數就是選項容器元件，用來取得各容器元件的參考
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvCalorie = itemView.findViewById(R.id.tv_calorie);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public MyEatRecordFragment.MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.my_record_item_view, parent, false);
            return new MyEatRecordFragment.MyAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyEatRecordFragment.MyAdapter.MyViewHolder holder, int position) {
            final Record record = list.get(position);
            holder.tvDate.setText(record.getDate());
            holder.tvCalorie.setText(record.getCalorie());
        }

    }
    private List<Record> getRecordList() {
        List<Record> recordList = new ArrayList<>();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int calorie = 777;
        String calorieStr = Integer.toString(calorie);
        String calorieStr2 = Integer.toString(calorie + 1);
        String calorieStr3 = Integer.toString(calorie + 2);
        recordList.add(new Record(sdf.format(date), calorieStr + "卡"));
        recordList.add(new Record(sdf.format(date), calorieStr2 + "卡"));
        recordList.add(new Record(sdf.format(date), calorieStr3 + "卡"));
        return recordList;
    }
}