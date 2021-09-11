package idv.example.goodposture.admin.order;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import idv.example.goodposture.R;


public class AdminOrderFragment extends Fragment {
    private AppCompatActivity activity;
    private TabLayout tl_admin_order;
    private ViewPager2 admin_order_viewpager2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       return inflater.inflate(R.layout.fragment_admin_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        handleViewPager2();
    }

    private void findViews(View view) {
        tl_admin_order = view.findViewById(R.id.tl_admin_order);
        admin_order_viewpager2 = view.findViewById(R.id.admin_order_viewpager2);
    }

    private void handleViewPager2() {
        admin_order_viewpager2.setAdapter(new AdminOrderPageAdapter(this.getActivity()));
        ArrayList<String> title = new ArrayList<>();
        title.add("待出貨");
        title.add("已出貨");
        title.add("已完成");
        title.add("已取消");
        //index 從0開始
        TabLayoutMediator tabLayoutMediator =
                new TabLayoutMediator(
                        tl_admin_order,
                        admin_order_viewpager2,
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