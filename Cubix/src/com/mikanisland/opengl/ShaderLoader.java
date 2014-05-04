package com.mikanisland.opengl;

import static android.opengl.GLES20.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

public class ShaderLoader {
	public static int load(InputStream v, InputStream f) {
		int vShader = glCreateShader(GL_VERTEX_SHADER);
		int fShader = glCreateShader(GL_FRAGMENT_SHADER);
		
		glShaderSource(vShader, getSource(v));
		glShaderSource(fShader, getSource(f));
		
		glCompileShader(vShader);
		glCompileShader(fShader);
		
		Log.i("SHADER", glGetShaderInfoLog(vShader));
		Log.i("SHADER", glGetShaderInfoLog(fShader));
		
		int program = glCreateProgram();
		
		glAttachShader(program, vShader);
		glAttachShader(program, fShader);
		
		glValidateProgram(program);
		
		glLinkProgram(program);
		
		Log.i("PROGRAM", glGetProgramInfoLog(program));
		
		glDeleteShader(vShader);
		glDeleteShader(fShader);
		
		return program;
	}
	
	private static String getSource(InputStream f) {
		StringBuilder sb = new StringBuilder();

		BufferedReader in = new BufferedReader(new InputStreamReader(f));
		
		try {
			String line;
			while ((line = in.readLine())!=null)
				sb.append(line + '\n');
		} catch (IOException e) {
			Log.e("ERROR@getSource()",e.getMessage());
		}
		
		return sb.toString();
	}
}
