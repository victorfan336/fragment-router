package com.victor.frouter.router;

import android.os.Bundle;

import com.hbss.commonlib.router.R;


/**
 * Created by Victor on 2017/4/10.
 */

public class FragmentRouterOption {

    private String tag = "";
    private boolean showAnim = true;
    private int animIn = R.anim.activity_slide_in_from_right;
    private int animOut = R.anim.activity_slide_out_from_left;
    private Bundle bundle = new Bundle();

    public FragmentRouterOption() {
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean showAnim() {
        return showAnim;
    }

    public void setShowAnim(boolean showAnim) {
        this.showAnim = showAnim;
    }

    public int getAnimIn() {
        return animIn;
    }

    public void setAnimIn(int animIn) {
        this.animIn = animIn;
    }

    public int getAnimOut() {
        return animOut;
    }

    public void setAnimOut(int animOut) {
        this.animOut = animOut;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public void clone(FragmentRouterOption option) {
        if (option != null) {
            option.setTag(getTag());
            option.setBundle(getBundle());
            option.setAnimIn(getAnimIn());
            option.setAnimOut(getAnimOut());
            option.setShowAnim(showAnim());
        }
    }
}
