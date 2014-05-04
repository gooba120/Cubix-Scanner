package com.mikanisland.cubix;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.mikanisland.tabsswipe.ScanFragment;

public class VideoActivity extends Activity implements OnClickListener, SurfaceHolder.Callback {
    
	private final Handler handler = new Handler();
	private long expectedTime, actualTime;
	MediaRecorder recorder;
    SurfaceHolder holder;
    boolean recording = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        recorder = new MediaRecorder();
        initRecorder();
        setContentView(R.layout.activity_arduino);

        SurfaceView cameraView = (SurfaceView) findViewById(R.id.surface_camera);
        holder = cameraView.getHolder();
        holder.addCallback(this);

        cameraView.setClickable(true);
        cameraView.setOnClickListener(this);
    }

    private void initRecorder() {
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

        CamcorderProfile cpHigh = CamcorderProfile
                .get(CamcorderProfile.QUALITY_HIGH);
        recorder.setProfile(cpHigh);
        recorder.setOutputFile(Environment.getExternalStorageDirectory()
        		.getAbsolutePath() + "/cubix.mp4");
        recorder.setMaxDuration(20000); // 20 seconds
        recorder.setMaxFileSize(50000000); // Approximately 5 megabytes
    }

    private void prepareRecorder() {
        recorder.setPreviewDisplay(holder.getSurface());

        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            finish();
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
    }

    public void onClick(View v) {
    	System.out.println("On click");
    	writeData((byte)1);   // starting signal
		expectedTime = System.currentTimeMillis() + 10000;  // 10 seconds later
		
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
	//			initRecorder();
	//          prepareRecorder();
				recording = true;
				recorder.start();
			}
		}, 7500);   // adjusted delay 
    	
//        if (recording) {
//            recorder.stop();
//            recording = false;
//
//            // Let's initRecorder so we can record again
//            initRecorder();
//            prepareRecorder();
//        } else {
//            recording = true;
//            recorder.start();
//        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        prepareRecorder();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (recording) {
            recorder.stop();
            recording = false;
        }
        recorder.release();
        finish();
    }
    
    private void writeData(byte b) {
		OutputStream out = ScanFragment.getScanFrag().getBluetoothSocketOut();

		try {
			out.write(b);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void beginListenForData() {
		final InputStream in = ScanFragment.getScanFrag().getBluetoothSocketIn();
		final byte buffer[] = new byte[10]; 
		
	    Thread workerThread = new Thread(new Runnable() {
	        public void run() {
	        	try {
	        		while(in.available() == 0) {
		            	in.read(buffer, 0, 10);
		            	System.out.println("Response from Arduino: " + buffer);
		            	recorder.stop();
		            	recording = false;
		            	break;
	        		}
	        	}
	            catch(Exception e) {
	            	e.printStackTrace();
	            }
	        }
	    });

	    workerThread.start();
	    System.out.println("Begin Listening");
	}
}