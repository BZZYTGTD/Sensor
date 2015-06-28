package com.example.accelareactivity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.TextView;

public class GetTypeofSensor extends Activity{
	

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
	
		private StringBuffer str;
		 private List<Sensor> allSensors;
		 private Sensor s;
		 private TextView show;
		 @Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
			mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
//			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			show = (TextView)findViewById(R.id.show);
			allSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);// 获得传感器列表
			str = new StringBuffer();
			show();
			Timer timer = new Timer();
	        TimerTask task = new TimerTask(){
	         @Override
	         public void run() {
	         Intent intent = new Intent(GetTypeofSensor.this, AccelerometerActivity.class); 
	         startActivity(intent);
	         GetTypeofSensor.this.finish();
	         }
	        };
	        timer.schedule(task, 1000);
	    
		 }

		private void show() {
			  str.append("该手机有" + allSensors.size() + "个传感器,分别是:\n");
			   for (int i = 0; i < allSensors.size(); i++) {
			    s = allSensors.get(i);
			   
			    switch (s.getType()) {
			    case Sensor.TYPE_ACCELEROMETER:
			     str.append(i + "加速度传感器"+ "\n");
			     break;
			    case Sensor.TYPE_GYROSCOPE:
			     str.append(i + "陀螺仪传感器"+ "\n");
			     break;
			    case Sensor.TYPE_LIGHT:
			     str.append(i + "环境光线传感器"+ "\n");
			     break;
			    case Sensor.TYPE_MAGNETIC_FIELD:
			     str.append(i + "电磁场传感器"+ "\n");
			     break;
			    case Sensor.TYPE_ORIENTATION:
			     str.append(i + "方向传感器"+ "\n");
			     break;
			    case Sensor.TYPE_PRESSURE:
			     str.append(i + "压力传感器"+ "\n");
			     break;
			    case Sensor.TYPE_PROXIMITY:
			     str.append(i + "距离传感器"+ "\n");
			     break;
			    case Sensor.TYPE_TEMPERATURE:
			     str.append(i + "温度传感器"+ "\n");
			     break;
			    default:
			     str.append(i + "未知传感器"+ "\n");
			     break;
			    }
			    str.append("设备名称:" + s.getName() + "\n");
			    str.append("设备版本:" + s.getVersion() + "\n");
			    str.append("通用类型号:" + s.getType() + "\n");
			    str.append("设备商名称:" + s.getVendor() + "\n");
			    str.append("传感器功耗:" + s.getPower() + "\n");
			    str.append("传感器分辨率:" + s.getResolution() + "\n");
			    str.append("传感器最大量程:" + s.getMaximumRange() + "\n");
			   }
			   show.setText(str);
		}
}
