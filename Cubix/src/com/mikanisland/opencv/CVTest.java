package com.mikanisland.opencv;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import android.util.Log;

public class CVTest {
	
	public static void test(String fileToOpen, String fileToSave) {
		Mat m = Highgui.imread(fileToOpen);
		
		Log.i("OCV", fileToOpen);
		Log.i("OCV", String.format("%d,%d", m.rows(), m.cols()));
		
		Mat m1 = new Mat(m.rows(), m.cols(), m.type());
		Imgproc.cvtColor(m, m1, Imgproc.COLOR_RGB2GRAY);
		
		Highgui.imwrite(fileToSave, m1);
	}
}
