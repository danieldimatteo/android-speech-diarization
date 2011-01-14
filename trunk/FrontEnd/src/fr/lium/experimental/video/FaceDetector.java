package fr.lium.experimental.video;

import static name.audet.samuel.javacv.jna.cxcore.*;
import static name.audet.samuel.javacv.jna.cv.*;

class FaceDetector {
	protected
	CvHaarClassifierCascade cascade;
	CvMemStorage storage;
	String cascadeName;
	CvSize size;
	public
	FaceDetector() {
		cascade = null;
		storage = null;
		size = null;
	}

	FaceDetector(String _cascadeName, CvSize _size) {
		cascade = null;
		storage = null;
		size = null;
		System.err.println( "FaceDetector::initialize() :"+_cascadeName);
		cascadeName = _cascadeName;
		size = _size;
		if (size == null) {
			throw new NullPointerException( "FaceDetector::initialize() (0)");
		}
		cascade = cvLoadHaarClassifierCascade (cascadeName, size.byValue());
		if (cascade == null) {
			throw new NullPointerException( "FaceDetector::initialize() (1)");
		}
		storage = cvCreateMemStorage(0);
		if (storage == null) {
			throw new NullPointerException( "FaceDetector::initialize() (2)");
		}

	}

	CvSeq detection(IplImage  image){
		//System.err.println( "FaceDetector::detection() :"+cascadeName << endl;
		return cvHaarDetectObjects (image, cascade, storage,
				1.1, 10, CV_HAAR_DO_CANNY_PRUNING,
				cvSize (0, 0));
	}

}