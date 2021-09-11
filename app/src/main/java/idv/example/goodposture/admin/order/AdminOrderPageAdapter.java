package idv.example.goodposture.admin.order;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

import idv.example.goodposture.user.my.Order;

class AdminOrderPageAdapter extends FragmentStateAdapter {
    List<Fragment> fragmentList = new ArrayList<>();

    public AdminOrderPageAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.fragmentList.add(new AdminOrderStateFragment(Order.ORDER_STATE_READY));
        this.fragmentList.add(new AdminOrderStateFragment(Order.ORDER_STATE_SHIPPED));
        this.fragmentList.add(new AdminOrderStateFragment(Order.ORDER_STATE_RECEIVED));
        this.fragmentList.add(new AdminOrderStateFragment(Order.ORDER_STATE_CANCEL));
    }

    @Override
    public Fragment createFragment(int position) {
        return (Fragment) fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
