package com.example.accelareactivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class AccelerometerActivity extends Activity implements SensorEventListener{

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private float gravity[] = new float[3];
	private Canvas mCanvas;
	private Paint mPaint;
	private SurfaceView sv1;
	private float AcceX = 0;
	private float AcceY = 0;
	private float AcceZ = 0;
	private int n = 0;
	private SurfaceHolder mSurfaceHolder;
	private StringBuffer str;
	 private List<Sensor> allSensors;
	 private Sensor s;
//	 private TextView show;
	 /**小球资源文件**/
	private Bitmap mbitmapBall;
	/**小球的坐标位置**/
	private float mPosX = 200;
	private float mPosY = 0;
	 /**每50帧刷新一次屏幕**/  
	public static final int TIME_IN_FRAME = 50; 
	private File targetFile ;
	private ImageButton start;
	private ImageButton show;
	private TextView t1;
	
	private String fileNameLast;
	 
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accelerometer);
//		setContentView(R.layout.activity_main);
		
		
		//横屏
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		sv1 = (SurfaceView)findViewById(R.id.sv1);
		mSurfaceHolder = sv1.getHolder();
		mSurfaceHolder.addCallback(new myHolder());
		mPaint = new Paint();
		mPaint.setStrokeWidth(2.0f);
		
		 MyClickListener myListener = new MyClickListener();
		start = (ImageButton)findViewById(R.id.start);
		start.setOnClickListener(myListener);
		show = (ImageButton)findViewById(R.id.show);
		show.setOnClickListener(myListener);
		mycalendar=Calendar.getInstance();
		t1 = (TextView)findViewById(R.id.t1);
		 /**加载小球资源**/
	    mbitmapBall = BitmapFactory.decodeResource(this.getResources(), R.drawable.qiu);
	    if(Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)){
			SDCardRoot = Environment.getExternalStorageDirectory().getPath() + File.separator;
		}else {
			Toast.makeText(ContextUtil.getInstance(), "SD卡不存在", Toast.LENGTH_SHORT).show();
		}
		 targetFile = new File(SDCardRoot+"Sensor");
		System.out.println("targetFile:" + targetFile);
		if(! targetFile.exists()){
				targetFile.mkdir();
		}
		 fileNameLast = targetFile.getAbsolutePath()+File.separator+fileName;
		 fileLast = new File(fileNameLast);
			if(!fileLast.exists()){
				try {
					fileLast.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Toast.makeText(getApplicationContext(), "File isn't exits.Now,create it.", 
						 Toast.LENGTH_LONG).show();
			}
		 
	 }
	 
	 private File  fileLast =null;
	private FileOutputStream fos =null;
	private Timer updateTime;
	private TimerTask tt =null;
	private boolean startYN = false;
	private Calendar mycalendar;
	
	 class MyClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.start:
				 if(!startYN){
					startYN = true;
					start.setBackgroundResource(R.drawable.stop);
					updateTime = new Timer("Acc");
				
					tt =new TimerTask() {
					
						@Override
						public void run() {
							
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									//实现x,y,z三个方向各自的加速度的曲线绘制，但是还是没实现满屏之后，清屏，重新绘制。
									/** 在这里加上线程安全锁 **/
								synchronized (mSurfaceHolder) {
									mCanvas = mSurfaceHolder.lockCanvas();
								    DrawCircle();
								    /** 绘制结束后解锁显示在屏幕上 **/
									mSurfaceHolder.unlockCanvasAndPost(mCanvas);
									
								}
									writeFiletoSdcard(AcceX,AcceY,AcceZ);
									
								}
								
							});
						}
					};			
						updateTime.scheduleAtFixedRate(tt, 0, 100);//隔0.1s更新一次
						//timer2=SystemClock.uptimeMillis();
					
					try {
						fos = new FileOutputStream(fileLast.getAbsolutePath(),true);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					}else{
						startYN = false;
						start.setBackgroundResource(R.drawable.start);
						try {
//							fos.write((mycalendar.getTime().toString()).getBytes());
						    byte []newLine="\r\n".getBytes();    
				            fos.write(newLine);
							fos.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						tt.cancel();
						updateTime.cancel();
					
					}
				break;
			case R.id.show:
				readFileFromSDcard(fileName2);
				break;
				default: 
					break;
			}
		}
		 
	 }
	 
	 private void DrawCircle() {
		 try{
				if(mCanvas != null ){
					
					t1.setText("t="+ timer1 + "  x=" + AcceX +"  y="+ AcceY+ "  z="+ AcceZ);
//					System.out.println("t1= "+ t1.toString());
					mPaint.setColor(Color.RED);
					mPaint.setTextSize(20);
					mCanvas.drawText("X方向曲线：" , 0, 130, mPaint);
					mCanvas.drawCircle(n, 150+AcceX,1 , mPaint);
					
					mPaint.setColor(Color.BLUE);
					mPaint.setTextSize(20);
					mCanvas.drawText("Y方向曲线：" , 0, 230, mPaint);
					mCanvas.drawCircle(n, 250+AcceY,1 , mPaint);
					
					mPaint.setColor(Color.GREEN);
					mPaint.setTextSize(20);
					mCanvas.drawText("Z方向曲线：" , 0, 330, mPaint);
					mCanvas.drawCircle(n, 350+AcceZ,1 , mPaint);
				
					n=n+1;//闅�涓偣鐢讳竴娆�
//					System.out.println("n = "+ n);
					 if(n >= getWindowManager().getDefaultDisplay().getWidth()){
							mSurfaceHolder.unlockCanvasAndPost(mCanvas);
							n = 0;
//							 mPaint = new Paint();  
//						     mPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));  
//						     mCanvas.drawPaint(mPaint);  
//						     mPaint.setXfermode(new PorterDuffXfermode(Mode.SRC));  
						    
							 mCanvas = mSurfaceHolder.lockCanvas(null);
							mCanvas.drawColor(Color.BLACK);
						}
				}
				
				
		 	}catch(Exception e){
				e.printStackTrace();
			}
			
			
		}
	

		@Override
		protected void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
			if(tt!=null){
				tt.cancel();
			}
			if(updateTime!=null){
				updateTime.cancel();
			}
			mSensorManager.unregisterListener(this);
		}


		@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			mSensorManager.registerListener(this, mSensor,mSensorManager.SENSOR_DELAY_NORMAL);
		}

	
	private void Draw() {
		   
//	    /**绘制游戏背景**/
//	    mCanvas.drawBitmap(mbitmapBg,0,0, mPaint);
	    /**绘制小球**/
	    mCanvas.drawBitmap(mbitmapBall, mPosX,mPosY, mPaint);
	    /**X轴 Y轴 Z轴的重力值**/
	    mPaint.setColor(Color.RED);
		mPaint.setTextSize(20);
	    mCanvas.drawText("X轴重力值 ：" + AcceX, 0, 40, mPaint);
	    mPaint.setColor(Color.GREEN);
		mPaint.setTextSize(20);
	    mCanvas.drawText("Y轴重力值 ：" + AcceY, 0, 80, mPaint);
	    mPaint.setColor(Color.BLUE);
		mPaint.setTextSize(20);
	    mCanvas.drawText("Z轴重力值 ：" + AcceZ, 0, 120, mPaint);
	}
	
	/** 控制游戏更新循环 **/
	private boolean mRunning = false;
	/**控制游戏循环**/
	private boolean mIsRunning = false;
	/**手机屏幕宽高**/
	private int mScreenWidth = 0;
	private int mScreenHeight = 0;
	
	/**小球资源文件越界区域**/
	private int mScreenBallWidth = 0;
	private int mScreenBallHeight = 0;
	private FileUtils fileUtils = new FileUtils();
	class myHolder implements  SurfaceHolder.Callback{

		
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			  /**开始游戏主循环线程**/
//		    mIsRunning = true;
//		    /**得到当前屏幕宽高**/
		    mScreenWidth =  getWindowManager().getDefaultDisplay().getWidth();//800
		    mScreenHeight =  getWindowManager().getDefaultDisplay().getHeight();//1217
//		    
//		    /**得到小球越界区域**/
////		    int height = mbitmapBall.getHeight();
//		    mScreenBallWidth = mScreenWidth - mbitmapBall.getWidth();
//		    mScreenBallHeight = mScreenHeight - mbitmapBall.getHeight();
			
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			 mIsRunning = false;
			
		}

//		@Override
//		public void run() {
//			while (mIsRunning) {
//				/** 取得更新游戏之前的时间 **/
//				long startTime = System.currentTimeMillis();
//
//				/** 在这里加上线程安全锁 **/
//				synchronized (mSurfaceHolder) {
//				    /** 拿到当前画布 然后锁定 **/
//				    mCanvas = mSurfaceHolder.lockCanvas();
//				    Draw();
//				    /** 绘制结束后解锁显示在屏幕上 **/
//				    mSurfaceHolder.unlockCanvasAndPost(mCanvas);
//				}
//
//				/** 取得更新游戏结束的时间 **/
//				long endTime = System.currentTimeMillis();
//
//				/** 计算出游戏一次更新的毫秒数 **/
//				int diffTime = (int) (endTime - startTime);
//
//				/** 确保每次更新时间为50帧 **/
//				while (diffTime <= TIME_IN_FRAME) {
//				    diffTime = (int) (System.currentTimeMillis() - startTime);
//				    /** 线程等待 **/
//				    Thread.yield();
//				}
//			}
//		}
		
	}
//	public static  String[] accFileNames = {"acc1.txt","acc2.txt","acc3.txt","acc4.txt","acc5.txt",
//			 "acc6.txt","acc7.txt","acc8.txt","acc9.txt","acc10.txt","acc11.txt","acc12.txt","acc13.txt",
//			 "acc14.txt","acc15.txt","acc16.txt","acc17.txt","acc18.txt","acc19.txt","acc20.txt"};
	private String fileName = "acc5.txt";
	private String SDCardRoot;
	public static long timer1=0;
	//文件保存进Sd卡
	public void writeFiletoSdcard( float fx,float fy,float fz) {
		try {
			//鎸囧畾淇濆瓨鏁版嵁鏃朵负杩藉姞鐨勬柟寮�
			timer1 =SystemClock.uptimeMillis();
//			fos.write(((("  x = " + AcceX + "  y = " + AcceY + "  z = "+ AcceZ).toString())).getBytes());
			fos.write((((" "+timer1 + " "+ AcceX + " " + AcceY + " "+ AcceZ).toString())).getBytes());

			byte []newLine="\r\n".getBytes();    
	        fos.write(newLine);
			fos.flush();
			}catch(Exception e) {
				e.printStackTrace( );
			}
	}
	 private String fileName2 = "acc1.txt";
	private String 	fileNameLast2  ;
	 String res = "";
	private FileInputStream fin;
	private String[] T;
	private String[] X ;
	private String[] Y ;
	private String[] Z ;
	private String txyz;
	private String[] xyz;
	private  byte[] buffer;
	private int count = 0;
	private  void readFileFromSDcard(String fileName){
		try {
			fileNameLast2 =  targetFile.getAbsolutePath()+File.separator+fileName;
			StringBuffer sb = new StringBuffer();
			File file = new File(fileNameLast2);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line ;
			
			//line = br.readLine()读取每行的数据
			while((line = br.readLine())!=null){
//				String space[] = line.split("\\s+");//正则表达式判断一个空格或多个空格
//				 txyz = line.split(" ");//str[]里存的就是截取空格之后的字符串
//				System.out.println("line.length: "+line.length());
				//以追加的方式把数据都放进StringBuffer里
					sb.append(line);
			}
//			System.out.println("sb:"+sb);
			//stringbuffer转换成string
			txyz = sb.toString();
			System.out.println("txyz :" + txyz);
//			System.out.println("sb.length"+sb.length());//996
			br.close();
//			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
        
	} 
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	private float linear_acceleration[]= new float[3];;
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		final float alpha = 0.8f;
//		//没有去处重力的影响
//		AcceX = event.values[0];
//		AcceY = event.values[1];
//		AcceZ = event.values[2];
		//去处重力影响的三轴数据
		// Isolate the force of gravity with the low-pass filter.
		  gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
		  gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
		  gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

		  // Remove the gravity contribution with the high-pass filter.
		  AcceX = event.values[0] - gravity[0];
		  AcceY = event.values[1] - gravity[1];
		  AcceZ = event.values[2] - gravity[2];
	
//		  //这里乘以2是为了让小球移动的更快
//	    mPosX -= AcceX * 2;
//	    mPosY += AcceY * 2;
//
//	    //检测小球是否超出边界
//	    if (mPosX < 0) {
//	    	mPosX = 0;
//	    } else if (mPosX > mScreenBallWidth) {
//	    	mPosX = mScreenBallWidth;
//	    }
//	    if (mPosY < 0) {
//	    	mPosY = 0;
//	    } else if (mPosY > mScreenBallHeight) {
//	    	mPosY = mScreenBallHeight;
//	    }
	    
	}

	
	

}

	
