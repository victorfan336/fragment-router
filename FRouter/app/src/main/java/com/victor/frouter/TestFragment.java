package com.victor.frouter;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.victor.frouter.annotation.FragmentRoute;
import com.victor.frouter.router.FragmentBuildInfo;
import com.victor.frouter.router.FragmentRouter;


/**
 * Created by Administrator on 2017/4/7.
 */
@FragmentRoute(moduleName = FragmentBuildInfo.APP, value = "Test")
public class TestFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        view.findViewById(R.id.btn_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestFragment.this.onClick();
            }
        });
        return view;
    }

    public void onClick() {
        new FragmentRouter.FRouterBuilder()
                .tag("A")
                .build()
                .go(getActivity());
    }

}
