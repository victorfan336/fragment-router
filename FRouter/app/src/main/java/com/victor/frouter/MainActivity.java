package com.victor.frouter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

import com.victor.frouter.router.FragmentRouter;

import java.util.List;


public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentRouter.init(R.id.containt);
        new FragmentRouter.FRouterBuilder()
                .tag("Test")
                .build()
                .go(this);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
            int count = 0;
            List<Fragment> fragList = fm.getFragments();
            for (int i = 0; fragList != null && i < fragList.size(); i++) {
                if (fragList.get(i) != null) {
                    count++;
                }
            }
            if (count > 1) {
                Fragment exitFragment = fragList.get(count - 1);
                ft.setCustomAnimations(R.anim.activity_slide_in_from_left, R.anim.activity_slide_out_from_right);
                ft.remove(exitFragment);
                Fragment showFragment = fragList.get(count - 2);
                ft.show(showFragment);
            }
            ft.commitAllowingStateLoss();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
