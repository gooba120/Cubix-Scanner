package com.mikanisland.opengl;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetManager;

import static android.opengl.GLES20.*;

public class ShaderVisualization {
	public static int program;
	public static int proj, view;
	public static int vertex, texCoord;
	public static int useTexture;
	
	public static void init(Context ctx) {
		AssetManager a = ctx.getAssets();
		try {
			program = ShaderLoader.load(a.open("Visualization.v"), a.open("Visualization.f"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		proj = glGetUniformLocation(program, "proj");
		view = glGetUniformLocation(program, "view");
		useTexture = glGetUniformLocation(program, "useTexture");
		
		glBindAttribLocation(program, vertex=0, "vertex");
		glBindAttribLocation(program, texCoord=1, "texCoord");
		
		glLinkProgram(program);
	}
}
