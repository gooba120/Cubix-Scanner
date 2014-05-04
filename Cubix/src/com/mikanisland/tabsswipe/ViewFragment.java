package com.mikanisland.tabsswipe;

import com.mikanisland.opengl.Object3DRotateActivity;
import com.mikanisland.opengl.VisualizationSurface;
import com.mikanisland.cubix.R;
import com.mikanisland.opengl.VisualizationRenderer;
//import com.maximusdev.games.ttr.GSGLSurfaceView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.opengl.GLSurfaceView;

public class ViewFragment extends Fragment  {

	private static ViewFragment viewFrag;
	private VisualizationRenderer renderer;
	private float zoomGestureStart, zoomGestureLast;
	private boolean inZoom;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		viewFrag = this;

		View rootView = inflater.inflate(R.layout.fragment_view, container, false);
		
		Button b = (Button)rootView.findViewById(R.id.view_object_button);

		b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), Object3DRotateActivity.class);
				startActivity(intent);
			}
		});
		
		return rootView;
	}

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
			return true;
		} else if (action == MotionEvent.ACTION_UP) {
			if (inZoom){
				inZoom = false;
				renderer.finishZoom(zoomGestureStart/zoomGestureLast);
			}
			return true;
		} else
			return false;
	}
	
	public static ViewFragment getViewFrag() {
		return viewFrag;
	}
}
