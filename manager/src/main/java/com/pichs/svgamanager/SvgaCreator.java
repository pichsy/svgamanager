package com.pichs.svgamanager;

import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;


import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @Description: Svga处理类， 管理单个页面上陆续增加的Svga动画地址
 * @Author: WuBo
 * @CreateDate: 2020/11/12$ 16:06$
 * @UpdateUser: WuBo
 * @UpdateDate: 2020/11/12$ 16:06$
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class SvgaCreator implements SvgaCallback, LifecycleObserver {

    private AppCompatActivity mActivity;
    private SvgaDialog mSvgaDialog;
    private SvgaInfo mCurSvgaInfo;
    // 为保证优默认优先级，这里单独维护一个_index计数, 多线程可见
    private volatile static long _index = 0;
    private boolean isOnResume = false;

    // 队列(线程安全)，Priority 越小优先级越高。
    private final PriorityBlockingQueue<SvgaInfo> mSvgaQueue = new PriorityBlockingQueue<>(1, (d1, d2) -> Long.compare(d1.getPriority(), d2.getPriority()));

    public SvgaCreator(@NotNull AppCompatActivity activity) {
        if (null == activity) {
            throw new RuntimeException("SvgaManager2: activity can not be null reference");
        }
        with(activity);
    }

    /**
     * 返回键监听集合，管理
     */
    private final Map<AppCompatActivity, View.OnClickListener> mCallbackMap = new HashMap<>();

    private SvgaCreator with(@NotNull AppCompatActivity activity) {
        // 换页面了吊起了动画，则重置状态
        if (this.mActivity == null) {
            this.mActivity = activity;
            this.mActivity.getLifecycle().addObserver(this);
        } else if (this.mActivity != activity) {
            onDestroy();
            this.mActivity = activity;
            this.mActivity.getLifecycle().addObserver(this);
        }
        createDialogIfNotExist();
        return this;
    }

    /**
     * 当前动画播放完成时回调
     */
    @Override
    public void onFinish() {
        SvgaUtils.runOnUiThread(() -> {
            if (mCurSvgaInfo != null) {
                mCurSvgaInfo.setFinish(true);
            }
            removeTop();
            showNextOne();
        });
    }

    /**
     * 创建dialog
     */
    private void createDialogIfNotExist() {
        SvgaUtils.runOnUiThread(() -> {
            if (mSvgaDialog == null) {
                mSvgaDialog = new SvgaDialog(mActivity).setCallback(SvgaCreator.this)
                        .setOnBackClickListener(view -> {
                            if (mActivity != null) {
                                View.OnClickListener onClickListener = mCallbackMap.get(mActivity);
                                if (onClickListener != null) {
                                    onClickListener.onClick(view);
                                } else {
                                    mCallbackMap.remove(mActivity);
                                }
                            }
                        });
            }
        });
    }

    /**
     * 添加动画返回键的点击监听
     *
     * @param onClickListener
     * @return
     */
    public SvgaCreator addBackClickListener(View.OnClickListener onClickListener) {
        if (onClickListener != null) {
            SvgaUtils.runOnUiThread(() -> mCallbackMap.put(mActivity, onClickListener));
        }
        return this;
    }

    /**
     * 添加一个动画地址到队列
     *
     * @param url svga动画地址
     */
    public void push(String url) {
        SvgaUtils.runOnUiThread(() -> {
            ++_index;
            push(url, SystemClock.uptimeMillis() + _index);
        });
    }

    /**
     * 添加一个动画地址到队列
     *
     * @param url      svga动画地址
     * @param priority 优先级，值越大，优先级越低
     */
    public void push(String url, long priority) {
        SvgaUtils.runOnUiThread(() -> {
            if (TextUtils.isEmpty(url)) {
                return;
            }
            mSvgaQueue.add(new SvgaInfo(url, priority));
            if (isCanShowDialog()) {
                showNextOne();
            }
        });
    }

    /**
     * 添加动画队列进来，执行顺序跟列表的特性和顺序相关，建议使用 ArrayList<String>
     *
     * @param list ArrayList<String>
     */
    public void push(List<String> list) {
        SvgaUtils.runOnUiThread(() -> {
            for (String url : list) {
                push(url);
            }
        });
    }

    private void showNextOne() {
        if (mSvgaQueue.isEmpty()) {
            dismissDialog();
            return;
        }
        mCurSvgaInfo = mSvgaQueue.element();
        // 这里做一个循环剔除坏数据
        while (mCurSvgaInfo == null && !mSvgaQueue.isEmpty()) {
            mCurSvgaInfo = mSvgaQueue.poll();
        }
        // 二次校验，安全退出
        if (mCurSvgaInfo == null || mSvgaQueue.isEmpty()) {
            dismissDialog();
            return;
        }
        showDialog();
        if (mSvgaDialog != null) {
            mSvgaDialog.loadImageUrl(mCurSvgaInfo.getUrl());
        }
    }

    private boolean isCanShowDialog() {
        if (mSvgaQueue.isEmpty()) {
            return false;
        }
        if (mSvgaDialog != null && mSvgaDialog.isShowing()) {
            return false;
        }
        if (mCurSvgaInfo != null && !mCurSvgaInfo.isFinish()) {
            return false;
        }
        return true;
    }

    private void removeTop() {
        mSvgaQueue.poll();
    }

    private void showDialog() {
        if (mActivity == null) return;
        createDialogIfNotExist();
        if (mSvgaDialog != null && !mSvgaDialog.isShowing()) {
            mSvgaDialog.show();
        }
    }

    private void dismissDialog() {
        SvgaUtils.runOnUiThread(() -> {
            if (mSvgaDialog != null) {
                mSvgaDialog.dismiss();
                _index = 0;
            }
        });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        isOnResume = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        isOnResume = false;
    }

    /**
     * 销毁的时候清除数据
     * 释放，下次进入再创建。
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        isOnResume = false;
        mSvgaQueue.clear();
        dismissDialog();
        _index = 0;
        mSvgaDialog = null;
        if (mActivity != null) {
            mCallbackMap.remove(mActivity);
            SvgaManager.get().removeSvgaCreator(mActivity);
            mActivity.getLifecycle().removeObserver(this);
            mActivity = null;
        }
        mCurSvgaInfo = null;
    }
}
