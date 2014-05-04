package com.mikanisland.portal;
//package com.example.portal2;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.UUID;

import com.getpebble.android.kit.*;
import com.getpebble.android.kit.PebbleKit.*;
import com.getpebble.android.kit.util.PebbleDictionary;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Handler;

public class GetPebbleDataLog extends Activity {

	private final static UUID PEBBLE_APP_UUID = UUID.fromString("6c190493-ce9e-46bf-a7e7-08aa4ed1617d");
	private PebbleDataReceiver receiver = null;
	
	private final StringBuilder mDisplayText = new StringBuilder();
	@Override
	protected void onResume(){
		super.onResume();
		System.out.println("test");
		final Handler handler = new Handler();
		
		
		Button button1 = (Button)findViewById(R.id.button1);
		
		button1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PebbleKit.closeAppOnPebble(getApplicationContext(), PEBBLE_APP_UUID);		
			}
		});
		
		if(PebbleKit.isWatchConnected(getApplicationContext())){
			System.out.println("Pebble is connected.");
			PebbleKit.startAppOnPebble(getApplicationContext(), PEBBLE_APP_UUID);
			if (PebbleKit.areAppMessagesSupported(getApplicationContext())) {
				  Log.i(getLocalClassName(), "App Message is supported!");
				}
			else {
				  Log.i(getLocalClassName(), "App Message is not supported");
			}
			
			receiver = new PebbleKit.PebbleDataReceiver(PEBBLE_APP_UUID) {
				
			@Override
			public void receiveData(final Context context, final int transactionId, final PebbleDictionary data) {
				
				//System.out.println(data.getBytes(45));
				ByteBuffer b = ByteBuffer.wrap(data.getBytes(45)).order(ByteOrder.LITTLE_ENDIAN);
				ShortBuffer s = b.asShortBuffer();
				/*ByteArrayOutputStream accel_in = new ByteArrayOutputStream();
				DataOutputStream accel_out = new DataOutputStream(accel_in);
				
				try {
					accel_out.write(data.getBytes(45));
					accel_out.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
				while(s.hasRemaining()){
					int x = (s.get()/32);
					int y = (s.get()/32);
					int z = (s.get()/32);
					
					int direction = Math.sqrt((Math.pow(x,2)) + (Math.pow(y, 2)));
					if(x >= 0 && y >= 0){
						step right motor direction/200 times;
					}
					else if (x <= 0 && y >= 0){
						step left motor direction/200 times;
					}
					else if (x >= 0 && y <= 0){
						step right motor direction/-200 times;
					}
					else if (x <= 0 && y <= 0){
						step left motor direction/-200 times;
					}
					mDisplayText.append("x: " + (s.get()/32) + ", ");
					mDisplayText.append("y: " + (s.get()/32) + ", ");
					mDisplayText.append("z: " + (s.get()/32) + ", ");
					mDisplayText.append("\n");
				}
				
				handler.post(new Runnable(){
					@Override
					public void run(){
						updateUI();
						PebbleKit.sendAckToPebble(getApplicationContext(), transactionId);
					}
				});
			}};
			PebbleKit.registerReceivedDataHandler(this, receiver);
			PebbleKit.registerPebbleConnectedReceiver(this, receiver);
		}
		else{
			System.out.println("Pebble is not connected.");
		}
	}
	
	@Override
	public void onPause(){
		super.onPause();
		
		if(receiver != null){
			unregisterReceiver(receiver);
			receiver = null;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_pebble_data_log);
	}
	

private void updateUI(){
		TextView textView1 = (TextView) findViewById(R.id.textView1);
		textView1.setText(mDisplayText.toString());
	}

}
