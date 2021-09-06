package idv.example.goodposture.user.my;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

import idv.example.goodposture.R;

public class MyOrderFragment extends Fragment {
    private AppCompatActivity activity;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_order, container, false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleToolbar();
        handleViewPager2();
    }

    private void findViews(View view) {
        toolbar = view.findViewById(R.id.tb_my);
        tabLayout = view.findViewById(R.id.tablayout_my_order);
        viewPager2 = view.findViewById(R.id.ViewPager2_my_order);
    }

    private void handleToolbar() {
        activity.setSupportActionBar(toolbar);
        //ToolBar的標題預設是AndroidManifest檔案中<Application/>標籤下屬性label設定的值
        toolbar.setTitle("我的訂單");
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //覆寫menu選項的監聽
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == android.R.id.home){
            Navigation.findNavController(toolbar).popBackStack();
        }else{
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void handleViewPager2() {
        viewPager2.setAdapter(new OrderStatePageAdapter(this.getActivity()));
        ArrayList<String> title = new ArrayList<>();
        title.add("待出貨");
        title.add("已出貨");
        title.add("已完成");
        title.add("已取消");
        //index 從0開始
        TabLayoutMediator tabLayoutMediator =
                new TabLayoutMediator(
                        tabLayout,
                        viewPager2,
                        true,
                        new TabLayoutMediator.TabConfigurationStrategy() {
                            @Override
                            public void onConfigureTab(TabLayout.Tab tab, int position) {
                                tab.setText(title.get(position));
                            }
                        });
        //
        tabLayoutMediator.attach();
    }
}