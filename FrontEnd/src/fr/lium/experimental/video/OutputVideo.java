package fr.lium.experimental.video;

import static name.audet.samuel.javacv.jna.cxcore.*;
import name.audet.samuel.javacv.OpenCVFrameRecorder;

public class OutputVideo{
	protected
	OpenCVFrameRecorder writer;
	int frameRate;
	CvSize size;
	String filename;
	IplImage frame;
	int currentFrameIndex;
	int depth;
	CvFont font;
	double hScale;
	double vScale;
	int lineWidth;

	public
	OutputVideo(String _filename, int _frameRate, CvSize _size){
		hScale=.5;
		vScale=.5;
		lineWidth=1;
		font = new CvFont(0, 0.5, 0);

		filename = _filename;
		frameRate = _frameRate;
		size = _size;
		currentFrameIndex = -1;
		frame = null;
		writer = new OpenCVFrameRecorder(filename, size.width, size.height);
		//cvCreateVideoWriter(filename,CV_FOURCC('X','V','I','D'), frameRate, size, 1);
		if (writer == null) {
			System.err.println( "OutputVideo::OutputVideo()" );
		}
	}

	void putFrame(IplImage  srcFrame) {
		frame = cvCloneImage(srcFrame);
		currentFrameIndex++;
	}

	void drawText(double val , CvPoint position, int color){
		//cvPutText(frame, val, position, font, getColor(color));
	}	

	void drawText(String text , CvPoint position, int color){
		//cvPutText(frame, text, position, font, getColor(color));
	}	

	CvScalar getColor(int color) {
		CvScalar colorRGV = CV_RGB(0,0,0);
		if (color == 1) {
			colorRGV = CV_RGB(255,0,0);
		} else if (color == 2) {
			colorRGV = CV_RGB(0,255,0);
		} else if (color == 3) {
			colorRGV = CV_RGB(0,0,255);
		} else if (color == 4) {
			colorRGV = CV_RGB(0,255,255);
		} else if (color == 5) {
			colorRGV = CV_RGB(255,0,255);
		} else if (color == 5) {
			colorRGV = CV_RGB(255,255,0);
		}
		return colorRGV;
	}

	void drawRect(CvRect r, int color) {
		/*CvPoint pt1, pt2;
		pt1.x = r.x;
		pt2.x = r.x+r.width;
		pt1.y = r.y;
		pt2.y = r.y+r.height;
		*/
		// Draw the rectangle in the input image
		//cvRectangle(frame, pt1, pt2, getColor(color), 3, 8, 0 );
	}

	void drawCircle(CvRect r, int color) {
		/*CvPoint center;
		int radius;
		center.x = cvRound(r.width*0.5 + r.x );
		center.y = cvRound(r.y + r.height*0.5);
		radius = cvRound((r.width + r.height)*0.25);
		cvCircle (frame, center, radius, getColor(color), 1, 8, 0 );*/
	}


	boolean writeFrame() throws Exception{
		if (frame == null){
			System.err.println( "OutputVideo::writeFrame()" );
			return false;
		}
		writer.record(frame);
		return true;
	}

	/*boolean writeOnScreen(String name){
		if (frame == null){
			System.err.println( "OutputVideo::writeFrame()" );
			return false;
		}
		cvShowImage (name, frame);
		return true;	
	}*/
};

