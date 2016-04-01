package com.dyl.cloudtags.view;

import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import com.dyl.cloudtags.R;
import com.dyl.cloudtags.utils.MeasureUtils;
import com.dyl.cloudtags.view.CircleView;

public class KeywordsFlow extends FrameLayout implements OnGlobalLayoutListener {

	/*
	* 处理关系的Paint
	* */
	private Paint mPaint;

	public static final int IDX_X = 0;
	public static final int IDX_Y = 1;
	public static final int IDX_TXT_LENGTH = 2;
	public static final int IDX_DIS_Y = 3;

	/*
	* 定义动画类型
	* ANIMATION_IN-----由外向内
	* ANIMATION_OUT----由内向外
	* */
	public static final int ANIMATION_IN = 1;
	public static final int ANIMATION_OUT = 2;

	/*
	* 位移动画类型
	* OUTSIDE_TO_LOCATION----外围到所在位置
	* LOCATION_TO_OUTSIDE----所在位置到外围
	* CENTER_TO_LOCATION-----中心到所在位置
	* LOCATION_TO_CENTER-----所在位置到中心
	* */
	public static final int OUTSIDE_TO_LOCATION = 1;
	public static final int LOCATION_TO_OUTSIDE = 2;
	public static final int CENTER_TO_LOCATION = 3;
	public static final int LOCATION_TO_CENTER = 4;

	/*
	* 属性动画
	* AlphaAnimation---透明度变化动画
	* ScaleAnimation---缩放动画
	* */
	private static AlphaAnimation animAlpha2Opaque;
	private static AlphaAnimation animAlpha2Transparent;
	private static ScaleAnimation animScaleLarge2Normal, animScaleNormal2Large, animScaleZero2Normal, animScaleNormal2Zero;


	/*
	* 文本进入时的动画类型标记
	* txtAnimInType----TextView进入
	* txtAnimOutType---TextView淡出
	* */
	private int txtAnimInType, txtAnimOutType;

	/*
	* 存储所有的文本信息
	* */
	private Vector<String> vecKeywords;

	/*
	* TextView的存储List
	* */
	LinkedList<CircleView> listTxtTop = new LinkedList<CircleView>();
	LinkedList<CircleView> listTxtBottom = new LinkedList<CircleView>();

	/*
	* 当前结点元素的索引值
	* */
	private int mCurrentIndex;

	public static final long ANIM_DURATION = 800l;
	public static final int TEXT_SIZE_MAX = 20;
	public static final int TEXT_SIZE_MIN = 10;
	private OnClickListener itemClickListener;
	private static Interpolator interpolator;


	private int width, height;
	private boolean enableShow;
	private Random random;


	private long lastStartAnimationTime;
	private long animDuration;
	private Context mContext;

	public interface ZoomViewListener {

		void onZoomStarted(float zoom, float zoomx, float zoomy);

		void onZooming(float zoom, float zoomx, float zoomy);

		void onZoomEnded(float zoom, float zoomx, float zoomy);
	}

	// zooming
	float zoom = 1.0f;
	float maxZoom = 2.0f;
	float smoothZoom = 1.0f;
	float zoomX, zoomY;
	float smoothZoomX, smoothZoomY;
	private boolean scrolling; // NOPMD by karooolek on 29.06.11 11:45

	// minimap variables
	private boolean showMinimap = false;
	private int miniMapColor = Color.BLACK;
	private int miniMapHeight = -1;
	private String miniMapCaption;
	private float miniMapCaptionSize = 10.0f;
	private int miniMapCaptionColor = Color.WHITE;

	// touching variables
	private long lastTapTime;
	private float touchStartX, touchStartY;
	private float touchLastX, touchLastY;
	private float startd;
	private boolean pinching;
	private float lastd;
	private float lastdx1, lastdy1;
	private float lastdx2, lastdy2;

	// drawing
	private final Matrix m = new Matrix();
	private final Paint p = new Paint();

	// listener
	ZoomViewListener listener;

	private Bitmap ch;

	public KeywordsFlow(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public KeywordsFlow(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public KeywordsFlow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	private void init() {
		lastStartAnimationTime = 0l;
		animDuration = ANIM_DURATION;
		random = new Random();
		mPaint = new Paint();
		vecKeywords = new Vector<String>();
		getViewTreeObserver().addOnGlobalLayoutListener(this);
		interpolator = AnimationUtils.loadInterpolator(getContext(), android.R.anim.decelerate_interpolator);
		animAlpha2Opaque = new AlphaAnimation(0.0f, 1.0f);
		animAlpha2Transparent = new AlphaAnimation(1.0f, 0.0f);
		animScaleLarge2Normal = new ScaleAnimation(2, 1, 2, 1);
		animScaleNormal2Large = new ScaleAnimation(1, 2, 1, 2);
		animScaleZero2Normal = new ScaleAnimation(0, 1, 0, 1);
		animScaleNormal2Zero = new ScaleAnimation(1, 0, 1, 0);
	}

	public float getZoom() {
		return zoom;
	}

	public float getMaxZoom() {
		return maxZoom;
	}

	public void setMaxZoom(final float maxZoom) {
		if (maxZoom < 1.0f) {
			return;
		}

		this.maxZoom = maxZoom;
	}

	public void setMiniMapEnabled(final boolean showMiniMap) {
		this.showMinimap = showMiniMap;
	}

	public boolean isMiniMapEnabled() {
		return showMinimap;
	}

	public void setMiniMapHeight(final int miniMapHeight) {
		if (miniMapHeight < 0) {
			return;
		}
		this.miniMapHeight = miniMapHeight;
	}

	public int getMiniMapHeight() {
		return miniMapHeight;
	}

	public void setMiniMapColor(final int color) {
		miniMapColor = color;
	}

	public int getMiniMapColor() {
		return miniMapColor;
	}

	public String getMiniMapCaption() {
		return miniMapCaption;
	}

	public void setMiniMapCaption(final String miniMapCaption) {
		this.miniMapCaption = miniMapCaption;
	}

	public float getMiniMapCaptionSize() {
		return miniMapCaptionSize;
	}

	public void setMiniMapCaptionSize(final float size) {
		miniMapCaptionSize = size;
	}

	public int getMiniMapCaptionColor() {
		return miniMapCaptionColor;
	}

	public void setMiniMapCaptionColor(final int color) {
		miniMapCaptionColor = color;
	}

	public void zoomTo(final float zoom, final float x, final float y) {
		this.zoom = Math.min(zoom, maxZoom);
		zoomX = x;
		zoomY = y;
		smoothZoomTo(this.zoom, x, y);
	}

	public void smoothZoomTo(final float zoom, final float x, final float y) {
		smoothZoom = clamp(1.0f, zoom, maxZoom);
		smoothZoomX = x;
		smoothZoomY = y;
		if (listener != null) {
			listener.onZoomStarted(smoothZoom, x, y);
		}
	}

	public ZoomViewListener getListener() {
		return listener;
	}

	public void setListner(final ZoomViewListener listener) {
		this.listener = listener;
	}

	public float getZoomFocusX() {
		return zoomX * zoom;
	}

	public float getZoomFocusY() {
		return zoomY * zoom;
	}

	@Override
	public boolean dispatchTouchEvent(final MotionEvent ev) {
		// single touch
		if (ev.getPointerCount() == 1) {
			processSingleTouchEvent(ev);
		}

		// // double touch
		if (ev.getPointerCount() == 2) {
			processDoubleTouchEvent(ev);
		}

		// redraw
		getRootView().invalidate();
		invalidate();

		return true;
	}

	private void processSingleTouchEvent(final MotionEvent ev) {

		final float x = ev.getX();
		final float y = ev.getY();

		final float w = miniMapHeight * (float) getWidth() / getHeight();
		final float h = miniMapHeight;
		final boolean touchingMiniMap = x >= 10.0f && x <= 10.0f + w && y >= 10.0f && y <= 10.0f + h;

		if (showMinimap && smoothZoom > 1.0f && touchingMiniMap) {
			processSingleTouchOnMinimap(ev);
		} else {
			processSingleTouchOutsideMinimap(ev);
		}
	}

	private void processSingleTouchOnMinimap(final MotionEvent ev) {
		final float x = ev.getX();
		final float y = ev.getY();

		final float w = miniMapHeight * (float) getWidth() / getHeight();
		final float h = miniMapHeight;
		final float zx = (x - 10.0f) / w * getWidth();
		final float zy = (y - 10.0f) / h * getHeight();
		smoothZoomTo(smoothZoom, zx, zy);
	}

	private void processSingleTouchOutsideMinimap(final MotionEvent ev) {
		final float x = ev.getX();
		final float y = ev.getY();
		float lx = x - touchStartX;
		float ly = y - touchStartY;
		final float l = (float) Math.hypot(lx, ly);
		float dx = x - touchLastX;
		float dy = y - touchLastY;
		touchLastX = x;
		touchLastY = y;

		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touchStartX = x;
				touchStartY = y;
				touchLastX = x;
				touchLastY = y;
				dx = 0;
				dy = 0;
				lx = 0;
				ly = 0;
				scrolling = false;
				break;

			case MotionEvent.ACTION_MOVE:
				if (scrolling || (smoothZoom > 1.0f && l > 30.0f)) {
					if (!scrolling) {
						scrolling = true;
						ev.setAction(MotionEvent.ACTION_CANCEL);
						super.dispatchTouchEvent(ev);
					}
					smoothZoomX -= dx / zoom;
					smoothZoomY -= dy / zoom;
					return;
				}
				break;

			case MotionEvent.ACTION_OUTSIDE:
			case MotionEvent.ACTION_UP:

				// tap
				if (l < 30.0f) {
					// check double tap
					if (System.currentTimeMillis() - lastTapTime < 500) {
						if (smoothZoom == 1.0f) {
							smoothZoomTo(maxZoom, x, y);
						} else {
							smoothZoomTo(1.0f, getWidth() / 2.0f, getHeight() / 2.0f);
						}
						lastTapTime = 0;
						ev.setAction(MotionEvent.ACTION_CANCEL);
						super.dispatchTouchEvent(ev);
						return;
					}

					lastTapTime = System.currentTimeMillis();

					performClick();
				}
				break;

			default:
				break;
		}

		ev.setLocation(zoomX + (x - 0.5f * getWidth()) / zoom, zoomY + (y - 0.5f * getHeight()) / zoom);

		ev.getX();
		ev.getY();

		super.dispatchTouchEvent(ev);
	}

	private void processDoubleTouchEvent(final MotionEvent ev) {
		final float x1 = ev.getX(0);
		final float dx1 = x1 - lastdx1;
		lastdx1 = x1;
		final float y1 = ev.getY(0);
		final float dy1 = y1 - lastdy1;
		lastdy1 = y1;
		final float x2 = ev.getX(1);
		final float dx2 = x2 - lastdx2;
		lastdx2 = x2;
		final float y2 = ev.getY(1);
		final float dy2 = y2 - lastdy2;
		lastdy2 = y2;

		// pointers distance
		final float d = (float) Math.hypot(x2 - x1, y2 - y1);
		final float dd = d - lastd;
		lastd = d;
		final float ld = Math.abs(d - startd);

		Math.atan2(y2 - y1, x2 - x1);
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startd = d;
				pinching = false;
				break;

			case MotionEvent.ACTION_MOVE:
				if (pinching || ld > 30.0f) {
					pinching = true;
					final float dxk = 0.5f * (dx1 + dx2);
					final float dyk = 0.5f * (dy1 + dy2);
					smoothZoomTo(Math.max(1.0f, zoom * d / (d - dd)), zoomX - dxk / zoom, zoomY - dyk / zoom);
				}

				break;

			case MotionEvent.ACTION_UP:
			default:
				pinching = false;
				break;
		}

		ev.setAction(MotionEvent.ACTION_CANCEL);
		super.dispatchTouchEvent(ev);
	}

	private float clamp(final float min, final float value, final float max) {
		return Math.max(min, Math.min(value, max));
	}

	private float lerp(final float a, final float b, final float k) {
		return a + (b - a) * k;
	}

	private float bias(final float a, final float b, final float k) {
		return Math.abs(b - a) >= k ? a + k * Math.signum(b - a) : b;
	}


	public long getDuration() {
		return animDuration;
	}

	public void setDuration(long duration) {
		animDuration = duration;
	}

	public boolean feedKeyword(String keyword) {
		boolean result = false;
		result = vecKeywords.add(keyword);
		Log.e("KeyWord",vecKeywords.toString());
		return result;
	}

	/*
	* 开始显示动画，已存在TextView显示退出动画
	*@return 正常显示动画返回true；反之为false。返回false原因如下：<br/>
	*         1.时间上不允许，受lastStartAnimationTime的制约；<br/>
	*         2.未获取到width和height的值。<br/>
	* */
	public boolean go2Show(int animType) {
		if (System.currentTimeMillis() - lastStartAnimationTime > animDuration) {
			enableShow = true;
			if (animType == ANIMATION_IN) {
				txtAnimInType = OUTSIDE_TO_LOCATION;
				txtAnimOutType = LOCATION_TO_CENTER;
			} else if (animType == ANIMATION_OUT) {
				txtAnimInType = CENTER_TO_LOCATION;
				txtAnimOutType = LOCATION_TO_OUTSIDE;
			}
			disapper();
			boolean result = show();
			return result;
		}
		return false;
	}

	private void disapper() {
		int size = getChildCount();
		for (int i = size - 1; i >= 0; i--) {
			final CircleView txv = (CircleView) getChildAt(i);
			if (txv.getVisibility() == View.GONE) {
				removeView(txv);
				continue;
			}
			FrameLayout.LayoutParams layParams = (LayoutParams) txv
					.getLayoutParams();
			int[] xy = new int[] { layParams.leftMargin, layParams.topMargin,
					txv.getWidth() };
			AnimationSet animSet = getAnimationSet(xy, (width >> 1),
					(height >> 1), txtAnimOutType);
			txv.startAnimation(animSet);
			animSet.setAnimationListener(new AnimationListener() {
				public void onAnimationStart(Animation animation) {
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
					txv.setOnClickListener(null);
					txv.setClickable(false);
					txv.setVisibility(View.GONE);
				}
			});
		}
	}

	private boolean show() {
		Log.e("keywords",vecKeywords.toString());
		if (width > 0 && height > 0 && vecKeywords != null
				&& vecKeywords.size() > 0 && enableShow) {
			enableShow = false;
			lastStartAnimationTime = System.currentTimeMillis();
			int xCenter = width >> 1, yCenter = height >> 1;
			int size = vecKeywords.size();
			int xItem = width / size, yItem = height / size;
			LinkedList<Integer> listX = new LinkedList<Integer>(), listY = new LinkedList<Integer>();
			for (int i = 0; i < size; i++) {
				listX.add(i * xItem);
				listY.add(i * yItem + (yItem >> 2));
			}
			Log.e("VecSize",vecKeywords.size()+"size");
			for (int i = 0; i < size; i++) {
				String keyword = vecKeywords.get(i);
				int xy[] = randomXY(random, listX, listY, xItem);
				final CircleView txv = new CircleView(getContext());
				txv.setBackgroundResource(R.drawable.text_view_border);
				txv.setGravity(Gravity.CENTER);
				txv.setOnClickListener(itemClickListener);
				txv.setText(keyword);
				txv.setTextColor(Color.BLACK);
				txv.setPadding(5, 5, 5, 5);
				txv.setSingleLine(true);
//				int r = random.nextInt(256);
//	            int g= random.nextInt(256);
//	            int b = random.nextInt(256);
	            int mColor = Color.rgb(255, 255, 255);
	            GradientDrawable myGrad = (GradientDrawable)txv.getBackground();
	            myGrad.setColor(mColor);
				Paint paint = txv.getPaint();
				int strWidth = (int) Math.ceil(paint.measureText(keyword));
				xy[IDX_TXT_LENGTH] = strWidth;
				// ��һ������:����x����
				if (xy[IDX_X] + strWidth > width - (xItem >> 1)) {
					int baseX = width - strWidth;
					// �����ı��ұ�Եһ���ĸ���
					xy[IDX_X] = baseX - xItem + random.nextInt(xItem >> 1);
				} else if (xy[IDX_X] == 0) {
					// �����ı����Եһ���ĸ���
					xy[IDX_X] = Math.max(random.nextInt(xItem), xItem / 3);
				}
				xy[IDX_DIS_Y] = Math.abs(xy[IDX_Y] - yCenter);
				txv.setTag(xy);
				if (xy[IDX_Y] > yCenter) {
					listTxtBottom.add(txv);
				} else {
					listTxtTop.add(txv);
				}
			}
			Log.e("SIZE",listTxtTop.size()+"sjkjs"+listTxtBottom.size());
			attach2Screen(listTxtTop, xCenter, yCenter, yItem);
			attach2Screen(listTxtBottom, xCenter, yCenter, yItem);
			return true;
		}
		return false;
	}

	/** ����TextView��Y���꽫������ӵ������ϡ� */
	private void attach2Screen(LinkedList<CircleView> listTxt, int xCenter,
			int yCenter, int yItem) {
		int size = listTxt.size();
		sortXYList(listTxt, size);
		for (int i = 0; i < size; i++) {
			CircleView txv = listTxt.get(i);
			int[] iXY = (int[]) txv.getTag();
			// �ڶ�������:����y����
			int yDistance = iXY[IDX_Y] - yCenter;
			// ����������ĵ�ģ���ֵ�������yItem<br/>
			// ���ڿ���һ·�½������ĵ�ģ����ֵҲ����Ӧ�����Ĵ�С<br/>
			int yMove = Math.abs(yDistance);
			inner: for (int k = i - 1; k >= 0; k--) {
				int[] kXY = (int[]) listTxt.get(k).getTag();
				int startX = kXY[IDX_X];
				int endX = startX + kXY[IDX_TXT_LENGTH];
				// y�������ĵ�Ϊ�ָ��ߣ���ͬһ��
				if (yDistance * (kXY[IDX_Y] - yCenter) > 0) {
					if (isXMixed(startX, endX, iXY[IDX_X], iXY[IDX_X]
							+ iXY[IDX_TXT_LENGTH])) {
						int tmpMove = Math.abs(iXY[IDX_Y] - kXY[IDX_Y]);
						if (tmpMove > yItem) {
							yMove = tmpMove;
						} else if (yMove > 0) {
							// ȡ��Ĭ��ֵ��
							yMove = 0;
						}
						break inner;
					}
				}
			}
			if (yMove > yItem) {
				int maxMove = yMove - yItem;
				int randomMove = random.nextInt(maxMove);
				int realMove = Math.max(randomMove, maxMove >> 1) * yDistance / Math.abs(yDistance);
				iXY[IDX_Y] = iXY[IDX_Y] - realMove;
				iXY[IDX_DIS_Y] = Math.abs(iXY[IDX_Y] - yCenter);
				// �Ѿ�������ǰi����Ҫ�ٴ�����
				sortXYList(listTxt, i + 1);
			}
			FrameLayout.LayoutParams layParams = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.WRAP_CONTENT,
					FrameLayout.LayoutParams.WRAP_CONTENT);
			layParams.gravity = Gravity.LEFT | Gravity.TOP;
			layParams.leftMargin = iXY[IDX_X];
			layParams.topMargin = iXY[IDX_Y];
			addView(txv, layParams);
			// ����
			AnimationSet animSet = getAnimationSet(iXY, xCenter, yCenter,
					txtAnimInType);
			txv.startAnimation(animSet);
		}
	}

	public AnimationSet getAnimationSet(int[] xy, int xCenter, int yCenter,
			int type) {
		AnimationSet animSet = new AnimationSet(true);
		animSet.setInterpolator(interpolator);
		if (type == OUTSIDE_TO_LOCATION) {
			animSet.addAnimation(animAlpha2Opaque);
			animSet.addAnimation(animScaleLarge2Normal);
			TranslateAnimation translate = new TranslateAnimation((xy[IDX_X]
					+ (xy[IDX_TXT_LENGTH] >> 1) - xCenter) << 1, 0,
					(xy[IDX_Y] - yCenter) << 1, 0);
			animSet.addAnimation(translate);
		} else if (type == LOCATION_TO_OUTSIDE) {
			animSet.addAnimation(animAlpha2Transparent);
			animSet.addAnimation(animScaleNormal2Large);
			TranslateAnimation translate = new TranslateAnimation(0, (xy[IDX_X]
					+ (xy[IDX_TXT_LENGTH] >> 1) - xCenter) << 1, 0,
					(xy[IDX_Y] - yCenter) << 1);
			animSet.addAnimation(translate);
		} else if (type == LOCATION_TO_CENTER) {
			animSet.addAnimation(animAlpha2Transparent);
			animSet.addAnimation(animScaleNormal2Zero);
			TranslateAnimation translate = new TranslateAnimation(0,
					(-xy[IDX_X] + xCenter), 0, (-xy[IDX_Y] + yCenter));
			animSet.addAnimation(translate);
		} else if (type == CENTER_TO_LOCATION) {
			animSet.addAnimation(animAlpha2Opaque);
			animSet.addAnimation(animScaleZero2Normal);
			TranslateAnimation translate = new TranslateAnimation(
					(-xy[IDX_X] + xCenter), 0, (-xy[IDX_Y] + yCenter), 0);
			animSet.addAnimation(translate);
		}
		animSet.setDuration(animDuration);
		return animSet;
	}

	private void sortXYList(LinkedList<CircleView> listTxt, int endIdx) {
		for (int i = 0; i < endIdx; i++) {
			for (int k = i + 1; k < endIdx; k++) {
				if (((int[]) listTxt.get(k).getTag())[IDX_DIS_Y] < ((int[]) listTxt
						.get(i).getTag())[IDX_DIS_Y]) {
					CircleView iTmp = listTxt.get(i);
					CircleView kTmp = listTxt.get(k);
					listTxt.set(i, kTmp);
					listTxt.set(k, iTmp);
				}
			}
		}
	}

	/** A�߶���B�߶��������ֱ����X��ӳ�����Ƿ��н����� */
	private boolean isXMixed(int startA, int endA, int startB, int endB) {
		boolean result = false;
		if (startB >= startA && startB <= endA) {
			result = true;
		} else if (endB >= startA && endB <= endA) {
			result = true;
		} else if (startA >= startB && startA <= endB) {
			result = true;
		} else if (endA >= startB && endA <= endB) {
			result = true;
		}
		return result;
	}

	/*
	* 随机生成XY
	* */
	private int[] randomXY(Random ran, LinkedList<Integer> listX, LinkedList<Integer> listY, int xItem) {
		int[] arr = new int[4];
		arr[IDX_X] = listX.remove(ran.nextInt(listX.size()));
		arr[IDX_Y] = listY.remove(ran.nextInt(listY.size()));
		return arr;
	}

	public void onGlobalLayout() {
		int tmpW = getWidth();
		int tmpH = getHeight();
		if (width != tmpW || height != tmpH) {
			width = tmpW;
			height = tmpH;
			show();
		}
	}

	public Vector<String> getKeywords() {
		return vecKeywords;
	}

	public void rubKeywords() {
		vecKeywords.clear();
	}

	public void rubAllViews() {
		listTxtTop.clear();
		listTxtBottom.clear();
		removeAllViews();
	}

	public void setOnItemClickListener(OnClickListener listener) {
		itemClickListener = listener;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		mPaint.setColor(Color.RED);
		//设置画出的线的 粗细程度
		mPaint.setStrokeWidth(5);
		int index = getKeyWordsIndex(listTxtTop,vecKeywords.get(0));
		boolean isTop = true;
		//画出一根线
		if (index != -1){
			mCurrentIndex = index;
		}else{
			mCurrentIndex = getKeyWordsIndex(listTxtBottom,vecKeywords.get(0));
			isTop = false;
		}
		if (isTop){
			for (int i=0 ; i<listTxtTop.size();i++){
				if (i != mCurrentIndex){
					canvas.drawLine(MeasureUtils.getTextViewCenterX(listTxtTop.get(mCurrentIndex)),MeasureUtils.getTextViewCenterY(listTxtTop.get(mCurrentIndex)),
							MeasureUtils.getTextViewCenterX(listTxtTop.get(i)),MeasureUtils.getTextViewCenterY(listTxtTop.get(i)),mPaint);
				}
			}
			for (int i=0;i<listTxtBottom.size();i++){
				canvas.drawLine(MeasureUtils.getTextViewCenterX(listTxtTop.get(mCurrentIndex)),MeasureUtils.getTextViewCenterY(listTxtTop.get(mCurrentIndex)),
						MeasureUtils.getTextViewCenterX(listTxtBottom.get(i)),MeasureUtils.getTextViewCenterY(listTxtBottom.get(i)),mPaint);
			}
		}else{
			for (int i=0 ; i<listTxtBottom.size();i++){
				if (i != mCurrentIndex){
					canvas.drawLine(MeasureUtils.getTextViewCenterX(listTxtBottom.get(mCurrentIndex)),MeasureUtils.getTextViewCenterY(listTxtBottom.get(mCurrentIndex)),
							MeasureUtils.getTextViewCenterX(listTxtBottom.get(i)),MeasureUtils.getTextViewCenterY(listTxtBottom.get(i)),mPaint);
				}
			}
			for (int i=0;i<listTxtTop.size();i++){
				canvas.drawLine(MeasureUtils.getTextViewCenterX(listTxtBottom.get(mCurrentIndex)),MeasureUtils.getTextViewCenterY(listTxtBottom.get(mCurrentIndex)),
						MeasureUtils.getTextViewCenterX(listTxtTop.get(i)),MeasureUtils.getTextViewCenterY(listTxtTop.get(i)),mPaint);
			}
		}
		zoom = lerp(bias(zoom, smoothZoom, 0.05f), smoothZoom, 0.2f);
		smoothZoomX = clamp(0.5f * getWidth() / smoothZoom, smoothZoomX, getWidth() - 0.5f * getWidth() / smoothZoom);
		smoothZoomY = clamp(0.5f * getHeight() / smoothZoom, smoothZoomY, getHeight() - 0.5f * getHeight() / smoothZoom);

		zoomX = lerp(bias(zoomX, smoothZoomX, 0.1f), smoothZoomX, 0.35f);
		zoomY = lerp(bias(zoomY, smoothZoomY, 0.1f), smoothZoomY, 0.35f);
		if (zoom != smoothZoom && listener != null) {
			listener.onZooming(zoom, zoomX, zoomY);
		}

		final boolean animating = Math.abs(zoom - smoothZoom) > 0.0000001f
				|| Math.abs(zoomX - smoothZoomX) > 0.0000001f || Math.abs(zoomY - smoothZoomY) > 0.0000001f;

		// nothing to draw
		if (getChildCount() == 0) {
			return;
		}

		// prepare matrix
		m.setTranslate(0.5f * getWidth(), 0.5f * getHeight());
		m.preScale(zoom, zoom);
		m.preTranslate(-clamp(0.5f * getWidth() / zoom, zoomX, getWidth() - 0.5f * getWidth() / zoom),
				-clamp(0.5f * getHeight() / zoom, zoomY, getHeight() - 0.5f * getHeight() / zoom));

		// get view
		final View v = getChildAt(0);
		m.preTranslate(v.getLeft(), v.getTop());

		// get drawing cache if available
		if (animating && ch == null && isAnimationCacheEnabled()) {
			v.setDrawingCacheEnabled(true);
			ch = v.getDrawingCache();
		}

		// draw using cache while animating
		if (animating && isAnimationCacheEnabled() && ch != null) {
			p.setColor(0xffffffff);
			canvas.drawBitmap(ch, m, p);
		} else { // zoomed or cache unavailable
			ch = null;
			canvas.save();
			canvas.concat(m);
			v.draw(canvas);
			canvas.restore();
		}

		// draw minimap
		if (showMinimap) {
			if (miniMapHeight < 0) {
				miniMapHeight = getHeight() / 4;
			}

			canvas.translate(10.0f, 10.0f);

			p.setColor(0x80000000 | 0x00ffffff & miniMapColor);
			final float w = miniMapHeight * (float) getWidth() / getHeight();
			final float h = miniMapHeight;
			canvas.drawRect(0.0f, 0.0f, w, h, p);

			if (miniMapCaption != null && miniMapCaption.length() > 0) {
				p.setTextSize(miniMapCaptionSize);
				p.setColor(miniMapCaptionColor);
				p.setAntiAlias(true);
				canvas.drawText(miniMapCaption, 10.0f, 10.0f + miniMapCaptionSize, p);
				p.setAntiAlias(false);
			}

			p.setColor(0x80000000 | 0x00ffffff & miniMapColor);
			final float dx = w * zoomX / getWidth();
			final float dy = h * zoomY / getHeight();
			canvas.drawRect(dx - 0.5f * w / zoom, dy - 0.5f * h / zoom, dx + 0.5f * w / zoom, dy + 0.5f * h / zoom, p);
			canvas.translate(-10.0f, -10.0f);
		}

		// redraw
		// if (animating) {
		getRootView().invalidate();
		invalidate();
		super.dispatchDraw(canvas);

	}

	public int getKeyWordsIndex(LinkedList<CircleView> circleViews,String str){
		for (int i=0;i<circleViews.size();i++){
			if (circleViews.get(i).getText().toString().equals(str)){
				return i;
			}
		}
		return -1;
	}


}
