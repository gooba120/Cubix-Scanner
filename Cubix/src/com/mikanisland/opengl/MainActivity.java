package com.mikanisland.opengl;

import com.mikanisland.cubix.R;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.view.Menu;
import android.view.MotionEvent;

public class MainActivity extends Activity {
	private VisualizationRenderer renderer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initGL();
	}
	
	private void initGL() {
		VisualizationSurface surface = new VisualizationSurface(this);
		setContentView(surface);
		
		renderer = surface.getRenderer();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	
	private float zoomGestureStart, zoomGestureLast;
	private boolean inZoom;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = MotionEventCompat.getActionMasked(event);
		
		if (action == MotionEvent.ACTION_MOVE) {
			if (event.getPointerCount() == 1) {
				if (inZoom) {
					inZoom = false;
					renderer.finishZoom(zoomGestureStart/zoomGestureLast);
				}
				
				// gesture rotate
				for (int i = 0; i < event.getHistorySize(); i++) {
					float x1, y1;
					float x0, y0;
					
					if (i == event.getHistorySize()-1) {
						x1 = event.getAxisValue(0);
						y1 = event.getAxisValue(1);
					} else {
						x1 = event.getHistoricalAxisValue(0, i+1);
						y1 = event.getHistoricalAxisValue(1, i+1);
					}
					
					x0 = event.getHistoricalAxisValue(0, i);				
					y0 = event.getHistoricalAxisValue(1, i);
					
					renderer.gestureRotate(x1-x0, y1-y0, 0.01f);
				}
			} else {
				// gesture zoom
				if (!inZoom) {
					inZoom = true;
					zoomGestureStart = (float)Math.hypot(
							event.getHistoricalAxisValue(0, 0, 0)-event.getHistoricalAxisValue(0, 1, 0),
							event.getHistoricalAxisValue(1, 0, 0)-event.getHistoricalAxisValue(1, 1, 0));
				} else {
					zoomGestureLast = (float)Math.hypot(
							event.getAxisValue(0, 0)-event.getAxisValue(0, 1),
							event.getAxisValue(1, 0)-event.getAxisValue(1, 1));
					
					float factor = zoomGestureStart/zoomGestureLast;
					renderer.gestureZoom(factor);
				}
			}
		} else if (action == MotionEvent.ACTION_UP) {
			if (inZoom){
				inZoom = false;
				renderer.finishZoom(zoomGestureStart/zoomGestureLast);
			}
		}
		
		return super.onTouchEvent(event);
	}
}
