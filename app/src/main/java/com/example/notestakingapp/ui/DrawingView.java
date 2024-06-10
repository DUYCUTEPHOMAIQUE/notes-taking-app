package com.example.notestakingapp.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
public class DrawingView extends View {
	public int mDefaultColor;
	private int width, height;
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Path mPath;
	private Paint mBitmapPaint;

	private Paint mPaint;
	Context context;
	private Paint circlePaint;
	private Path circlePath;

	public DrawingView(Context c) {
		super(c);
		context=c;
		mPath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		circlePaint = new Paint();
		circlePath = new Path();
		circlePaint.setAntiAlias(true);
		circlePaint.setColor(Color.BLUE);
		circlePaint.setStyle(Paint.Style.STROKE);
		circlePaint.setStrokeJoin(Paint.Join.MITER);
		circlePaint.setStrokeWidth(4f);
		setup();
		this.setDrawingCacheEnabled(true);
	}

	public DrawingView(Context c, AttributeSet attrs) {
		super(c, attrs);
		context=c;
		mPath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		circlePaint = new Paint();
		circlePath = new Path();
		circlePaint.setAntiAlias(true);
		circlePaint.setColor(Color.BLUE);
		circlePaint.setStyle(Paint.Style.STROKE);
		circlePaint.setStrokeJoin(Paint.Join.MITER);
		circlePaint.setStrokeWidth(4f);
		setup();
		this.setDrawingCacheEnabled(true);
	}

	void setup() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(Color.GREEN);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(12);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		mCanvas.drawColor(Color.WHITE);
	}

	public void drawBitmap(Bitmap bitmap) {
		mBitmap = Bitmap.createBitmap(bitmap);
		Bitmap mutableBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
		mCanvas = new Canvas(mutableBitmap);
		mCanvas.drawBitmap(bitmap, 0, 0, null);
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);

		canvas.drawPath( mPath,  mPaint);
		canvas.drawPath( circlePath,  circlePaint);
	}

	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;

	private void touch_start(float x, float y) {
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
			mX = x;
			mY = y;

			circlePath.reset();
			circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
		}
	}

	private void touch_up() {
		mPath.lineTo(mX, mY);
		circlePath.reset();
		mCanvas.drawPath(mPath,  mPaint);
		mPath.reset();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touch_start(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				touch_move(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				touch_up();
				invalidate();
				break;
		}
		return true;
	}

	public void clearCanvas() {
		onSizeChanged(width, height, width, height);
		invalidate();
	}
	public Bitmap getBitmap() {
		Bitmap newBitmap = mBitmap.copy(mBitmap.getConfig(), true);
		return newBitmap;
	}

	public void changeColor(int color) {
		String hexColor = String.format("#%06X", (0xFFFFFF & color));
		mPaint.setColor(Color.parseColor(hexColor));
	}
}