package com.mikanisland.opengl;

import java.io.InputStream;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.Matrix;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import static android.opengl.GLES20.*;

public class VisualizationRenderer implements GLSurfaceView.Renderer {

	private Context context;
	private float[] view, proj;
	
	private Object3D object;
	
	private float center;
	private float newCenter;
	
	public VisualizationRenderer(Context ctx, InputStream objLoadStream) {
		context = ctx;
		
		try {
			object = ObjectLoader.load(objLoadStream);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private float angle = 0;
	
	@Override
	public void onDrawFrame(GL10 gl) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		object.render(proj, view);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		Matrix.perspectiveM(proj, 0, 90, (float)width/height, 1, 1000);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		ShaderVisualization.init(context);
		newCenter = center = -5;
		
		GLES20.glClearColor(1,1,1,1);
		
		view = new float[16];
		proj = new float[16];
		
		Matrix.setIdentityM(view, 0);
		Matrix.translateM(view, 0, 0, 0, center);
		
		gestureRotate(0.4f, 0, 0.4f);
		gestureRotate(0, 0.4f, 0.4f);
		
		// object should be created in constructor
		// object = new Object3D();
		
		createCube();
	}
	
	private void createCube() {
		float s = 1;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				for (int z = 0; z < 3; z++) {
					if (y==0 || y==2 || x==0 || x==2 || z==0 || z==2) {
						object.addPoint(s*(x-1), s*(y-1), s*(z-1), 0, 0);
					}
				}
			}
		}
		object.updateBuffer();
	}
	
	public void gestureZoom(float factor) {
		float offset = center*factor - newCenter;
		newCenter += offset;
		
		float[] m = new float[] {
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, offset, 1
		};
		
		float[] temp = new float[16];
		Matrix.multiplyMM(temp, 0, m, 0, view, 0);
		view = temp;
	}
	
	public void finishZoom(float factor) {
		center = newCenter;
	}
	
	public void gestureRotate(float x, float y, float s) {
		float d = (float)Math.hypot(x,y);
		
		if (d == 0)
			return;
		
		float ct = (float) x/d;
		float st = (float) Math.sqrt(1-ct*ct);
		if (y > 0) st *= -1;
		
		float a = d*s;
		float ca = (float)Math.cos(a);
		float sa = (float)Math.sin(a);
		
		float[] t0 = new float[] {
			1, 0, 0, 0,
			0, 1, 0, 0,
			0, 0, 1, 0,
			0, 0, -center, 1
		};
		
		float[] t1 = new float[] {
			1, 0, 0, 0,
			0, 1, 0, 0, 
			0, 0, 1, 0,
			0, 0, center, 1
		};
		
		float[] m0 = new float[] {
			ct, -st, 0, 0,
			st, ct, 0, 0,
			0, 0, 1, 0,
			0, 0, 0, 1
		};
		
		float[] m1 = new float[] {
			ca, 0, -sa, 0,
			0, 1, 0, 0,
			sa, 0, ca, 0,
			0, 0, 0, 1,
		};
		
		float[] m2 = new float[] {
			ct, st, 0, 0,
			-st, ct, 0, 0,
			0, 0, 1, 0,
			0, 0, 0, 1
		};
		
		float[] temp = new float[16];
		
		// t1 * m2 * m1 * m0 * t0 * view
		
		Matrix.multiplyMM(temp, 0, t0, 0, view, 0);
		Matrix.multiplyMM(view, 0, m0, 0, temp, 0);
		Matrix.multiplyMM(temp, 0, m1, 0, view, 0);
		Matrix.multiplyMM(view, 0, m2, 0, temp, 0);
		Matrix.multiplyMM(temp, 0, t1, 0, view, 0);

		view = temp;
		
	}
}
