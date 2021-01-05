package com.pichs.svgamanager;

/**
 * @Description: $
 * @Author: WuBo
 * @CreateDate: 2020/11/12$ 16:29$
 * @UpdateUser: WuBo
 * @UpdateDate: 2020/11/12$ 16:29$
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class SvgaInfo {

    private String url;
    private long priority;
    private boolean isFinish = false;

    public SvgaInfo(String url) {
        this.url = url;
    }

    public SvgaInfo(String url, long priority) {
        this.url = url;
        this.priority = priority;
    }

    public String getUrl() {
        return url;
    }

    public SvgaInfo setUrl(String url) {
        this.url = url;
        return this;
    }

    public long getPriority() {
        return priority;
    }

    public SvgaInfo setPriority(long priority) {
        this.priority = priority;
        return this;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public SvgaInfo setFinish(boolean finish) {
        isFinish = finish;
        return this;
    }
}
