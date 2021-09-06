package idv.example.goodposture.user;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import idv.example.goodposture.R;
import idv.example.goodposture.user.home.HomeLoginFragment;


public class PreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre);
        findViews();
        handleButton();
//        handleIntent();
    }

    private void findViews() {

    }

    private void handleButton() {
    }

    private void handleIntent() {
        NavController navController = Navigation.findNavController(this,R.id.fragment);
        int id = getIntent().getIntExtra("id",0);
        if(id == 1){
            navController.navigate(R.id.homeLoginFragment);
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.homeLoginFragment,new HomeLoginFragment())
//                    .addToBackStack(null)
//                    .commit();
        }
    }
}