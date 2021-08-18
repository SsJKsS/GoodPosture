package idv.example.goodposture.admin.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.DecimalFormat;
import java.util.ArrayList;

import idv.example.goodposture.R;

public class AdminHomeFragment extends Fragment {
    private TextView tvExpectedSales, tvWeeklySales, tvCompleteRate,
            tvNo1Sales, tvNo2Sales, tvNo3Sales, tvNo1, tvNo2, tvNo3;
    private LineChart lineChart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_admin_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleSales();
        handleChart();
    }

    private void findViews(View view) {
        tvExpectedSales = view.findViewById(R.id.tv_expectedSales);
        tvWeeklySales = view.findViewById(R.id.tv_weeklySales);
        tvCompleteRate = view.findViewById(R.id.tv_completeRate);
        tvNo1 = view.findViewById(R.id.tv_no1);
        tvNo2 = view.findViewById(R.id.tv_no2);
        tvNo3 = view.findViewById(R.id.tv_no3);
        tvNo1Sales = view.findViewById(R.id.tv_no1Sales);
        tvNo2Sales = view.findViewById(R.id.tv_no2Sales);
        tvNo3Sales = view.findViewById(R.id.tv_no3Sales);
        lineChart = view.findViewById(R.id.lineChart);
    }

    private void handleSales() {
        // 銷售額
        int expectedSales, weeklySales, completeRate;
        expectedSales = 20000;
        weeklySales = 40000;
        completeRate = (weeklySales / expectedSales) * 100;
        String expectedSalesStr = "$" + String.format("%,d", expectedSales);
        String weeklySalesStr = "$" + String.format("%,d", weeklySales);
        String completeRateStr = completeRate + "%";
        tvExpectedSales.setText(expectedSalesStr);
        tvWeeklySales.setText(weeklySalesStr);
        tvCompleteRate.setText(completeRateStr);
        if (completeRate >= 100) {
            tvCompleteRate.setTextColor(Color.RED);
        }

        // 排行榜
        int no1Sales, no2Sales, no3Sales;
        String no1SalesCommodity, no2SalesCommodity, no3SalesCommodity;
        no1Sales = 1455;
        no2Sales = 1433;
        no3Sales = 1422;
        no1SalesCommodity = "啞鈴";
        no2SalesCommodity = "雞胸肉";
        no3SalesCommodity = "彈力帶";
        tvNo1.setText(no1SalesCommodity);
        tvNo2.setText(no2SalesCommodity);
        tvNo3.setText(no3SalesCommodity);
        tvNo1Sales.setText(String.format("%,d", no1Sales));
        tvNo2Sales.setText(String.format("%,d", no2Sales));
        tvNo3Sales.setText(String.format("%,d", no3Sales));
    }

    private void handleChart() {
        ArrayList<Entry> values = new ArrayList<>();
        values.add(new Entry(1,2));
        values.add(new Entry(2,4));
        values.add(new Entry(3,6));
        values.add(new Entry(4,3));
        values.add(new Entry(5,5));
        values.add(new Entry(6,6));
        values.add(new Entry(7,14));
        values.add(new Entry(8,8));
        values.add(new Entry(9,10));
        values.add(new Entry(10,12));
        values.add(new Entry(11,6));
        values.add(new Entry(12,10));
        LineDataSet lineDataSet = new LineDataSet(values, "近 12 個月銷售狀況");
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);
        lineDataSet.setLineWidth(1);
        lineDataSet.setCircleRadius(4);
        lineDataSet.setDrawValues(false);

        // 設定 X 軸
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(1);
        xAxis.setGranularity(1);
        xAxis.setLabelCount(12);

        // 設定 Y 軸
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        // 設定圖表描述
        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
    }

}