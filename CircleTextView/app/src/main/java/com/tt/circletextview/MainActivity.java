package com.tt.circletextview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tt.circletextview.view.CircleTextView;

public class MainActivity extends AppCompatActivity {

    CircleTextView mCircleTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCircleTextView = (CircleTextView) findViewById(R.id.textview);
        mCircleTextView.setBackgroundColor(Color.GREEN);
    }
}
