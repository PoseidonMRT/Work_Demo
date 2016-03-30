package com.tt.gridviewsinpleadapter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private GridView mGirdView;
    private String[] str  = new String[]{"Hello","text"};
    private int[] res = new int[]{R.mipmap.ic_launcher,R.mipmap.ic_launcher};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGirdView = (GridView)findViewById(R.id.gridview);
        List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < str.length; i++) {
            Map<String, Object> listem = new HashMap<String, Object>();
            listem.put("textview", str[i]);
            listem.put("imageview", res[i]);
            listems.add(listem);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,listems,R.layout.item,new String[]{"imageview","textview"},new int[]{R.id.imageview,R.id.textview});
        mGirdView.setAdapter(simpleAdapter);
    }
}
