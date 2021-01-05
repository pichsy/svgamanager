package com.pichs.app.svgamanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pichs.common.widget.cardview.XCardButton;
import com.pichs.common.widget.view.XImageView;
import com.pichs.svgamanager.SvgaManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String url = "https://xxxx.svga";
        SvgaManager.get().with(this).push(url);
        SvgaManager.get().with(this).push(url, 100);
        List<String> list = new ArrayList<>();
        list.add(url);
        SvgaManager.get().with(this).push(list);
    }


}