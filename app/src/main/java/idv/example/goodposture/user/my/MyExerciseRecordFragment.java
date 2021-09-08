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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import idv.example.goodposture.R;
import idv.example.goodposture.user.home.Bodyinfo;

public class MyExerciseRecordFragment extends Fragment {

    private Activity activity;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private LineChart exerciseChart;
    private TextView tvExerciseCalorie;
    private FloatingActionButton fabAddExerciseRecord;
    private List<MyRecord> myRecordList;
    private MyRecord myRecord;
    private Float[] totalCalorie = new Float[24];
    private float total = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        myRecordList = new ArrayList<>();
        myRecord = new MyRecord();
        for (int i = 0; i <= 23; i++) {
            totalCalorie[i] = 0f;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        return inflater.inflate(R.layout.fragment_my_exercise_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleData();
        handleButton();
    }

    private void findViews(View view) {
        exerciseChart = view.findViewById(R.id.exerciseChart);
        tvExerciseCalorie = view.findViewById(R.id.tv_exerciseCalorie);
        fabAddExerciseRecord = view.findViewById(R.id.fab_addExerciseRecord);
    }

    private void handleData() {
        String uid = auth.getCurrentUser().getUid();
        String today = new SimpleDateFormat("yyyy/MM/dd")
                .format(Calendar.getInstance().getTime());
        db.collection("record")
                .whereEqualTo("uid", uid)
                .whereEqualTo("date", today)
                .whereEqualTo("calorieType", "exercise")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            myRecordList.add(document.toObject(MyRecord.class));
                        }
                            for (int i = 0; i < myRecordList.size(); i++) {
                                switch (myRecordList.get(i).getTime()) {
                                    case "0:00":
                                        totalCalorie[0] += myRecordList.get(i).getCalorie();
                                        break;
                                    case "1:00":
                                        totalCalorie[1] += myRecordList.get(i).getCalorie();
                                        break;
                                    case "2:00":
                                        totalCalorie[2] += myRecordList.get(i).getCalorie();
                                        break;
                                    case "3:00":
                                        totalCalorie[3] += myRecordList.get(i).getCalorie();
                                        break;
                                    case "4:00":
                                        totalCalorie[4] += myRecordList.get(i).getCalorie();
                                        break;
                                    case "5:00":
                                        totalCalorie[5] += myRecordList.get(i).getCalorie();
                                        break;
                                    case "6:00":
                                        totalCalorie[6] += myRecordList.get(i).getCalorie();
                                        break;
                                    case "7:00":
                                        totalCalorie[7] += myRecordList.get(i).getCalorie();
                                        break;
                                    case "8:00":
                                        totalCalorie[8] += myRecordList.get(i).getCalorie();
                                        break;
                                    case "9:00":
                                        totalCalorie[9] += myRecordList.get(i).getCalorie();
                                        break;
                                    case "10:00":
                                        totalCalorie[10] += myRecordList.get(i).getCalorie();
                                        break;
                                    case "11:00":
                                        totalCalorie[11] += myRecordList.get(i).getCalorie();
                                        break;
                                    case "12:00":
                                        totalCalorie[12] += myRecordList.get(i).getCalorie();
                                        break;
                                    case "13:00":
                                        totalCalorie[13] += myRecordList.get(i).getCalorie();
                                        break;
                                    case "14:00":
                                        totalCalorie[14] += myRecordList.get(i).getCalorie();
                                        break;
                                    case "15:00":
                                        totalCalorie[15] += myRecordList.get(i).getCalorie();
                                        break;
                                    case "16:00":
                                        totalCalorie[16] += myRecordList.get(i).getCalorie();
                                        break;
                                    case "17:00":
                                        totalCalorie[17] += myRecordList.get(i).getCalorie();
                                        break;
                                    case "18:00":
                                        totalCalorie[18] += myRecordList.get(i).getCalorie();
                                        break;
                                    case "19:00":
                                        totalCalorie[19] += myRecordList.get(i).getCalorie();
                                        break;
                                    case "20:00":
                                        totalCalorie[20] += myRecordList.get(i).getCalorie();
                                        break;
                                    case "21:00":
                                        totalCalorie[21] += myRecordList.get(i).getCalorie();
                                        break;
                                    case "22:00":
                                        totalCalorie[22] += myRecordList.get(i).getCalorie();
                                        break;
                                    case "23:00":
                                        totalCalorie[23] += myRecordList.get(i).getCalorie();
                                        break;
                                    default:
                                        break;
                                }
                            handleChart();
                        }
                        for (int sizeIndex = 0; sizeIndex < myRecordList.size(); sizeIndex++) {
                            total += myRecordList.get(sizeIndex).getCalorie();
                        }
                        tvExerciseCalorie.append(" " + (int)total + " 大卡");
                    } else {
                        String message = task.getException() == null ?
                                "Not Found" :
                                task.getException().getMessage();
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleChart() {
        ArrayList<Entry> values = new ArrayList<>();
        values.add(new Entry(0, totalCalorie[0]));
        values.add(new Entry(1, totalCalorie[1]));
        values.add(new Entry(2, totalCalorie[2]));
        values.add(new Entry(3, totalCalorie[3]));
        values.add(new Entry(4, totalCalorie[4]));
        values.add(new Entry(5, totalCalorie[5]));
        values.add(new Entry(6, totalCalorie[6]));
        values.add(new Entry(7, totalCalorie[7]));
        values.add(new Entry(8, totalCalorie[8]));
        values.add(new Entry(9, totalCalorie[9]));
        values.add(new Entry(10, totalCalorie[10]));
        values.add(new Entry(11, totalCalorie[11]));
        values.add(new Entry(12, totalCalorie[12]));
        values.add(new Entry(13, totalCalorie[13]));
        values.add(new Entry(14, totalCalorie[14]));
        values.add(new Entry(15, totalCalorie[15]));
        values.add(new Entry(16, totalCalorie[16]));
        values.add(new Entry(17, totalCalorie[17]));
        values.add(new Entry(18, totalCalorie[18]));
        values.add(new Entry(19, totalCalorie[19]));
        values.add(new Entry(20, totalCalorie[20]));
        values.add(new Entry(21, totalCalorie[21]));
        values.add(new Entry(22, totalCalorie[22]));
        values.add(new Entry(23, totalCalorie[23]));
        LineDataSet lineDataSet = new LineDataSet(values, "今日運動紀錄");
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);
        lineDataSet.setLineWidth(4);
        lineDataSet.setCircleRadius(4);
        lineDataSet.setDrawValues(false);

        // 設定 X 軸
        XAxis xAxis = exerciseChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0);
        xAxis.setGranularity(1);
        xAxis.setLabelCount(12);

        // 設定 Y 軸
        YAxis rightAxis = exerciseChart.getAxisRight();
        rightAxis.setEnabled(false);

        // 設定圖表描述
        Description description = new Description();
        description.setText("");
        exerciseChart.setDescription(description);

        LineData lineData = new LineData(lineDataSet);
        exerciseChart.setData(lineData);
        // 更新圖表
        exerciseChart.invalidate();
    }

    private void handleButton() {
        fabAddExerciseRecord.setOnClickListener(view -> {
            // 取得NavController物件
            NavController navController = Navigation.findNavController(view);
            // 跳至頁面
            navController.navigate(R.id.action_myRecordIndexFragment_to_myAddRecordFragment);
        });
    }
}