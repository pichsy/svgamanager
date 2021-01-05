package com.pichs.app.svgamanager;

import android.app.Application;

import com.pichs.common.widget.utils.XTypefaceHelper;
import com.pichs.svgamanager.SvgaManager;

/**
 * @Description:
 * @Author: 吴波
 * @CreateDate: 2021/1/4 14:49
 * @UpdateUser: 吴波
 * @UpdateDate: 2021/1/4 14:49
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        XTypefaceHelper.init(this);
        SvgaManager.get().init(this);
    }
}
