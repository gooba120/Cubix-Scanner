package com.mikanisland.opengl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Environment;

public class VisualizationSurface extends GLSurfaceView {
	private VisualizationRenderer renderer;
	
	public VisualizationSurface(Context ctx) {
		super(ctx);
		
		setEGLContextClientVersion(2);
		setPreserveEGLContextOnPause(true);
		
		 // TODO: create an input stream that points to the file that should be loaded
		InputStream input = null;
		
		try {
			input = new FileInputStream(new File(Environment.getExternalStorageDirectory(), "bundle.out"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		setRenderer(renderer = new VisualizationRenderer(ctx, input));
		setRenderMode(RENDERMODE_CONTINUOUSLY);
	}
	
	public VisualizationRenderer getRenderer() {
		return renderer;
	}
}
