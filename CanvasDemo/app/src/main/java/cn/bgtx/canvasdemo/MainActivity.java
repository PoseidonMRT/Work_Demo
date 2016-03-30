package cn.bgtx.canvasdemo;

import java.io.File;
import java.io.FileOutputStream;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button btn_save, btn_resume;
	private ImageView iv_canvas;
	private Bitmap baseBitmap;
	private Canvas canvas;
	private Paint paint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*
		* 新建画笔
		* */
		paint = new Paint();
		paint.setStrokeWidth(5);
		paint.setColor(Color.RED);

		iv_canvas = (ImageView) findViewById(R.id.iv_canvas);
		btn_save = (Button) findViewById(R.id.btn_save);
		btn_resume = (Button) findViewById(R.id.btn_resume);

		btn_save.setOnClickListener(click);
		btn_resume.setOnClickListener(click);
		iv_canvas.setOnTouchListener(touch);
	}

	private View.OnTouchListener touch = new OnTouchListener() {
		/*
		* 开始坐标
		* X
		* Y
		* */
		float startX;
		float startY;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (baseBitmap == null) {
					baseBitmap = Bitmap.createBitmap(iv_canvas.getWidth(),
							iv_canvas.getHeight(), Bitmap.Config.ARGB_8888);
					canvas = new Canvas(baseBitmap);
					canvas.drawColor(Color.WHITE);
				}
				startX = event.getX();
				startY = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				float stopX = event.getX();
				float stopY = event.getY();
				canvas.drawLine(startX, startY, stopX, stopY, paint);
				startX = event.getX();
				startY = event.getY();
				iv_canvas.setImageBitmap(baseBitmap);
				break;
			case MotionEvent.ACTION_UP:

				break;
			default:
				break;
			}
			return true;
		}
	};
	private View.OnClickListener click = new OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.btn_save:
				saveBitmap();
				break;
			case R.id.btn_resume:
				resumeCanvas();
				break;
			default:
				break;
			}
		}
	};

	protected void saveBitmap() {
		try {
			File file = new File(Environment.getExternalStorageDirectory(),
					System.currentTimeMillis() + ".png");
			FileOutputStream stream = new FileOutputStream(file);
			baseBitmap.compress(CompressFormat.PNG, 100, stream);
			Toast.makeText(MainActivity.this, "保存成功！", Toast.LENGTH_LONG).show();
			
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
			intent.setData(Uri.fromFile(Environment
					.getExternalStorageDirectory()));
			sendBroadcast(intent);
		} catch (Exception e) {
			Toast.makeText(MainActivity.this, "保存失败！", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}


	protected void resumeCanvas() {
		if (baseBitmap != null) {
			baseBitmap = Bitmap.createBitmap(iv_canvas.getWidth(),
					iv_canvas.getHeight(), Bitmap.Config.ARGB_8888);
			canvas = new Canvas(baseBitmap);
			canvas.drawColor(Color.WHITE);
			iv_canvas.setImageBitmap(baseBitmap);
		}
	}
}
