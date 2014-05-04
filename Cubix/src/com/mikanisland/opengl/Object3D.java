package com.mikanisland.opengl;

import static android.opengl.GLES20.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

public class Object3D {
	private int objectBuffer;
	private int bufferCapacity; // in vertices
	private int vertexCount;
	private int currentMode;
	private float[] objectData;
	
	// each point has {x,y,z} (3D location) and {s,t} (texture mapping) all floats
	
	public Object3D() {
		IntBuffer buf = IntBuffer.allocate(1);
		glGenBuffers(1, buf);
		objectBuffer = buf.get();
		currentMode = GL_POINTS;
		objectData = null;
		vertexCount = 0;
		bufferCapacity = -1;
	}
	
	public void render(float[] proj, float[] view) {
		glUseProgram(ShaderVisualization.program);
		
		glUniform1f(ShaderVisualization.useTexture, 0);
		glUniformMatrix4fv(ShaderVisualization.proj, 1, false, proj, 0);
		glUniformMatrix4fv(ShaderVisualization.view, 1, false, view, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, objectBuffer);
		
		glEnableVertexAttribArray(ShaderVisualization.vertex);
		glEnableVertexAttribArray(ShaderVisualization.texCoord);

		
		
		glVertexAttribPointer(ShaderVisualization.vertex, 3, GL_FLOAT, false, 20, 0);
		glVertexAttribPointer(ShaderVisualization.texCoord, 2, GL_FLOAT, false, 20, 12);
		
		glDrawArrays(currentMode, 0, vertexCount);

		glDisableVertexAttribArray(ShaderVisualization.texCoord);
		glDisableVertexAttribArray(ShaderVisualization.vertex);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		glUseProgram(0);
	}
	
	public void addPoint(float x, float y, float z, float s, float t) {
		if (objectData == null) {
			bufferCapacity = 100;
			objectData = new float[5*bufferCapacity];
			
			objectData[0] = x;
			objectData[1] = y;
			objectData[2] = z;
			objectData[3] = s;
			objectData[4] = t;
			vertexCount = 1;
		} else {
			if (vertexCount == bufferCapacity) {
				bufferCapacity *= 2;
				float[] oldData = objectData;
				objectData = Arrays.copyOf(oldData, bufferCapacity*5);
				
			}
			
			objectData[vertexCount*5+0] = x;
			objectData[vertexCount*5+1] = y;
			objectData[vertexCount*5+2] = z;
			objectData[vertexCount*5+3] = s;
			objectData[vertexCount*5+4] = t;
			vertexCount++;
		}
	}
	
	
	public void updateBuffer() {
		glDeleteBuffers(1, new int[] {objectBuffer}, 0);
		
		objectBuffer = genBuffer();
		
		glBindBuffer(GL_ARRAY_BUFFER, objectBuffer);
		glBufferData(GL_ARRAY_BUFFER, objectData.length*4, asFloatBuffer(objectData), GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	private int genBuffer() {
		IntBuffer buf = IntBuffer.allocate(1);
		glGenBuffers(1, buf);
		return buf.get();
	}
	
	private FloatBuffer asFloatBuffer(float[] f) {
		FloatBuffer fb = FloatBuffer.allocate(f.length);
		fb.put(f);
		fb.flip();
		return fb;
	}
}
