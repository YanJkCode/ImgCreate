package com.android.imgcreate;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class ImgCreateAdapter<T> extends LinearLayout {
    private String imgName = "imgCreate";
    private String imgPath = "imgCreate";
    private View mContentView;
    private T data;
    private float ratio;

    public ImgCreateAdapter(Context context, T data) {
        super(context);
        this.data = data;
        setAlpha(0);
        setGravity(Gravity.CENTER);
        if (context instanceof Activity) {
            mContentView = LayoutInflater.from(context).inflate(getLayoutId(), this);
            renderView(mContentView, data);
        }
    }

    public void getBitMap(OnBitmapBackListener onBitmapBackListener) {
        if (onBitmapBackListener != null) {
            mOnBitmapBackListener = onBitmapBackListener;
            createImg();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (ratio != 0) {
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            //根据宽高比ratio和模式创建一个测量值
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (widthSize * ratio), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setRatio(float aspectW, float aspectH) {
        this.ratio = aspectW / aspectH;
    }

    private void createImg() {
        ViewParent parent = getParent();
        if (parent != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    if (mOnBitmapBackListener != null) {
                        mOnBitmapBackListener.onBitmapBack(saveBitmap(getContext(), createBitmapByViewAll(ImgCreateAdapter.this),
                                imgPath, imgName));
                    }
                    removeThis();
                }
            });
        } else {
            if (getContext() != null && getContext() instanceof Activity) {
                ViewGroup decorView = (ViewGroup) ((Activity) getContext()).getWindow().getDecorView();
                decorView.addView(this);
                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                setLayoutParams(layoutParams);
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (mOnBitmapBackListener != null) {
                            mOnBitmapBackListener.onBitmapBack(saveBitmap(getContext(), createBitmapByViewAll(ImgCreateAdapter.this),
                                    imgPath, imgName));
                        }
                        removeThis();
                    }
                });
            }
        }
    }

    protected abstract int getLayoutId();

    /**
     * 用于 设置布局内容 重写即可
     *
     * @param view
     * @param data
     */
    protected void renderView(View view, T data) {
    }

    public T getData() {
        return data;
    }

    public void setSaveImgName(String imgName) {
        this.imgName = imgName;
    }

    public void setSaveImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    /**
     * 用于截取一个bitmap对象
     *
     * @param view
     * @return
     */
    private static Bitmap createBitmapByView(View view) {
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    /**
     * 用于截取滑动控件超长图
     *
     * @param v
     * @return
     */
    public static Bitmap createBitmapByViewAll(View v) {
        Bitmap bmp = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        v.draw(c);
        return bmp;
    }

    private static File saveBitmap(Context context, Bitmap bitmap, String path, String fileName) {
        return saveBitmap(bitmap, new File(context.getExternalCacheDir(), path), fileName);
    }

    private static File saveBitmap(Bitmap bitmap, File file, String fileName) {
        if (file == null) {
            return null;
        }
        if (!file.exists()) {
            file.mkdirs();
        }
        File picFile = new File(file, fileName + ".png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(picFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
        return picFile;
    }

    public static Bitmap fileToBitmap(File file) {
        return BitmapFactory.decodeFile(file.getPath());
    }

    public static Uri pathToUri(String imgPath) {
        return Uri.parse(imgPath);
    }

    private OnBitmapBackListener mOnBitmapBackListener;

    /**
     * 返回图片的回调接口
     */
    public interface OnBitmapBackListener {
        void onBitmapBack(File imgFile);
    }

    /**
     * 把自身从父容器移除
     */
    public void removeThis() {
        post(new Runnable() {
            @Override
            public void run() {
                ViewParent parent = getParent();
                if (parent != null && parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(ImgCreateAdapter.this);
                }
            }
        });
    }
}
