package idv.example.goodposture;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;


public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        handleBottomNavigationView();

    }

    private void findViews() {
        // 取得BottomNavigationView參考
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void handleBottomNavigationView() {
        // 3.2 取得NavController物件
        NavController navController = Navigation.findNavController(this, R.id.navHost);
        // 3.3 加入 導覽功能 至 頁籤導覽元件
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

}