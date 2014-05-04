package com.mikanisland.opencv;

import java.io.File;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;


public class VideoParser {
	private String pathFormat;
	private int frameNumber;
	
	public VideoParser(String format) {
		frameNumber = 1;
		pathFormat = format;
	}
	
	public boolean nextFrame(Mat m) {
		File f = new File(String.format(pathFormat, frameNumber));
		if (!f.exists())
			return false;
		
		Mat m0 = Highgui.imread(String.format(pathFormat, frameNumber));
		
		m0.assignTo(m);
		
		frameNumber++;
		return true;
	}
}
