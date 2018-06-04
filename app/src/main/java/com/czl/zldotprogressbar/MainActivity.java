package com.czl.zldotprogressbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.czl.zldotprogressbar2.ZLDotProgressBar;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    ZLDotProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ZLDotProgressBar)findViewById(R.id.progressbar);
        progressBar.setmTexts(Arrays.asList(new String[]{"已申请", "初审中", "预授结果", "复审中", "结束"}));
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setNewProgress(3);
    }
}
