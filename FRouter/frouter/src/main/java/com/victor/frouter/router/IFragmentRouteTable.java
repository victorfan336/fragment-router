package com.victor.frouter.router;

import android.support.v4.app.Fragment;

import java.util.Map;


/**
 * Created by Victor on 2017/4/12.
 */

public interface IFragmentRouteTable {
    void handleFragmentTable(Map<String, Class<? extends Fragment>> map);
}
