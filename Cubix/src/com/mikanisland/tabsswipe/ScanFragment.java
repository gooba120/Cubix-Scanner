package com.mikanisland.tabsswipe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import com.mikanisland.cubix.R;
import com.mikanisland.cubix.UpdateBluetoothStatusTask;
import com.mikanisland.cubix.VideoActivity;
import com.mikanisland.opencv.CVTest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ScanFragment extends Fragment {
	
	private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(getActivity()) {
		@Override
		public void onManagerConnected(int status) {
		   switch (status) {
		       case LoaderCallbackInterface.SUCCESS:
		       {
		      System.out.println("OpenCV loaded successfully");
		      // Create and set View
//		      setContentView(R.layout.main);
		       } break;
		       default:
		       {
		      super.onManagerConnected(status);
		       } break;
		   }
		    }
		};
	
	private static ScanFragment scanFrag;    // singleton
	private BluetoothAdapter m_BT;
	private BluetoothDevice arduino;
	private BluetoothSocket btSocket;
	private final byte BT_MAC_ADDR[] = {0x20, 0x13, 0x11, 0x14, 0x12, 0x9};
	private OutputStream out;
	private InputStream in;
	private byte buffer[] = new byte[10];
	private UpdateBluetoothStatusTask currTask;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		scanFrag = this;
		
		System.out.println("Trying to load OpenCV library");
	    if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_2, getActivity(), mOpenCVCallBack))
	    {
	    	System.out.println("Cannot connect to OpenCV Manager");
	    }
		
		
		final View rootView = inflater.inflate(R.layout.fragment_scan, container, false);
		final TextView status = (TextView)rootView.findViewById(R.id.bluetooth_status2);
		
		final ProgressBar progBar = (ProgressBar)rootView.findViewById(R.id.bluetooth_progress_bar);
		progBar.setVisibility(ProgressBar.INVISIBLE);
		
		// Bluetooth stuff
        updateBTStatus(rootView);
        
        final Button startScan = (Button)rootView.findViewById(R.id.start_scan_button);
        startScan.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(status.getText().toString().equals("Connected")) {
					Intent intent = new Intent(getActivity(), VideoActivity.class);
					startActivity(intent);
				}
				else
					Toast.makeText(getActivity(), "Connect your Bluetooth!", Toast.LENGTH_SHORT).show();
			}
		});
        
        final Button refreshB = (Button)rootView.findViewById(R.id.refresh);
        refreshB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(currTask != null)
					currTask.cancel(true);
				
				currTask = new UpdateBluetoothStatusTask(ScanFragment.this, progBar, rootView);
				currTask.execute();
//				
//				CVTest.test(Environment.getExternalStorageDirectory().getAbsolutePath()+"/CAM00070.jpg", 
//						Environment.getExternalStorageDirectory().getAbsolutePath()+"/OUTPUT.png");
			}
		});
		
		return rootView;
	}
	
	private void updateBTStatus(View rootView) {

		final TextView text = (TextView)rootView.findViewById(R.id.bluetooth_status2);
        if((checkBT() == 0) && (createSocket() == 0)) {
        	text.setText("Connected");
        	text.setTextColor(Color.GREEN);
        }
        else {
        	text.setText("Disconnected");
        	text.setTextColor(Color.RED);
        }
	}
	
	// success 0, error -1
	public int checkBT() {
		m_BT = BluetoothAdapter.getDefaultAdapter();
		
		return 0;
	}
	
	public int createSocket() {
		
		arduino = m_BT.getRemoteDevice(BT_MAC_ADDR);
		System.out.println("Connecting to ... " + arduino);
		m_BT.cancelDiscovery();
		try {
			Method m = arduino.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
			btSocket = (BluetoothSocket) m.invoke(arduino, 1); 
//				Method m = arduino.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] {int.class});
//	            btSocket = (BluetoothSocket)m.invoke(arduino, UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));                   
//	            btSocket = arduino.createRfcommSocketToServiceRecord(UUID.randomUUID());
			btSocket.connect();
			System.out.println("Connection made.");
			
			return 0;
		} 
		catch (Exception e) {
			try {
				btSocket.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public InputStream getBluetoothSocketIn() {
		try {
			in = btSocket.getInputStream();
		 } 
		 catch (IOException e) {
			 e.printStackTrace();
			 in = null;
		 }
		
		return in;
	}
	
	public OutputStream getBluetoothSocketOut() {
		try {
			out = btSocket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
			out = null;
		}
		
		return out;
	}
	
	public static ScanFragment getScanFrag() {
		return scanFrag;
	}
	
	public BluetoothSocket getBTSocket() {
		return btSocket;
	}
}
