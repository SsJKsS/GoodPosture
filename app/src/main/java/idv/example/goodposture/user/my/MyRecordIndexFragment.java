package idv.example.goodposture.user.my;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import idv.example.goodposture.R;

public class MyRecordIndexFragment extends Fragment {
    private AppCompatActivity activity;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private int tabIndex;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_record_index, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleTabLayout();
        handleViewPager2();
    }

    private void findViews(View view) {
        tabLayout = view.findViewById(R.id.recordTabLayout);
        viewPager2 = view.findViewById(R.id.ViewPager2);
    }

    private void handleTabLayout() {
        // 監聽當下選擇的頁籤，存值給 tabIndex，讓 ViewPager2 根據得到的值顯示對應的 fragment
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("運動熱量紀錄")) {
                    tabIndex = 0;
                } else {
                    tabIndex = 1;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void handleViewPager2() {
        // 將自訂義的 PageAdapter 藉由 viewPager2 載入
        viewPager2.setAdapter(new PageAdapter(activity));
        // 使用 TabLayoutMediator 將 tabLayout & viewPager2 結合
        TabLayoutMediator tab = new TabLayoutMediator(tabLayout, viewPager2, (tab1, position) -> {
            // 根據頁籤位置設定頁籤名稱
            switch (position) {
                case 0:
                    tab1.setText("運動熱量紀錄");
                    break;
                case 1:
                    tab1.setText("飲食熱量紀錄");
                    break;
            }
        });
        // 將剛剛設定好的 TabLayoutMediator 依附在 viewPager2 上
        tab.attach();
    }

    public class PageAdapter extends FragmentStateAdapter {
        public PageAdapter(@NonNull Activity fragmentActivity) {
            super((FragmentActivity) fragmentActivity);
        }

        // 回傳有幾個頁籤
        @Override
        public int getItemCount() {
            return 2;
        }

        // 根據當前所在的頁籤呈現 fragment
        @Override
        public Fragment createFragment(int position) {
            // 獲取當前所在的頁籤位置藉由 setCurrentItem() 設定 position
            if (tabIndex == 0) {
                viewPager2.setCurrentItem(0);
            }
            // 藉由 position 的值判斷要載入哪個 fragment
            switch (position) {
                case 0:
                    return new MyExerciseRecordFragment();
                default:
                    return new MyEatRecordFragment();
            }
        }

    }
}