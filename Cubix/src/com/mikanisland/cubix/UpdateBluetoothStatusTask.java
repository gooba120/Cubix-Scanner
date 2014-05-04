package com.mikanisland.cubix;

import com.mikanisland.tabsswipe.ScanFragment;

import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class UpdateBluetoothStatusTask extends AsyncTask<Void, Void, Integer> {
	
	private ScanFragment scanFrag;
	private ProgressBar progBar;
	private View rootView;
	
	public UpdateBluetoothStatusTask(ScanFragment sf, ProgressBar pb, View rv) {
		scanFrag = sf;
		progBar = pb;
		rootView = rv;
	}
	
	@Override
	public void onPreExecute() {
		progBar.setVisibility(ProgressBar.VISIBLE);
	}
	
	@Override
	protected Integer doInBackground(Void... urls) {
		System.out.println("Do in background");
		try {
			if(scanFrag.getBTSocket().isConnected())
				scanFrag.getBTSocket().close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		if((scanFrag.checkBT() == 0) && (scanFrag.createSocket() == 0)) {
        	System.out.println("Success");
        	return 0;   // success
        }
        else {
        	System.out.println("Failure");
        	return -1;
        }
    }

	@Override
    protected void onProgressUpdate(Void... progress) {}

	@Override
	protected void onCancelled() {
		System.out.println("Task cancelled");
		progBar.setVisibility(ProgressBar.INVISIBLE);
	}
	
	@Override
    protected void onPostExecute(Integer result) {
        progBar.setVisibility(ProgressBar.INVISIBLE);
        
        final TextView text = (TextView)rootView.findViewById(R.id.bluetooth_status2);
        if(result == 0) {
        	text.setText("Connected");
        	text.setTextColor(Color.GREEN);
        }
        else {
        	text.setText("Disconnected");
        	text.setTextColor(Color.RED);
        }
    }
	
}