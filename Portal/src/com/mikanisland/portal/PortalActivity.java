package com.mikanisland.portal;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;

public class PortalActivity extends Activity implements SensorEventListener {

	private final int BT_CODE = 1;
	private final byte BT_MAC_ADDR[] = {0x00, 0x14, 0x01, 0x09, 0x16, 0x02};
	private BluetoothAdapter BTAdapter;
	private BluetoothDevice arduino;
	private BluetoothSocket BTSocket;
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private OutputStream BTOut;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_portal);
		
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		
		enableBT();
		try {
			BTOut = BTSocket.getOutputStream();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		Button up = (Button)findViewById(R.id.up);
		Button down = (Button)findViewById(R.id.down);
		Button left = (Button)findViewById(R.id.left);
		Button right = (Button)findViewById(R.id.right);
		
		if ((accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)) != null) {
			sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		int angle = (int)Math.toDegrees(Math.atan(event.values[1] / event.values[0]));
		if(angle >= 0)
			angle = 90 - angle;
		else
			angle = -90 - angle;
		
//		System.out.println("X: " + event.values[0] + ", Y: " + event.values[1] + ", Z: " + event.values[2]);
		System.out.println("Angle: " + angle);
		try {
			BTOut.write(angle);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	
	public void enableBT() {
		BTAdapter = BluetoothAdapter.getDefaultAdapter();
		if (BTAdapter == null) {
		    // Device does not support Bluetooth
			System.out.println("No Bluetooth on device!!!");
		}
		else {
			if(!BTAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    startActivityForResult(enableBtIntent, BT_CODE);
			}
			else {
				arduino = BTAdapter.getRemoteDevice(BT_MAC_ADDR);
				Log.d("", "Connecting to ... " + arduino);
				BTAdapter.cancelDiscovery();
				try {
					Method m = arduino.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
					BTSocket = (BluetoothSocket) m.invoke(arduino, 1); 
					BTSocket.connect();
				}
				catch(Exception e) {
					try {
						BTSocket.close();
					}
					catch(Exception e2) {
						e2.printStackTrace();
					}
					e.printStackTrace();
				}
			}
		}
	}
	
	public InputStream getBTSocketInputStream() {
		try {
			return BTSocket.getInputStream();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public OutputStream getBTSocketOutputStream() {
		try {
			return BTSocket.getOutputStream();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.portal, menu);
		return true;
	}
}