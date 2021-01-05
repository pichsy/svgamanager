# SvgaManager

SvgaManager 基础Svga封装的管理类。

生命周期自动管理svga动画，排序，优先级，无需关注太多，使用缓存，增加动画加载效率

如果觉得好用给我个star吧，开源不易。
如果遇到什么问题直接提issue。

### 引入

      
    implementation 'com.github.pichsy:svgamanager:1.0'
    
    ...


### 用法

    在Application中初始化
    SvgaManager.get().init(this);
     
    String url = "https://xxxx.svga";
    SvgaManager.get().with(this).push(url);
    SvgaManager.get().with(this).push(url, 100);
    List<String> list = new ArrayList<>();
    list.add(url);
    SvgaManager.get().with(this).push(list);
     