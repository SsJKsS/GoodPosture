package idv.example.goodposture.user.my;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;


class OrderStatePageAdapter extends FragmentStateAdapter {
    List<Fragment> fragmentList = new ArrayList<>();

    public OrderStatePageAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        //初始化pageAdaper切換的訂單狀態頁面們
        //ORDER_STATE_READY：待出貨
        //ORDER_STATE_SHIPPED：已出貨
        //ORDER_STATE_RECEIVED：已完成
        //ORDER_STATE_CANCEL：已取消
        this.fragmentList.add(new MyOrderStateFragment(Order.ORDER_STATE_READY));
        this.fragmentList.add(new MyOrderStateFragment(Order.ORDER_STATE_SHIPPED));
        this.fragmentList.add(new MyOrderStateFragment(Order.ORDER_STATE_RECEIVED));
        this.fragmentList.add(new MyOrderStateFragment(Order.ORDER_STATE_CANCEL));
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
