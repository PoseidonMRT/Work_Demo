package com.dyl.cloudtags;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.dyl.cloudtags.view.CircleView;
import com.dyl.cloudtags.view.KeywordsFlow;

public class MainActivity extends Activity {

	private KeywordsFlow keywordsFlow;
	private String[] keywords;
	private TextView world_city_refresh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initData();
	}

	private void initView() {
		keywordsFlow = (KeywordsFlow) findViewById(R.id.keywordsflow);
	}

	public void initData(){
		keywords = new String[] { "语文", "数学", "英语", "化学", "数学", "英语", "化学", "生物", "历史" };
		feedKeywordsFlow(keywordsFlow, keywords);
		keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
		keywordsFlow.setDuration(800l);
		keywordsFlow.setOnItemClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String keyword = ((CircleView) v).getText().toString();
				Log.e("KEY",keyword);
				keywords = null;
				keywords = new String[]{keyword,"Hello","Hello","Heloo"};
				keywordsFlow.rubKeywords();
				keywordsFlow.rubAllViews();
				feedKeywordsFlow(keywordsFlow, keywords);
				keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
			}
		});
	}

	private static void feedKeywordsFlow(KeywordsFlow keywordsFlow, String[] arr) {
		for (int i=0;i<arr.length;i++){
			keywordsFlow.feedKeyword(arr[i]);
		}
	}
}
