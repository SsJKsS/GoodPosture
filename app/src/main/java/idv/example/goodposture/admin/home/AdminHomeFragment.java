package idv.example.goodposture.admin.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.Date;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import idv.example.goodposture.R;
import idv.example.goodposture.admin.setting.ExpectedSales;
import idv.example.goodposture.user.MainActivity;
import idv.example.goodposture.user.home.Bodyinfo;
import idv.example.goodposture.user.my.MyRecord;
import idv.example.goodposture.user.my.Order;
import idv.example.goodposture.user.my.Sales;
import idv.example.goodposture.user.shopping.Product;

public class AdminHomeFragment extends Fragment {
    private TextView tvExpectedSales, tvMonthlySales, tvCompleteRate,
            tvNo1Sales, tvNo2Sales, tvNo3Sales, tvNo1, tvNo2, tvNo3;
    private LineChart lineChart;
    private FirebaseFirestore db;
    private ExpectedSales expectedSales;
    private int total;
    double completeRate;
    private List<Order> orderList;
    private Sales sales;
    private List<Product> productList;
    private List<Order> orderSalesList;
    private String[] productName = new String[99];
    private int[] sellAmount = new int[99];
    private int[] productDate = new int[99];
    private int[] saleAmount = new int[12];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        expectedSales = new ExpectedSales();
        orderList = new ArrayList<>();
        sales = new Sales();
        productList = new ArrayList<>();
        orderSalesList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            saleAmount[i] = 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleExpectedSales();
        handleHotSales();
        handleChart();
    }

    private void findViews(View view) {
        tvExpectedSales = view.findViewById(R.id.tv_expectedSales);
        tvMonthlySales = view.findViewById(R.id.tv_weeklySales);
        tvCompleteRate = view.findViewById(R.id.tv_completeRate);
        tvNo1 = view.findViewById(R.id.tv_no1);
        tvNo2 = view.findViewById(R.id.tv_no2);
        tvNo3 = view.findViewById(R.id.tv_no3);
        tvNo1Sales = view.findViewById(R.id.tv_no1Sales);
        tvNo2Sales = view.findViewById(R.id.tv_no2Sales);
        tvNo3Sales = view.findViewById(R.id.tv_no3Sales);
        lineChart = view.findViewById(R.id.lineChart);
    }

    private void handleExpectedSales() {
        // 查詢指定集合
        db.collection("expected_sales")
                .document("JegPOdvDoarQExD8cu00")
                // 獲取資料
                .get()
                // 設置網路傳輸監聽器
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // 將獲取的資料存成自定義類別
                        DocumentSnapshot documentSnapshot = task.getResult();
                        expectedSales = documentSnapshot.toObject(ExpectedSales.class);
                        int expected = expectedSales.getExpectedSales();
                        String expectedSalesStr = "$" + expectedSales.getExpectedSales();
                        tvExpectedSales.setText(expectedSalesStr);
                        sales.setSales(expected);
                        db.collection("sales")
                                .document("KAoQSRFP0kxV4FvSFngG").set(sales);
                        handleTotalSales(expected);
                    } else {
                        String message = task.getException() == null ?
                                "查無資料" :
                                task.getException().getMessage();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleTotalSales(int expected) {
        db.collection("order")
                .whereEqualTo("orderState", 3)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            orderList.add(document.toObject(Order.class));
                        }
                        // 統計總和
                        for (int sizeIndex = 0; sizeIndex < orderList.size(); sizeIndex++) {
                            total += orderList.get(sizeIndex).getOrderAmount();
                        }
                        String weeklySalesStr = "$" + total;
                        tvMonthlySales.setText(weeklySalesStr);
                        sales.setTotal(total);
                        db.collection("sales")
                                .document("KAoQSRFP0kxV4FvSFngG").set(sales);
                        if (expected == 0 && total != 0) {
                            tvCompleteRate.setText("未設定銷售額");
                            tvCompleteRate.setTextColor(Color.RED);
                        } else if (expected != 0 && total == 0) {
                            tvCompleteRate.setText("目前尚無銷售額");
                            tvCompleteRate.setTextColor(Color.RED);
                        } else if (expected == 0 && total == 0) {
                            tvCompleteRate.setText("0%");
                        } else {
                            completeRate = ((double) total / (double) expected) * 100;
                            String completeRateStr = (int) completeRate + "%";
                            tvCompleteRate.setText(completeRateStr);
                            if (completeRate >= 100) {
                                tvCompleteRate.setTextColor(Color.RED);
                            }
                        }

                    } else {
                        String message = task.getException() == null ?
                                "查無資料" :
                                task.getException().getMessage();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleHotSales() {
        // 查詢指定集合
        db.collection("product")
                .orderBy("sellAmount", Query.Direction.DESCENDING)
                // 獲取資料
                .get()
                // 設置網路傳輸監聽器
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // 將獲取的資料存成自定義類別
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            productList.add(document.toObject(Product.class));
                        }
                        for (int i = 0; i < productList.size(); i++) {
                            productName[i] = productList.get(i).getName();
                            sellAmount[i] = productList.get(i).getSellAmount();
                        }
                        tvNo1.setText(productName[0]);
                        tvNo2.setText(productName[1]);
                        tvNo3.setText(productName[2]);
                        tvNo1Sales.setText(String.valueOf(sellAmount[0]));
                        tvNo2Sales.setText(String.valueOf(sellAmount[1]));
                        tvNo3Sales.setText(String.valueOf(sellAmount[2]));
                    } else {
                        String message = task.getException() == null ?
                                "查無資料" :
                                task.getException().getMessage();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void handleChart() {
        // 查詢指定集合
        db.collection("order")
                .whereEqualTo("orderState", 3)
                .get()
                // 設置網路傳輸監聽器
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // 將獲取的資料存成自定義類別
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            orderSalesList.add(document.toObject(Order.class));
                        }
                        for (int i = 0; i < orderSalesList.size(); i++) {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(orderSalesList.get(i).getOrderTime());
                            int month = cal.get(Calendar.MONTH);
                            productDate[i] = month;
//                            Log.d("JK", String.valueOf(productDate[i]));
                        }
                        for (int i = 0; i < orderSalesList.size(); i++) {
                            switch (productDate[i]) {
                                case 0:
                                    saleAmount[0] += orderSalesList.get(i).getOrderAmount();
                                    break;
                                case 1:
                                    saleAmount[1] += orderSalesList.get(i).getOrderAmount();
                                    break;
                                case 2:
                                    saleAmount[2] += orderSalesList.get(i).getOrderAmount();
                                    break;
                                case 3:
                                    saleAmount[3] += orderSalesList.get(i).getOrderAmount();
                                    break;
                                case 4:
                                    saleAmount[4] += orderSalesList.get(i).getOrderAmount();
                                    break;
                                case 5:
                                    saleAmount[5] += orderSalesList.get(i).getOrderAmount();
                                    break;
                                case 6:
                                    saleAmount[6] += orderSalesList.get(i).getOrderAmount();
                                    break;
                                case 7:
                                    saleAmount[7] += orderSalesList.get(i).getOrderAmount();
                                    break;
                                case 8:
                                    saleAmount[8] += orderSalesList.get(i).getOrderAmount();
                                    break;
                                case 9:
                                    saleAmount[9] += orderSalesList.get(i).getOrderAmount();
                                    break;
                                case 10:
                                    saleAmount[10] += orderSalesList.get(i).getOrderAmount();
                                    break;
                                case 11:
                                    saleAmount[11] += orderSalesList.get(i).getOrderAmount();
                                    break;
                                default:
                                    break;
                            }
                            handleChart1();
                        }
                    }
                    else {
                        String message = task.getException() == null ?
                                "查無資料" :
                                task.getException().getMessage();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleChart1() {
        ArrayList<Entry> values = new ArrayList<>();
        values.add(new Entry(1, saleAmount[0]));
        values.add(new Entry(2, saleAmount[1]));
        values.add(new Entry(3, saleAmount[2]));
        values.add(new Entry(4, saleAmount[3]));
        values.add(new Entry(5, saleAmount[4]));
        values.add(new Entry(6, saleAmount[5]));
        values.add(new Entry(7, saleAmount[6]));
        values.add(new Entry(8, saleAmount[7]));
        values.add(new Entry(9, saleAmount[8]));
        values.add(new Entry(10, saleAmount[9]));
        values.add(new Entry(11, saleAmount[10]));
        values.add(new Entry(12, saleAmount[11]));
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
        // 更新圖表
        lineChart.invalidate();
    }

}