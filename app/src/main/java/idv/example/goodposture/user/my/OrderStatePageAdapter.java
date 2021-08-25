package idv.example.goodposture.user.my;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;


class OrderStatePageAdapter extends FragmentStateAdapter {
    List<Fragment> fragmentList = new ArrayList<>();
    //List<String> orderState = new ArrayList<>();

    public OrderStatePageAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
//        //初始化orderState清單
//        this.orderState.add("待出貨");
//        this.orderState.add("已出貨");
//        this.orderState.add("已取消");
        //初始化pageAdaper切換的頁面們
        this.fragmentList.add(new MyOrderStateFragment(Order.ORDER_STATE_READY));
        this.fragmentList.add(new MyOrderStateFragment(Order.ORDER_STATE_SHIPPED));
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
