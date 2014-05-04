package com.mikanisland.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class VisualizationSurface extends GLSurfaceView {
	private VisualizationRenderer renderer;
	
	public VisualizationSurface(Context ctx) {
		super(ctx);
		
		setEGLContextClientVersion(2);
		setPreserveEGLContextOnPause(true);
		
		setRenderer(renderer = new VisualizationRenderer(ctx));
		setRenderMode(RENDERMODE_CONTINUOUSLY);
	}
	
	public VisualizationRenderer getRenderer() {
		return renderer;
	}
}
