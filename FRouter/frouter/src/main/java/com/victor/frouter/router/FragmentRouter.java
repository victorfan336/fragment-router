package com.victor.frouter.router;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.victor.frouter.annotation.Consts;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.victor.frouter.annotation.Consts.DOT;
import static com.victor.frouter.annotation.Consts.ROUTE_TABLE;


/**
 * Created by Victor on 2017/4/10.
 */

public class FragmentRouter {

    private static FragmentRouter router;
    private Map<String, Class<? extends Fragment>> fragmentMap = new HashMap<>();
    private int mContainId;
    private FragmentRouterOption option;

    private FragmentRouter(int containId) {
        mContainId = containId;
        initFragmentTable();
    }

    /**
     * 必须进行初始化
     * @param containId
     */
    public static void init(int containId) {
        if (router == null) {
            synchronized (FragmentRouter.class) {
                if (router == null) {
                    router = new FragmentRouter(containId);
                }
            }
        }
    }

    private FragmentRouterOption getOption() {
        if (option == null) {
            option = new FragmentRouterOption();
        }
        return option;
    }

    public static FragmentRouter getInstance() {
        if (router == null) {
            new Throwable("FragmentRouter did not call the init function ! /n");
        }
        return router;
    }

    /**
     * 获取标注过的Fragment
     */
    private void initFragmentTable() {
        try {
            String [] modules = FragmentBuildInfo.ALL_MODULE;

            for (int i = 0; i < modules.length; i++) {
                String className = Consts.PACKAGE_NAME + DOT + modules[i] + ROUTE_TABLE;
                Class<?> routeTableClz = Class.forName(className);
                Constructor constructor = routeTableClz.getConstructor();
                IFragmentRouteTable instance = (IFragmentRouteTable) constructor.newInstance();
                instance.handleFragmentTable(fragmentMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Fragment getFragment() {
        if (TextUtils.isEmpty(option.getTag())) {
            new Throwable("fragment's tag cannot be null");
            return null;
        }
        // 类名是否是全路径
        if (!option.getTag().contains(FragmentBuildInfo.APP_PACKAGE_NAME)) {
            Class fragment = fragmentMap.get(option.getTag());
            if (fragment != null) {
                option.setTag(fragment.getName());
            } else {
                new Throwable("the fragment tag cannot find, please confirm the tag " + option.getTag());
            }
        }

        Fragment newFragment = null;
        try {
            Class<Fragment> classStr = (Class<Fragment>) Class.forName(option.getTag());
            Constructor constructor = classStr.getConstructor();
            newFragment = (Fragment) constructor.newInstance();
            newFragment.setArguments(option.getBundle());
        } catch (Exception e) {
            new Throwable("the fragment tag cannot find, please confirm the tag " + option.getTag());
        }
        return newFragment;
    }

    private boolean checkParams() {
        if (mContainId == 0) {
            new Throwable("FragmentRouter.init(); function did not init !");
            return false;
        }
        if (option == null) {
            new Throwable("Fragment router option did not init !");
            return false;
        }
        if (TextUtils.isEmpty(option.getTag())) {
            new Throwable("Fragment router params error, some params cannot null !");
            return false;
        }
        return true;
    }

    private void go(FragmentActivity activity) {
        if (checkParams()) {
            android.support.v4.app.FragmentManager fm = activity.getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();

            if (option.showAnim()) {
                ft.setCustomAnimations(option.getAnimIn(), option.getAnimOut());
            }

            int count = 0;
            List<Fragment> fragList = fm.getFragments();
            for (int i = 0; fragList != null && i < fragList.size(); i++) {
                if (fragList.get(i) != null) {
                    count++;
                }
            }
            if (count > 0) {
                Fragment fragment = fm.getFragments().get(count - 1);
                if (null != fragment) {
                    ft.hide(fragment);
                }
            }
            ft.add(mContainId, getFragment(), option.getTag());
            try {
//                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        option = null;
    }


    public static class FRouterBuilder {
        private FragmentRouterOption option;

        public FRouterBuilder() {
            option = new FragmentRouterOption();
        }

        public FRouterBuilder tag(String tag) {
            option.setTag(tag);
            return this;
        }

        public FRouterBuilder showAnim(boolean showAnim) {
            option.setShowAnim(showAnim);
            return this;
        }

        public FRouterBuilder setAnimIn(int animIn) {
            option.setAnimIn(animIn);
            return this;
        }

        public FRouterBuilder setAnimOut(int animOut) {
            option.setAnimOut(animOut);
            return this;
        }

        public FRouterBuilder bundle(Bundle bundle) {
            option.setBundle(bundle);
            return this;
        }

        public FRouterBuilder build() {
            option.clone(FragmentRouter.getInstance().getOption());
            return this;
        }

        public void go(FragmentActivity activity) {
            FragmentRouter.getInstance().go(activity);
        }
    }
}
