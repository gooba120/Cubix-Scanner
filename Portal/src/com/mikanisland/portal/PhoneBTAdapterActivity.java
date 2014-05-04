//package com.mikanisland.portal;
//
//import android.app.Activity;
//import android.hardware.usb.UsbDevice;
//import android.hardware.usb.UsbDeviceConnection;
//import android.hardware.usb.UsbManager;
//import android.os.Bundle;
//
//public class PhoneBTAdapterActivity extends Activity {
//	
//	private UsbManager usbManager;
//	private UsbDevice computer;
//	private UsbDeviceConnection connection;
//	
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super(savedInstanceState);
////		setContentView(R.layout.);
//		
//		usbManager = (UsbManager)getSystemService(USB_SERVICE);
//		
//		String deviceName = (String)usbManager.getDeviceList().keySet().toArray()[0];
//		connection = usbManager.openDevice(usbManager.getDeviceList().get(deviceName));
//		
//		connection.bulkTransfer(endpoint, buffer, length, timeout);
//	}
//	
//}