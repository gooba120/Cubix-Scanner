package com.mikanisland.opencv;

import java.util.Arrays;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class Test {
	public static VideoParser parser;
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		parser = new VideoParser("/home/me/Desktop/TEST/image-%03d.jpeg");
		
//		doAlgorithm();
		doFeatures();
//		doOutline();
	}

	private static void doOutline() {
		Mat[] frames = new Mat[2];
		for (int i = 0; i < frames.length; i++) {
			frames[i] = new Mat();
			parser.nextFrame(frames[i]);
		}
		
		Mat dif = differentiate(frames[0], frames[1]);
		
		normalize(dif);
		binarize(dif, 10);
		
		Highgui.imwrite("outline.png", dif);
	}
	
	private static void doFeatures() {
		FeatureDetector fd = FeatureDetector.create(FeatureDetector.ORB);
		
		Mat m0 = new Mat();
		Mat m1 = new Mat();
		Mat m2 = new Mat();

		parser.nextFrame(m0);
		parser.nextFrame(m1);
		parser.nextFrame(m2);
		
		int width = m0.cols();
		int height = m0.rows();
		
		Mat img0 = differentiate(m0, m1);
		Mat img1 = differentiate(m1, m2);
		
		Imgproc.morphologyEx(img0, m0, Imgproc.MORPH_OPEN, 
				Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5)));
		Imgproc.morphologyEx(img1, m1, Imgproc.MORPH_OPEN, 
				Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5)));
		
		img0 = m0;
		img1 = m1;
		
		MatOfKeyPoint kp0 = new MatOfKeyPoint();
		MatOfKeyPoint kp1 = new MatOfKeyPoint();
		
		fd.detect(img0, kp0);
		fd.detect(img1, kp1);
		
		DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
		DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
		
		Mat d0 = new Mat();
		Mat d1 = new Mat();
		extractor.compute(img0, kp0, d0);
		extractor.compute(img1, kp1, d1);
		
		MatOfDMatch matches = new MatOfDMatch();
		matcher.match(d0, d1, matches);
		
//		for (int r = 0; r < kp0.rows(); r++) {
//			for (int c = 0; c < kp0.cols(); c++) {
//				System.out.print(Arrays.toString(kp0.get(r,c))+", ");
//			}
//			
//			System.out.println();
//		}
		
		float f = 1/(1);
		
		Vector<double[]> angles = new Vector<double[]>();
		for (int i = 0; i < matches.rows(); i++) {
			double[] d = new double[4];
			d[0] = (kp0.get((int)matches.get(i, 0)[0],0)[0]-width/2)*f;
			d[2] = (kp1.get((int)matches.get(i, 0)[1],0)[0]-width/2)*f;
			
			d[1] = (kp0.get((int)matches.get(i, 0)[0],0)[1]-height/2)*f;
			d[3] = (kp1.get((int)matches.get(i, 0)[1],0)[1]-height/2)*f;
			
			angles.add(d);
		}
		
		Vector<double[]> positions = GetPos(angles);
//		for (int i = 0; i < angles.size(); i++) {
//			double x = angles.get(i)[0]/f + width/2;
//			double y = angles.get(i)[1]/f + height/2;
//			
//			positions.add(new double[] {x, y, 0});
//			
//
//			x = angles.get(i)[2]/f + width/2;
//			y = angles.get(i)[3]/f + height/2;
//			
//			positions.add(new double[] {x, y, 0});
//		}
		
		System.out.println();
	}
	private static double[] arcTanArray(double[] angles){
		for(int i = 0; i < angles.length; i++){
			angles[i] = Math.atan(angles[i]);
		}
		return angles;
	}
	
	
	private static double getAlpha(double[] angles){
		double Alpha = 0;
		angles = arcTanArray(angles);
		Alpha = Math.atan( (   Math.sin(D_alpha) / Math.tan(angles[2]) + Math.cos(D_alpha) - 1) / 
				           ((1 / Math.tan(angles[0])) - Math.cos(D_alpha) / Math.tan(angles[2]) + Math.sin(D_alpha))      );
		return Alpha;
	}
	
	private static double getR(double[] angles){
		double R = 0;
		double alpha = getAlpha(angles);
		angles = arcTanArray(angles);
		R = Math.sin(angles[1]) / (Math.sin(angles[1] + alpha));
		return R;
	}
	
	static double D_alpha = 0.005 /*(angular frequency) / (frame rate)*/;
	//static double H = 5;
	static int T = 0; //frame number
	
	
	public static Vector<double[]> GetPos(Vector<double[]> angles){ // I need the frame number T
		Vector<double[]> Pos = new Vector<double[]>();
		for(int i = 0; i < angles.size(); i++){
			double[] D = new double[3];
			
			double x = 0;
			double y = 0;
			double z = 0;
			double R = getR(angles.get(i));
			double Alpha = getAlpha(angles.get(i)) - T++ * D_alpha;
			
			
			x = R * Math.cos(Alpha);
			y = R * Math.sin(Alpha);
			z = angles.get(i)[1];
			
			D[0] = x;
			D[1] = y; 
			D[2] = z;
			Pos.add(D);
		}
		return Pos;
	}
	
	
	private static void doAlgorithm() {
		Mat[] frames = new Mat[2];
		frames[0] = new Mat();
		frames[1] = new Mat();
		
		parser.nextFrame(frames[0]);
		
		int current = 0;
		
		while (parser.nextFrame(frames[current = (current+1)%2])) {
			toGrayScale(frames[0]);
			toGrayScale(frames[1]);
			
			Mat dif = differentiate(frames[1-current], frames[current]);
			
			normalize(dif);
			binarize(dif, 0.3);

			Mat dst = new Mat();
			Imgproc.morphologyEx(dif, dst, Imgproc.MORPH_CLOSE, 
					Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5)));
			
			Highgui.imwrite("out0.png", dif);
			Highgui.imwrite("out1.png", dst);
			break;
		}
	}
	
	private static Mat differentiate(Mat img0, Mat img1) {
		Mat dif = new Mat(img0.rows(), img0.cols(), img0.type());
		
		for (int r = 0; r < dif.rows(); r++) {
			for (int c = 0; c < dif.cols(); c++) {
				double[] d0 = img0.get(r, c);
				double[] d1 = img1.get(r, c);
				
				double[] d = new double[d0.length];
				for (int i = 0; i < d.length; i++)
					d[i] = d1[i]-d0[i];
				
				dif.put(r, c, d);
			}
		}
		
		return dif;
	}
	
	private static void normalize(Mat img) {
		double maxSq = 0;
		
		for (int r = 0; r < img.rows(); r++) {
			for (int c = 0; c < img.cols(); c++) {
				double[] d = img.get(r, c);
				double sum = 0;
				for (int i = 0; i < d.length; i++)
					sum += d[i]*d[i];
				
				maxSq = Math.max(maxSq, sum);
			}
		}
		
		double l = Math.sqrt(maxSq);
		
		for (int r = 0; r < img.rows(); r++) {
			for (int c = 0; c < img.cols(); c++) {
				double[] d = img.get(r, c);
				for (int i = 0; i < d.length; i++)
					d[i] *= 255/l;
				img.put(r, c, d);
			}
		}
	}
	
	private static void binarize(Mat img, double threshold) {
		double thresholdSq = threshold*threshold;
		
		for (int r = 0; r < img.rows(); r++) {
			for (int c = 0; c < img.cols(); c++) {
				double[] d = img.get(r, c);
				double sum = 0;
				for (int i = 0; i < d.length; i++)
					sum += d[i]*d[i];
				
				if (sum < thresholdSq)
					Arrays.fill(d, 0);
				else
					Arrays.fill(d, 255);
				
				img.put(r, c, d);
			}
		}
	}
	
	private static void toGrayScale(Mat img) {
		Mat gray = new Mat();
		Imgproc.cvtColor(img, gray, Imgproc.COLOR_RGB2GRAY);
		gray.assignTo(img);
	}
}
