package com.pichs.svgamanager;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.net.http.HttpResponseCache;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.opensource.svgaplayer.SVGACache;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.utils.log.SVGALogger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: Svga管理类，分页面管理，不页面之间相互不冲突。
 * @Author: WuBo
 * @CreateDate: 2020/11/13$ 18:45$
 * @UpdateUser: WuBo
 * @UpdateDate: 2020/11/13$ 18:45$
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class SvgaManager {

    private SvgaManager() {
    }

    private final static class Holder {
        @SuppressLint("StaticFieldLeak")
        private static final SvgaManager _instance = new SvgaManager();
    }

    public static SvgaManager get() {
        return SvgaManager.Holder._instance;
    }

    private final Map<AppCompatActivity, SvgaCreator> mCreators = new ConcurrentHashMap<>();

    private volatile AppCompatActivity mCurrentActivity;

    private boolean isCacheInit = false;

    public void init(Context context) {
        if (isCacheInit) {
            return;
        }
        // 提前设置文件缓存
        SVGACache.INSTANCE.onCreate(context.getApplicationContext(), SVGACache.Type.FILE);

        SVGAParser.Companion.shareParser().init(context.getApplicationContext());
        try {
            File cacheDir = new File(context.getApplicationContext().getCacheDir(), "svga");
            HttpResponseCache.install(cacheDir, 512 * 1024 * 1024);
            // svga日志
            SVGALogger.INSTANCE.setLogEnabled(false);
            isCacheInit = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized SvgaManager with(AppCompatActivity activity) {
        if (activity == null) {
            throw new RuntimeException("activity can not be null reference");
        }
        init(activity);
        mCurrentActivity = activity;
        getCreator(activity);
        return this;
    }

    private SvgaCreator getCreator(AppCompatActivity activity) {
        SvgaCreator svgaCreator = mCreators.get(activity);
        if (svgaCreator == null) {
            svgaCreator = new SvgaCreator(activity);
            mCreators.put(activity, svgaCreator);
        }
        return svgaCreator;
    }

    public void push(String url) {
        getCreator(mCurrentActivity).push(url);
    }

    public void push(String url, int priority) {
        getCreator(mCurrentActivity).push(url, priority);
    }

    public void push(List<String> urlList) {
        getCreator(mCurrentActivity).push(urlList);
    }

    public void addBackClickListener(View.OnClickListener onClickListener) {
        getCreator(mCurrentActivity).addBackClickListener(onClickListener);
    }

    public void removeSvgaCreator(AppCompatActivity activity) {
        if (activity != null) {
            mCreators.remove(activity);
        }
    }
}
