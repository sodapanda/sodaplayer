package info.sodapanda.sodaplayer.views;


import info.sodapanda.sodaplayer.pojo.MuchGift;
import info.sodapanda.sodaplayer.views.data.GiftPoint;
import info.sodapanda.sodaplayer.views.data.MoveablePoint;
import info.sodapanda.sodaplayer.views.data.PointsInfo;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;

public class GiftAnimSurfaceView extends SurfaceView implements Callback{
	Canvas canvas;
	SurfaceHolder surfaceHolder;
	Paint paint;
	DrawThread drawThread;
	private float scale;
	private LinkedBlockingDeque<MuchGift> gift_queue;
	
	public GiftAnimSurfaceView(Context context) {
		super(context);
		init();
	}

	public GiftAnimSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GiftAnimSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		setZOrderOnTop(true);
		paint = new Paint();
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		float width = display.getWidth();
		scale = width/800f;
		Log.i("dudu","宽度:"+width+"比例"+scale);
		gift_queue = new LinkedBlockingDeque<MuchGift>(200);
		startAnim();
	}
	
	public void addAnim(MuchGift muchgift){
		try {
			gift_queue.put(muchgift);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void startAnim(){
		new DrawThread("MuchGiftThread").start();
	}
	
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i("dudu", "holder started");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
	
	class DrawThread extends Thread{
		public DrawThread(String name){
			super(name);
		}
		
		@Override
		public void run() {
			Bitmap image=null;
			PointsInfo info=null;
			while(true){
				Log.i("dudu","大量图片的循环");
				try {
					MuchGift thisgift = gift_queue.take();
					image = thisgift.getImg();
					info = thisgift.getInfo();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				if(image == null){//规定传进来的gift_img为null的话就停止了
					Log.i("dudu","显示图片的线程退出了");
					return;
				}
				ArrayList<GiftPoint> pointlist= info.getArray();
				ArrayList<MoveablePoint> movePointList = new ArrayList<MoveablePoint>();
				
				Random random = new Random();
				for(int i=0;i<pointlist.size();i++){
					MoveablePoint mPoint = new MoveablePoint(new GiftPoint(random.nextInt(800), random.nextInt(480)), pointlist.get(i));
					movePointList.add(mPoint);
				}
				
				float durian = 150f;
				try {
					for (int i = 0; i < durian;) {
						canvas = surfaceHolder.lockCanvas(null);
						canvas.scale(scale, scale);
						paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
						canvas.drawPaint(paint);
						paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
	
						for (int j = 0; j < movePointList.size(); j++) {
							int[] location = new int[2];
							location = getLocationByTime(movePointList.get(j), i,durian);
							if (i != durian - 1) {
								canvas.drawBitmap(image, location[0],location[1], null);
	
							} else {
								canvas.drawBitmap(image,movePointList.get(j).getEnd_point().getX(), movePointList.get(j).getEnd_point().getY(),null);
							}
						}
						surfaceHolder.unlockCanvasAndPost(canvas);
						i = i + 1;
					}
					Thread.sleep(1000);
					canvas = surfaceHolder.lockCanvas(null);
					canvas.scale(scale, scale);
					paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
					canvas.drawPaint(paint);
					paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
					surfaceHolder.unlockCanvasAndPost(canvas);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private int[] getLocationByTime(MoveablePoint point,int time,float duran){
		int[] location = new int[2];
		GiftPoint start = point.getStart_point();
		GiftPoint end = point.getEnd_point();
		
		double xSpeed = (end.getX()-start.getX())/duran;
		double ySpeed = (end.getY()-start.getY())/duran;
		
		int x = (int) (start.getX()+xSpeed*time);
		int y = (int) (start.getY()+ySpeed*time);
		
		location[0] = x;
		location[1] = y;
		
		return location;
	}
	
	@Override
	protected void onDetachedFromWindow() {
		try {
			gift_queue.putFirst(new MuchGift(null, null));
			Log.i("dudu","队列放入空");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.onDetachedFromWindow();
	}
}
