package fr.lium.experimental.video;

import static name.audet.samuel.javacv.jna.cxcore.*;
import static name.audet.samuel.javacv.jna.cv.*;
import static name.audet.samuel.javacv.jna.highgui.*;

public class InputVideo {
	protected
	boolean trace;
	CvCapture camera;
	String name;
	int cameraFrameRate;
	int cameraFrameCount;
	IplImage frame;
	IplImage grayFrame;
	IplImage smallGrayFrame;
	int scale;
	int currentFrameIndex;
	public
	InputVideo(int _scale){
		trace = false;
		scale = 1;	
		camera = null;
		frame = null;
		grayFrame = null;
		smallGrayFrame = null;
		scale = _scale;
		currentFrameIndex=-1;
		cameraFrameRate = -1;
		cameraFrameCount = -1;
	}

	InputVideo(String name, int _scale){
		trace = false;
		scale = 1;	
		camera = null;
		frame = null;
		grayFrame = null;
		smallGrayFrame = null;
		scale = _scale;
		currentFrameIndex=-1;
		cameraFrameRate = -1;
		cameraFrameCount = -1;
		if (init(name) == false) {
			System.err.println("InputVideo::InputVideo()");
		};
	}

	boolean init(String name) {
		camera = cvCreateFileCapture(name);
		if (camera == null) {
			System.err.println("InputVideo::init()");
			return false;
		}
		
		cameraFrameRate =  (int) cvGetCaptureProperty(camera, CV_CAP_PROP_FPS);
		cameraFrameCount = (int) cvGetCaptureProperty(camera, CV_CAP_PROP_FRAME_COUNT);

		frame = cvQueryFrame (camera);
		currentFrameIndex = 0; 
		grayFrame = cvCreateImage(cvSize (frame.width, frame.height), IPL_DEPTH_8U, 1);
		smallGrayFrame = cvCreateImage(cvSize (frame.width / scale, frame.height / scale), IPL_DEPTH_8U, 1);

		return true;
	}

	int getCurrentFrameIndex(){
		return currentFrameIndex;
	}

	IplImage getCurrentFrame(){
		return frame;
	}

	int getFrameRate() {
		return cameraFrameRate;
	}

	int getFrameCount() {
		return cameraFrameCount;
	}

	CvSize getSize(){
		return cvGetSize(frame);
	}

	IplImage getSmallGrayFrame(){
		return smallGrayFrame;
	}
	
	IplImage getNextFrame(){
		if (camera == null){
			System.err.println("InputVideo::getNextFrame()");
			return null;
		}
		frame = cvQueryFrame (camera);
		//IplImage *  copy  = cvCloneImage(frame);; // the input image converted to HSV color mode
		//frames.push_back(*copy);
		currentFrameIndex++;
		if (currentFrameIndex >= getFrameCount()){
			return null;
		}
		cvCvtColor (frame, grayFrame, CV_BGR2GRAY);
		cvResize (grayFrame, smallGrayFrame, CV_INTER_LINEAR);

		return frame;
	}
}
