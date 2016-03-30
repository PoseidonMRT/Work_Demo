package com.tt.customcircleview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tt.customcircleview.view.CustomCircleView;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private CustomCircleView mCustomCircleView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCustomCircleView = (CustomCircleView) findViewById(R.id.custom);
        new Thread(mCustomCircleView).run();
    }
}
