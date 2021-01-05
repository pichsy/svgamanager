package com.pichs.svgamanager;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;


import java.net.MalformedURLException;
import java.net.URL;

/**
 * @Description: Svga显示弹窗
 * @Author: WuBo
 * @CreateDate: 2020/11/12$ 15:44$
 * @UpdateUser: WuBo
 * @UpdateDate: 2020/11/12$ 15:44$
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class SvgaDialog extends PopupWindow {

    private FrameLayout mContainer;
    private View mBackView;
    private AppCompatActivity mActivity;
    private SVGAImageView mSVGImageView;
    private SVGAParser mSVGParser;
    private String mSvgUrl;
    private SvgaCallback mSvgaCallback;

    public SvgaDialog(AppCompatActivity activity) {
        this(activity, null);
    }

    public SvgaDialog(AppCompatActivity activity, AttributeSet attrs) {
        this(activity, attrs, R.style.Widget_AppCompat_PopupWindow);
    }

    public SvgaDialog(AppCompatActivity activity, AttributeSet attrs, int defStyleAttr) {
        super(activity, attrs, defStyleAttr);
        mActivity = activity;
        init();
    }

    private void init() {
        mSVGParser = new SVGAParser(mActivity);
        View rootView = View.inflate(mActivity, R.layout.dialog_svga_layout, null);
        initView(rootView);
        setContentView(rootView);

        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        // 全屏显示，在状态栏下也能看见
        setClippingEnabled(false);
        setFocusable(false);
        setOutsideTouchable(true);
    }


    public void initView(View rootView) {
        mBackView = rootView.findViewById(R.id.view_back);
        mContainer = rootView.findViewById(R.id.view_container);
        mSVGParser = SVGAParser.Companion.shareParser();
        mSVGImageView = new SVGAImageView(mActivity);
        mSVGImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mContainer.addView(mSVGImageView, new ViewGroup.LayoutParams(-1, -1));
        mSVGImageView.setCallback(new SVGACallback() {
            @Override
            public void onPause() {
            }

            @Override
            public void onFinished() {
                if (mSvgaCallback != null) {
                    mSvgaCallback.onFinish();
                }
            }

            @Override
            public void onRepeat() {
            }

            @Override
            public void onStep(int i, double v) {
            }
        });
        loadImageUrl(this.mSvgUrl);
    }


    @Override
    public void dismiss() {
        if (mSVGImageView != null) {
            mSVGImageView.stopAnimation();
        }
        super.dismiss();
    }

    public SvgaDialog setCallback(SvgaCallback svgaCallback) {
        this.mSvgaCallback = svgaCallback;
        return this;
    }

    public SvgaDialog setOnBackClickListener(View.OnClickListener onBackClickListener) {
        if (mBackView != null) {
            mBackView.setOnClickListener(onBackClickListener);
        }
        return this;
    }

    public void loadImageUrl(String url) {
        this.mSvgUrl = url;
        if (mSVGImageView != null && mSvgUrl != null) {
            try {
                URL surl = new URL(this.mSvgUrl);
                mSVGParser.decodeFromURL(surl, new SVGAParser.ParseCompletion() {
                    @Override
                    public void onComplete(SVGAVideoEntity svgaVideoEntity) {
                        mSVGImageView.setImageDrawable(new SVGADrawable(svgaVideoEntity));
                        mSVGImageView.setLoops(1);
                        mSVGImageView.startAnimation();
                    }

                    @Override
                    public void onError() {

                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isShowing() {
        return super.isShowing();
    }

    public void show() {
        showAtLocation(mActivity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

}
