package com.mikanisland.opengl;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetManager;

import static android.opengl.GLES20.*;

public class DefaultProgram {
	public static int program;
	
	public static int view, proj;
	public static int vertex, color;
	
	public static void init(Context ctx) {
		AssetManager assets = ctx.getAssets();
		
		try {
			program = ShaderLoader.load(assets.open("Default.v"), assets.open("Default.f"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		view = glGetUniformLocation(program, "view");
		proj = glGetUniformLocation(program, "proj");
		
//		vertex = glGetAttribLocation(program, "vertex");
//		color = glGetAttribLocation(program, "color");
		glBindAttribLocation(program, vertex=0, "vertex");
		glBindAttribLocation(program, color=1, "color");
		
		glLinkProgram(program);
	}
}
