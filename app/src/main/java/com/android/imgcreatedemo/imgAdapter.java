package com.android.imgcreatedemo;

import android.content.Context;

import com.android.imgcreate.ImgCreateAdapter;

public class imgAdapter extends ImgCreateAdapter<String> {

    public imgAdapter(Context context, String data) {
        super(context, data);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.img_view;
    }

}
